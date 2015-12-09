angular.module('ecommApp')
.controller('ShipmentGenerateController', ['$scope', '$rootScope', '$location', '$window', '$timeout', '$state', '$interval', 'toastr', 'orderService', 'Shop', 'Utils', 'Warehouse', 'courierService',
    function($scope, $rootScope, $location, $window, $timeout, $state, $interval, toastr, orderService, Shop, Utils, Warehouse, courierService) {

        var $ = angular.element;

        /** 如果编辑了任何信息，则切换数据时提示操作员［确定取消当前所有操作？］
         */
        $scope.isAnythingModified = false;

        $scope.courier = {};
        $scope.defaultQuery = {
            number: 0,
            size: 100,
            sort: ['internalCreateTime,desc'],
            warehouse: undefined,
            shop: undefined,
            statuses:[],
            shippingDescription: undefined,
            /** 是否显示出过发货单的订单
             */
            showDeployedOrders: false
        };
        $scope.query = angular.copy( $scope.defaultQuery );

        $scope.deliveryMethods =
        [
            { name:'快递', value:1 },
            { name:'自提', value:2 },
            { name:'送货上门', value:3 }
        ];

        $scope.shipments = [];
        $scope.orders = [];


        // 将所有店铺过滤，拿出所有配置了配送状态的店铺的ID
        $scope.selectAllShops = function(shops)
        {
            $.each(shops, function()
            {
                var shop = this;
                if ( shop.deployStep )
                {
                    var exist = false;
                    $.each($scope.query.statuses, function()
                    {
                        if ( this.id === shop.deployStep.id )
                        {
                            exist = true;
                            return false;
                        }
                    });
                    if ( ! exist )
                    {
                        $scope.query.statuses.push(shop.deployStep);
                    }
                }
            });
        };

        Warehouse.getAll({ // 导入所有仓库
            enabled: true,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有店铺
            return courierService.getAll({
                sort: ['name']
            }).then(function(couriers) {
                $scope.couriers = couriers;
            });
        }).then(function() { // 导入所有店铺
            return Shop.getAll({
                enabled: true,
                sort: ['name']
            }).then(function(shops) {
                $scope.shops = shops;
                $scope.query.shop = $scope.query.shop ? $scope.query.shop : $scope.shops[ 0 ];
                $scope.selectAllShops(shops);
                //console.log($scope.query.statuses);
            });
        }).then(function() {
            $scope.searchData();
        });

        $scope.searchData = function()
        {
            executeShipmentOperationReview('VERIFY' );
            $scope.isAnythingModified = false;
        };

        $scope.turnPage = function( number )
        {
            if ( number > -1 && number < $scope.page.totalPages )
            {
                $scope.query.number = number;
                $scope.searchData();
            }
        };

        $scope.searchBtn = function()
        {
            if( ! $scope.isAnythingModified )
            {
                $scope.searchData();
            }
            else
            {
                $('#cancelModifiedInfoWhilePagination').modal('show');
            }
        };

        $scope.search = function( e )
        {
            var keyCode = e.which || e.keyCode;
            if ( keyCode === 13 )
            {
                if( ! $scope.isAnythingModified )
                {
                    if( ! angular.isNumber( $scope.query.size ) || $scope.query.size < 1 )
                    {
                        $scope.query.size = 1;
                    }
                    // Do that thing you finally wanted to do
                    $scope.searchData();
                }
                else
                {
                    $('#cancelModifiedInfoWhilePagination').modal('show');
                }
            }
        };

        $scope.reset = function()
        {
            if( ! $scope.isAnythingModified )
            {
                $scope.query = angular.copy( $scope.defaultQuery );
                $scope.searchData();
            }
            else
            {
                $('#cancelModifiedInfoWhilePagination').modal('show');
            }
        };

        /** 锁定/解锁同品更名
         */
        $scope.shipmentJoinedLockedEditName = function( e, item, shipment )
        {
            var keyCode = e.which || e.keyCode;
            if ( keyCode === 13 )
            {
                var items = shipment.shipmentItems;
                for( var itemIndex in items )
                {
                    /* 如果连锁更名生效 */
                    if( items[ itemIndex ].isShipmentJoinedLockEditName )
                    {
                        if( ! items[ itemIndex ].holdName )
                        {
                            items[ itemIndex ].holdName = items[ itemIndex ].shortName ? items[ itemIndex ].shortName : items[ itemIndex ].fullName;
                        }
                        items[ itemIndex ].shortName = item.shortName;
                    }
                }
            }
        };

        /** hold住并更改详情简称
         */
        $scope.holdAndChangeItemShortName = function( item )
        {
            if( ! item.holdName )
            {
                item.holdName = item.shortName ? item.shortName : item.fullName;
            }
            $scope.isAnythingModified = true;
        };

        /** 更改详情数量，有做改动
         */
        $scope.changeItemQtyShippedBlur = function( item )
        {
            if( item.qtyShipped < 1 )
            {
                item.qtyShipped = 1;
            }
            else
            {
                item.qtyShipped = Math.floor( item.qtyShipped );
            }
            $scope.isAnythingModified = true;
        };

        /** 更改拆分数量，有做改动
         */
        $scope.changeItemQtySeparatedBlur = function( item )
        {
            if( item.qtySeparated < 1 )
            {
                item.qtySeparated = 1;
            }
            else if( item.qtySeparated > item.qtyShipped )
            {
                item.qtySeparated = item.qtyShipped;
            }
            else
            {
                item.qtySeparated = Math.floor( item.qtySeparated );
            }
        };

        /** 清空同品更名
         */
        $scope.emptyIsShipmentJoinedLockedEditName = function( shipment )
        {
            var items = shipment.shipmentItems;
            for( var itemIndex in items )
            {
                items[ itemIndex ].isShipmentJoinedLockEditName = false;
                items[ itemIndex ].shortName = items[ itemIndex ].holdName ? items[ itemIndex ].holdName : items[ itemIndex ].shortName;
            }
        };

        var itemIndex = 0;

        /** 分单
         */
        $scope.separateShipmentPrepare = function( shipment )
        {
            var isItemExistedAndQtyShippedGreaterThanOne = false;
            if( shipment.shipmentItems && shipment.shipmentItems.length > 1 )
            {
                isItemExistedAndQtyShippedGreaterThanOne = true;
            }
            else
            {
                var shipItems = shipment.shipmentItems;
                for( var shipItemIndex in shipItems )
                {
                    if( shipItems[ shipItemIndex].qtyShipped > 1 )
                    {
                        isItemExistedAndQtyShippedGreaterThanOne = true;
                        break;
                    }
                }
            }

            /* 分单准备之前要确保不止一件商品 */
            if( isItemExistedAndQtyShippedGreaterThanOne )
            {
                var items = shipment.shipmentItems;

                /* 当未激活时 */
                if( ! shipment.isShipmentSeparationActivate )
                {
                    for( itemIndex in items )
                    {
                        items[ itemIndex].isShipmentSeparationActivate = true;
                    }
                    shipment.isShipmentSeparationActivate = true;
                }
                /* 当激活时再进来就是［确认分单］操作 */
                else
                {
                    var isSeparateSuccessful = false;
                    var clonedItems = [];
                    var deletedItems = [];

                    for( itemIndex in items )
                    {
                        if( items[ itemIndex ].qtySeparated > 0 )
                        {
                            isSeparateSuccessful = true;

                            var holdQtyShipped = items[ itemIndex ].qtyShipped;
                            var holdQtySeparated = items[ itemIndex ].qtySeparated;
                            var clonedItem = angular.copy( items[ itemIndex ] );
                            clonedItem.qtyShipped = clonedItem.qtySeparated;
                            clonedItem.qtySeparated = null;
                            clonedItems.push( clonedItem );

                            if( items[ itemIndex ].qtyShipped === items[ itemIndex ].qtySeparated )
                            {
                                deletedItems.push( items[ itemIndex ] );
                            }
                            else
                            {
                                items[ itemIndex ].qtyShipped = holdQtyShipped - holdQtySeparated;
                                items[ itemIndex ].qtySeparated = null;
                            }
                        }
                    }

                    if( deletedItems && deletedItems.length > 0 )
                    {
                        for( var deletedItemIndex in deletedItems )
                        {
                            items.splice( items.indexOf( deletedItems[ deletedItemIndex ] ), 1 );
                        }
                    }

                    for( itemIndex in clonedItems )
                    {
                        clonedItems[ itemIndex ].isShipmentSeparationActivate = false;
                    }

                    /* 拆分成功 */
                    if( isSeparateSuccessful )
                    {
                        for( itemIndex in items )
                        {
                            items[ itemIndex].isShipmentSeparationActivate = false;
                        }

                        var sameOrderLastShipmentIndex = 0;
                        var clonedShipment = {};
                        for( var shipmentIndex in $scope.shipments )
                        {
                            if( $scope.shipments[ shipmentIndex].orderId === shipment.orderId )
                            {
                                sameOrderLastShipmentIndex = shipmentIndex;
                                clonedShipment = angular.copy( $scope.shipments[ shipmentIndex] );
                            }
                        }
                        clonedShipment.shipmentItems = clonedItems;

                        sameOrderLastShipmentIndex++;

                        $scope.shipments.splice( sameOrderLastShipmentIndex , 0, clonedShipment );

                        shipment.isShipmentSeparationActivate = false;
                        clonedShipment.isShipmentSeparationActivate = false;
                    }
                    /* 拆分失败 */
                    else
                    {
                        toastr.warning('请在需要拆分的商品后面［拆分数量］输入框内填写数量');
                    }

                    /* 如果运单没有详情，则剔除 */
                    if( shipment.shipmentItems.length < 1 )
                    {
                        $scope.shipments.splice( $scope.shipments.indexOf( shipment ), 1 );
                    }
                }
            }
            /* 如果详情小于等于 1 个，则提示操作员当［当前订单只有一个商品不能进行分单操作］ */
            else
            {
                toastr.warning('当前运单只有一件商品不能进行分单操作');
            }
        };

        /** 取消分单
         */
        $scope.separateShipmentCancel = function( shipment )
        {
            var items = shipment.shipmentItems;
            for( itemIndex in items )
            {
                items[ itemIndex ].isShipmentSeparationActivate = false;
                items[ itemIndex ].qtySeparated = null;
            }
            shipment.isShipmentSeparationActivate = false;
        };

        /** 合并运单
         */
        $scope.mergeSelectedShipments = function()
        {
            var selectedShipments = [];
            if( $scope.shipments && $scope.shipments.length > 0 )
            {
                var shipments = $scope.shipments;
                for( var shipmentIndex in shipments )
                {
                    if( shipments[ shipmentIndex ].isSelected )
                    {
                        selectedShipments.push( shipments[ shipmentIndex ] );
                    }
                }
            }

            if( selectedShipments.length > 1 )
            {
                var isOrderIdMatched = true;
                var previousShipment = null;
                for( var selectedShipmentIndex in selectedShipments )
                {
                    var selectedShipment = selectedShipments[ selectedShipmentIndex ];
                    if( previousShipment )
                    {
                        if( selectedShipment.orderId !== previousShipment.orderId )
                        {
                            isOrderIdMatched = false;
                            break;
                        }
                    }
                    previousShipment = angular.copy( selectedShipment );
                }
                if( isOrderIdMatched )
                {
                    var finalShipment = selectedShipments[ 0 ];
                    var finalShipmentItems = finalShipment.shipmentItems;
                    var mergedShipmentItemIndex = 0;
                    for( var finalShipmentIndex in selectedShipments )
                    {
                        if( finalShipmentIndex > 0 )
                        {
                            var mergedShipmentItems = selectedShipments[ finalShipmentIndex ].shipmentItems;
                            for( var finalShipmentItemIndex in finalShipmentItems )
                            {
                                for( mergedShipmentItemIndex in mergedShipmentItems )
                                {
                                    /* 如果该合并的详情存在，则累加其［发货数量］ */
                                    if( mergedShipmentItems[ mergedShipmentItemIndex ].orderItemId === finalShipmentItems[ finalShipmentItemIndex ].orderItemId )
                                    {
                                        finalShipmentItems[ finalShipmentItemIndex ].qtyShipped += mergedShipmentItems[ mergedShipmentItemIndex ].qtyShipped;
                                        mergedShipmentItems.splice( mergedShipmentItems.indexOf( mergedShipmentItems[ mergedShipmentItemIndex ] ), 1 );
                                    }
                                }
                            }
                            /* 如果由合并的详情不存在最终发货单内，则追加详情 */
                            if( mergedShipmentItems.length > 0 )
                            {
                                for( mergedShipmentItemIndex in mergedShipmentItems )
                                {
                                    finalShipmentItems.push( angular.copy( mergedShipmentItems[ mergedShipmentItemIndex ] ) );
                                    mergedShipmentItems.splice( mergedShipmentItems.indexOf( mergedShipmentItems[ mergedShipmentItemIndex ] ), 1 );
                                }
                            }
                            $scope.shipments.splice( $scope.shipments.indexOf( selectedShipments[ finalShipmentIndex ] ), 1 );
                        }
                    }

                    toastr.success('成功合并选中的运单');
                }
                else
                {
                    toastr.warning('合并的运单包含不同订单的运单，请剔除不同订单号的运单后再继续');
                }
            }
            else
            {
                toastr.warning('请选择至少 2 张同订单号的运单来进行合并');
            }
        };

        /** 向下批量生成快递单号
         */
        $scope.downwardGeneratingShipNumbers = function( shipment )
        {
            var shipments = $scope.shipments;
            if( shipments[ shipments.length - 1 ] !== shipment )
            {
                if( shipment.shipNumber )
                {
                    var isBelowShipment = false;
                    var shipNumberStr = shipment.shipNumber.replace(/[^a-zA-Z\.]/g, '');
                    var shipNumberInt = shipment.shipNumber.replace(/[^0-9\.]/g, '');
                    for( var shipmentIndex in shipments )
                    {
                        if( isBelowShipment )
                        {
                            shipNumberInt++;
                            shipments[ shipmentIndex].shipNumber = shipNumberStr + shipNumberInt;
                        }

                        if( shipments[ shipmentIndex ] === shipment )
                        {
                            isBelowShipment = true;
                        }
                    }

                    $scope.isAnythingModified = true;
                }
                else
                {
                    toastr.warning('请确保该运单有快递单号，才能使用批量向下生成');
                }
            }
            else
            {
                toastr.warning('最后一张运单不能使用该功能');
            }
        };

        /** 打开新标签页准备好可供打印的配货单
         */
        $scope.printDistributedOrders = function()
        {
            if( $scope.orders && $scope.orders.length > 0 )
            {
                var orderIds = [];
                $.each( $scope.orders, function()
                {
                    orderIds.push( this.id );
                });

                $window.open( '/#/order-print?orderId=' + ( orderIds || '') );
            }
            else
            {
                toastr.warning('没有可以打印的配货单');
            }
        };

        $scope.shipmentCouriers = [];
        $scope.shipmentCourierPrintSlideChecked = false;
        /** 打印快递单
         */
        $scope.toggleShipmentCourierPrintSlide = function()
        {
            $scope.shipmentCourierPrintSlideChecked = ! $scope.shipmentCourierPrintSlideChecked;

            /* 如果打开弹出层，则显示弹出层滚动条并隐藏body滚动条，反之亦然 */
            if( $scope.shipmentCourierPrintSlideChecked )
            {
                $scope.shipmentCouriers = angular.copy( $scope.shipments );

                $('body').css('overflow','hidden');
                $('[ps-open="shipmentCourierPrintSlideChecked"]').css('overflow','auto');
                toastr.success('快递单准备就绪，可以进行打印操作');
            }
            else
            {
                $('body').css('overflow','auto');
                $('[ps-open="shipmentCourierPrintSlideChecked"]').css('overflow','hidden');
            }
        };


        function executeShipmentOperationReview( query, action )
        {
            var data = {
                action                      : action,
                warehouseId                 : query.warehouse ? query.warehouse.id : null,
                shopId                      : query.shop ? query.shop.id : null,
                deleted                     : false,
                orders                      : orderService.selectedOrders,
                //selectedCourier             : $scope.courier.selected,
                dataMap                     :
                {
                    operatorId              : $rootScope.user().id,
                    startShipNumber         : $scope.startShipNumber
                },
                ignoredMap                  :
                {
                    differentWarehouseError : false,
                    differentDeliveryMethodError : false,
                    emptyCourierAndShipNumberError : false,
                    warehouseExistOrderShipmentError : false,
                    emptyReceiveAddressError : false
                },
                assignWarehouseId           : $scope.query.warehouse ? $scope.query.warehouse.id : null,
                assignShopId                : $scope.query.shop ? $scope.query.shop.id : null,
                assignDeliveryMethod        : $scope.query.deliveryMethod ? $scope.query.deliveryMethod.value : null,
                assignShippingDescription   : $scope.query.shippingDescription ? $scope.query.shippingDescription : null,
                showDeployedOrders          : $scope.query.showDeployedOrders,
                shipments                   : $scope.shipments
            };
            var params =
            {
                page: $scope.query.number,
                size: $scope.query.size,
                sort : ['internalCreateTime,desc']
            };
            console.log('Before Operation Review:');
            console.log( data );
            orderService.confirmOrderWhenGenerateShipment( data, params ).then(function(review)
            {
                console.log('After Operation Review:');
                console.log(review);
                $scope.page = review.pagedOrder;
                console.log( '$scope.page: ' );
                console.log( $scope.page );

                $scope.shipments = angular.copy( review.reviewShipments );
                $scope.orders = angular.copy( review.orders );
            });
        }

        executeShipmentOperationReview( $scope.query, 0, 'VERIFY' );

        $scope.operateDate = Date.now();
        $scope.operationReview = orderService.getOperationReview;

        function updateCreateTime()
        {
            $scope.operateDate = new Date();
        }

        var createTime = $interval(updateCreateTime, 500);

        /* 点击将某个订单的 ignoreCheck 标为  ! ignoreCheck，在进行复核验证时不再对该订单进行验证 */
        //$scope.ignoreOrNotCheckOrder = function( order )
        //{
        //    order.ignoreCheck = ! order.ignoreCheck;
        //    var operationReview = orderService.getOperationReview();
        //    var data =
        //    {
        //        'action' : 'VERIFY',
        //        'orders' : operationReview.orders,
        //        'checkMap' : operationReview.checkMap,
        //        'dataMap' : operationReview.dataMap,
        //        'ignoredMap' : operationReview.ignoredMap,
        //        'selectedCourier' : operationReview.selectedCourier,
        //        'assignWarehouseId' : operationReview.assignWarehouseId,
        //        'assignShopId'                : operationReview.assignShopId,
        //        'assignDeliveryMethod'        : operationReview.assignDeliveryMethod,
        //        'shippingDescription'         : operationReview.shippingDescription,
        //        'showDeployedOrders'          : operationReview.showDeployedOrders
        //    };
        //    var params =
        //    {
        //        page: $scope.query.number,
        //        size: $scope.query.size,
        //        sort : ['internalCreateTime,desc']
        //    };
        //    orderService.confirmOrderWhenGenerateShipment( data, params).then(function(review){
        //        console.log('After Operation Review:');
        //        console.log(review);
        //
        //        $scope.shipments = angular.copy( review.shipments );
        //    });
        //};

        /* 点击将［取消］或［恢复］某项验证 */
        $scope.ignoreOrNotChecker = function(ignoredMap, checker, isIgnored)
        {
            ignoredMap[checker] = isIgnored;

            var operationReview = orderService.getOperationReview();
            var data =
            {
                'action'                        : 'VERIFY',
                'orders'                        : operationReview.orders,
                'checkMap'                      : operationReview.checkMap,
                'dataMap'                       : operationReview.dataMap,
                'ignoredMap'                    : operationReview.ignoredMap,
                'selectedCourier'               : operationReview.selectedCourier,
                'assignWarehouseId'             : operationReview.assignWarehouseId,
                'assignShopId'                  : operationReview.assignShopId,
                'assignDeliveryMethod'          : operationReview.assignDeliveryMethod,
                'assignShippingDescription'     : operationReview.assignShippingDescription,
                'showDeployedOrders'            : operationReview.showDeployedOrders,
                'shipments'                     : $scope.shipments
            };
            var params =
            {
                page: $scope.query.number,
                size: $scope.query.size,
                sort : ['internalCreateTime,desc']
            };
            orderService.confirmOrderWhenGenerateShipment( data, params ).then(function(review){
                console.log('After Operation Review:');
                console.log(review);

                if( ! $scope.shipments || $scope.shipments.length < 1 )
                {
                    $scope.shipments = angular.copy( review.reviewShipments );
                }
                $scope.orders = angular.copy( review.orders );
            });
        };

        /* 确认生成发货单，如果再次验证通过则可以生成，否则保持在该页 */
        $scope.confirmShipmentGeneration = function()
        {
            var operationReview = orderService.getOperationReview();
            var data =
            {
                'action'                        : 'CONFIRM',
                'orders'                        : operationReview.orders,
                'checkMap'                      : operationReview.checkMap,
                'dataMap'                       : operationReview.dataMap,
                'ignoredMap'                    : operationReview.ignoredMap,
                'selectedCourier'               : operationReview.selectedCourier,
                'assignWarehouseId'             : operationReview.assignWarehouseId,
                'assignShopId'                  : operationReview.assignShopId,
                'assignDeliveryMethod'          : operationReview.assignDeliveryMethod,
                'assignShippingDescription'     : operationReview.assignShippingDescription,
                'showDeployedOrders'            : operationReview.showDeployedOrders,
                'shipments'                     : $scope.shipments
            };
            var params =
            {
                page: $scope.query.number,
                size: $scope.query.size,
                sort : ['internalCreateTime,desc']
            };

            orderService.confirmOrderWhenGenerateShipment( data, params ).then(function(review)
            {
                if( review.confirmable )
                {
                    /* 如果没有最终订单  */
                    if( review.resultMap.isEmptyShipments )
                    {
                        toastr.warning('抱歉，没有可以生成发货单的订单！');
                    }
                    else
                    {
                        toastr.success('成功开出 ' + review.resultMap.generatedShipmentCount + ' 张发货单。');
                        $state.go('shipment');
                    }
                }
                else
                {
                    toastr.warning('确认之前请确保验证全部通过，或者您可以选择点击［取消验证］之后再确认！');
                }

                if( ! $scope.shipments || $scope.shipments.length < 1 )
                {
                    $scope.shipments = angular.copy( review.reviewShipments );
                }

                $scope.orders = angular.copy( review.orders );
                //console.log('After Operation Review:');
                //console.log(review);
                //console.log('验证是否全部通过 ：' + review.confirmable);
                $interval.cancel(createTime);
            });
        };
    }
]).directive('tooltip', function()
{
    return {
        restrict: 'A',
        link: function(scope, element)
        {
            $(element).hover(function()
            {
                // on mouseenter
                $(element).tooltip('show');
            }, function()
            {
                // on mouseleave
                $(element).tooltip('hide');
            });
        }
    };
});