angular.module('ecommApp')
.controller('ShipmentGenerateController', ['$scope', '$rootScope', '$location', '$interval', 'toastr', 'orderService', 'Shop', 'Utils', 'Warehouse', 'courierService',
    function($scope, $rootScope, $location, $interval, toastr, orderService, Shop, Utils, Warehouse, courierService) {

        var $ = angular.element;

        /** 如果编辑了任何信息，则切换数据时提示操作员［确定取消当前所有操作？］
         */
        $scope.isAnythingModified = false;
        /** 是否显示出过发货单的订单
         */
        $scope.showDeployedOrders = false;

        $scope.courier = {};
        $scope.defaultQuery = {
            number: 0,
            size: 100,
            sort: ['internalCreateTime,desc'],
            warehouse: undefined,
            shop: undefined,
            statuses: []
        };
        $scope.query = angular.copy( $scope.defaultQuery );

        $scope.deliveryMethods =
        [
            { name:'快递', value:1 },
            { name:'自提', value:2 },
            { name:'送货上门', value:3 }
        ];

        $scope.shipments = [];


        // 将所有店铺过滤，拿出所有配置了配送状态的店铺的ID
        $scope.selectAllShops = function(shops)
        {
            $.each(shops, function()
            {
                var shop = this;
                if (shop.deployStep)
                {
                    var exist = false;
                    $.each($scope.query.statuses, function()
                    {
                        if (this.id === shop.deployStep.id)
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
            if (number > -1 && number < $scope.page.totalPages)
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

        /* 锁定/解锁同品更名 */
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

        /* hold住并更改详情简称 */
        $scope.holdAndChangeItemShortName = function( item )
        {
            if( ! item.holdName )
            {
                item.holdName = item.shortName ? item.shortName : item.fullName;
            }
            $scope.isAnythingModified = true;
        };

        /* 更改详情数量，有做改动 */
        $scope.changeItemQtyShippedBlur = function( item )
        {
            if( ! angular.isNumber( item.qtyShipped ) || item.qtyShipped < 1 )
            {
                item.qtyShipped = 1;
            }
            $scope.isAnythingModified = true;
        };

        /* 更改拆分数量，有做改动 */
        $scope.changeItemQtySeparatedBlur = function( item )
        {
            if( angular.isNumber( item.qtySeparated ) && item.qtySeparated < 1 )
            {
                item.qtySeparated = 1;
            }
            else if( angular.isNumber( item.qtySeparated ) && item.qtySeparated > item.qtyShipped )
            {
                item.qtySeparated = item.qtyShipped;
            }
        };

        /* 清空同品更名 */
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

        /* 分单 */
        $scope.separateShipmentPrepare = function( shipment )
        {
            /* 分单准备之前要确保详情不止一个 */
            if( shipment.shipmentItems && shipment.shipmentItems.length > 1 )
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

                    for( itemIndex in items )
                    {
                        if( items[ itemIndex ].qtySeparated )
                        {
                            isSeparateSuccessful = true;

                            var holdQtyShipped = items[ itemIndex ].qtyShipped;
                            var clonedItem = angular.copy( items[ itemIndex ] );
                            clonedItem.qtyShipped = clonedItem.qtySeparated;
                            clonedItem.qtySeparated = null;
                            clonedItems.push( clonedItem );

                            if( items[ itemIndex ].qtyShipped === items[ itemIndex ].qtySeparated )
                            {
                                items.splice( itemIndex, 1 );
                            }
                            else
                            {
                                items[ itemIndex ].qtyShipped = holdQtyShipped;
                                items[ itemIndex ].qtySeparated = null;
                            }
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

                        console.log( 'sameOrderLastShipmentIndex: ' + sameOrderLastShipmentIndex );

                        shipment.isShipmentSeparationActivate = false;
                        clonedShipment.isShipmentSeparationActivate = false;
                    }
                    /* 拆分失败 */
                    else
                    {
                        toastr.warning('请在需要拆分的商品后面［拆分数量］输入框内填写数量');
                    }
                }
            }
            /* 如果详情小于等于 1 个，则提示操作员当［当前订单只有一个商品不能进行分单操作］ */
            else
            {
                toastr.warning('当前订单只有一个商品不能进行分单操作');
            }
        };

        /* 取消分单 */
        $scope.separateShipmentCancel = function( shipment )
        {
            var items = shipment.shipmentItems;
            for( itemIndex in items )
            {
                items[ itemIndex].isShipmentSeparationActivate = false;
                items[ itemIndex].qtySeparated = null;
            }
            shipment.isShipmentSeparationActivate = false;
        };

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
                    for( var finalShipmentIndex in selectedShipments )
                    {
                        console.log( 'finalShipmentIndex: ' + finalShipmentIndex );
                        if( finalShipmentIndex > 0 )
                        {
                            var mergedShipmentItems = selectedShipments[ finalShipmentIndex].shipmentItems;
                            for( var mergedShipmentItemIndex in mergedShipmentItems )
                            {
                                for( var finalShipmentItemIndex in finalShipmentItems )
                                {
                                    console.log( 'mergedShipmentItems[ mergedShipmentItemIndex ]: ' );
                                    console.log( mergedShipmentItems[ mergedShipmentItemIndex ] );
                                    console.log( 'finalShipmentItems[ finalShipmentItemIndex ]: ' );
                                    console.log( finalShipmentItems[ finalShipmentItemIndex ] );
                                    if( mergedShipmentItems[ mergedShipmentItemIndex ].orderItemId === finalShipmentItems[ finalShipmentItemIndex ].orderItemId )
                                    {
                                        finalShipmentItems[ finalShipmentItemIndex ].qtyShipped += mergedShipmentItems[ mergedShipmentItemIndex ].qtyShipped;
                                    }
                                    else
                                    {
                                        finalShipmentItems.push( angular.copy( mergedShipmentItems[ mergedShipmentItemIndex] ) );
                                    }
                                }
                            }
                        }
                    }

                    toastr.success('成功合并选中的运单');
                }
                else
                {
                    toastr.warning('合并的运单内包含不同订单的运单，请剔除不同订单的运单再继续');
                }
            }
            else
            {
                toastr.warning('请选择至少 2 张同订单号的运单来进行合并');
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
                shippingDescription         : $scope.query.shippingDescription ? $scope.query.shippingDescription : null,
                showDeployedOrders          : $scope.showDeployedOrders
            };
            var params =
            {
                page: $scope.query.number,
                size: $scope.query.size,
                sort : ['internalCreateTime,desc']
            };
            console.log('Before Operation Review:');
            console.log( data );
            orderService.confirmOrderWhenGenerateShipment( data, params ).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
                $scope.page = review.pagedOrder;
                console.log( '$scope.page: ' );
                console.log( $scope.page );

                $scope.shipments = angular.copy( review.shipments );

            });
        }

        executeShipmentOperationReview( $scope.query, 0, 'VERIFY' );


        $scope.operateDate = Date.now();
        $scope.operationReview = orderService.getOperationReview;

        function updateCreateTime() {
            $scope.operateDate = new Date();
        }

        var createTime = $interval(updateCreateTime, 500);

        /* 点击将某个订单的 ignoreCheck 标为  ! ignoreCheck，在进行复核验证时不再对该订单进行验证 */
        $scope.ignoreOrNotCheckOrder = function( order )
        {
            order.ignoreCheck = ! order.ignoreCheck;
            var operationReview = orderService.getOperationReview();
            var data =
            {
                'action' : 'VERIFY',
                'orders' : operationReview.orders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap,
                'selectedCourier' : operationReview.selectedCourier,
                'assignWarehouseId' : operationReview.assignWarehouseId,
                'assignShopId'                : operationReview.assignShopId,
                'assignDeliveryMethod'        : operationReview.assignDeliveryMethod,
                'shippingDescription'         : operationReview.shippingDescription,
                'showDeployedOrders'          : operationReview.showDeployedOrders
            };
            var params =
            {
                page: $scope.query.number,
                size: $scope.query.size,
                sort : ['internalCreateTime,desc']
            };
            orderService.confirmOrderWhenGenerateShipment( data, params).then(function(review){
                console.log('After Operation Review:');
                console.log(review);

                $scope.shipments = angular.copy( review.shipments );
            });
        };

        /* 点击将［取消］或［恢复］某项验证 */
        $scope.ignoreOrNotChecker = function(ignoredMap, checker, isIgnored)
        {
            ignoredMap[checker] = isIgnored;

            var operationReview = orderService.getOperationReview();
            var data =
            {
                'action' : 'VERIFY',
                'orders' : operationReview.orders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap,
                'selectedCourier' : operationReview.selectedCourier,
                'assignWarehouseId' : operationReview.assignWarehouseId,
                'assignShopId'                : operationReview.assignShopId,
                'assignDeliveryMethod'        : operationReview.assignDeliveryMethod,
                'shippingDescription'         : operationReview.shippingDescription,
                'showDeployedOrders'          : operationReview.showDeployedOrders
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

                $scope.shipments = angular.copy( review.shipments );
            });
        };

        /* 确认生成发货单，如果再次验证通过则可以生成，否则保持在该页 */
        $scope.confirmShipmentGeneration = function()
        {
            var operationReview = orderService.getOperationReview();
            var data =
            {
                'action' : 'CONFIRM',
                'orders' : operationReview.orders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap,
                'selectedCourier' : operationReview.selectedCourier,
                'assignWarehouseId' : operationReview.assignWarehouseId,
                'assignShopId'                : operationReview.assignShopId,
                'assignDeliveryMethod'        : operationReview.assignDeliveryMethod,
                'shippingDescription'         : operationReview.shippingDescription,
                'showDeployedOrders'          : operationReview.showDeployedOrders
            };
            var params =
            {
                page: $scope.query.number,
                size: $scope.query.size,
                sort : ['internalCreateTime,desc']
            };
            orderService.confirmOrderWhenGenerateShipment( data, params ).then(function(review){

                if( review.confirmable )
                {
                    /* 如果没有最终订单  */
                    if( review.resultMap.isEmptyFinalOrders )
                    {
                        toastr.warning('抱歉，没有可以生成发货单的订单！');
                    }
                    else
                    {
                        toastr.success('成功开出 ' + review.resultMap.generatedShipmentCount + ' 张发货单。');
                        $scope.$parent.toggleShipmentSheetSlide();

                        orderService.getPagedOrdersForOrderDeploy({
                            page: 0,
                            size: $scope.$parent.pageSize,
                            sort: ['internalCreateTime,desc'],
                            warehouseId: $scope.$parent.query.warehouse ? $scope.$parent.query.warehouse.id : null,
                            shopId: $scope.$parent.query.shop ? $scope.$parent.query.shop.id : null,
                            deleted: false
                        }).then(function(page) {
                            console.log('page:');
                            console.log(page);
                            $.each(page.content, function() {
                                orderService.checkItemProductShopTunnel(this);
                            });
                            $scope.$parent.page = page;
                            $scope.$parent.totalPagesList = Utils.setTotalPagesList(page);
                        });

                        $location.url('/shipments');
                    }
                }
                else
                {
                    toastr.warning('确认之前请确保验证全部通过，或者您可以选择点击［取消验证］之后再确认！');
                }

                console.log('After Operation Review:');
                console.log(review);
                console.log('验证是否全部通过 ：' + review.confirmable);
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