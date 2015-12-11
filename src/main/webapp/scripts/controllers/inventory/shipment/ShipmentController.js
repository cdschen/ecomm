angular.module('ecommApp')

.controller('ShipmentController', ['$scope', '$rootScope', 'toastr', 'Warehouse', 'Shop', 'courierService', 'Utils', 'shipmentService', 'Auth',
    function($scope, $rootScope, toastr, Warehouse, Shop, courierService, Utils, shipmentService, Auth) {

        /** 如果编辑了任何信息，则切换数据时提示操作员［确定取消当前所有操作？］
         */
        $scope.isAnythingModified = false;

        $scope.template = {
            shipmentComplete: {
                url: 'views/inventory/shipment/shipment-complete.html?' + (new Date())
            },
            shipmentsComplete: {
                url: 'views/inventory/shipment/shipments-complete.html?' + (new Date())
            }
        };

        /* Activate Date Picker */
        $('input[ng-model="query.createTimeStart"], input[ng-model="query.createTimeEnd"], ' +
            'input[ng-model="query.lastUpdateStart"], input[ng-model="query.lastUpdateEnd"], ' +
            'input[ng-model="query.pickupTimeStart"], input[ng-model="query.pickupTimeEnd"], ' +
            'input[ng-model="query.signupTimeStart"], input[ng-model="query.signupTimeEnd"]').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'bottom left',
            todayHighlight: true
        });

        $scope.selectedShipment = {};

        $scope.shipmentCompleteSlideChecked = false;
        $scope.shipmentsCompleteSlideChecked = false;

        $scope.defaultQuery = {
            size: 20,
            sort: ['createTime,desc'],
            warehouse: undefined,
            shop: undefined,
            shipStatus: undefined,
            deliveryMethod: undefined,
            shippingDescription: undefined
        };
        $scope.query = angular.copy( $scope.defaultQuery );
        $scope.warehouses = [];
        $scope.shipments = [];
        $scope.shops = [];
        $scope.couriers = [];
        $scope.shipStatus =
        [
            { id : 1, name : '已打印' },
            { id : 7, name : '待处理' },
            { id : 2, name : '配货完成' },
            { id : 5, name : '配送异常' },
            { id : 3, name : '已发出' },
            { id : 4, name : '已签收' },
            { id : 6, name : '已作废' }
        ];

        $scope.deliveryMethods =
        [
            { value : 1, name : '快递' },
            { value : 2, name : '自提' },
            { value : 3, name : '送货上门' }
        ];

        $scope.deleteIds = [];

        /* 查询发货单分页数据所需查询参数 */
        function getQueryParamJSON( query, number )
        {
            return {
                page: number ? number : 0,
                size: query.size,
                sort: ['createTime,desc'],
                shipWarehouseId: query.warehouse ? query.warehouse.id : null,
                orderId: query.orderId ? query.orderId : null,
                courierId: query.courier ? query.courier.id : null,
                shipNumber: query.shipNumber ? query.shipNumber : null,
                shopId: query.shop ? query.shop.id : null,
                shipStatus: query.shipStatus ? query.shipStatus.id : null,
                deliveryMethod: $scope.query.deliveryMethod ? $scope.query.deliveryMethod.value : null,
                shippingDescription: $scope.query.shippingDescription ? $scope.query.shippingDescription : null,
                createTimeStart: query.createTimeStart ? query.createTimeStart : null,
                createTimeEnd: query.createTimeEnd ? query.createTimeEnd : null,
                lastUpdateStart: query.lastUpdateStart ? query.lastUpdateStart : null,
                lastUpdateEnd: query.lastUpdateEnd ? query.lastUpdateEnd : null,
                pickupTimeStart: query.pickupTimeStart ? query.pickupTimeStart : null,
                pickupTimeEnd: query.pickupTimeEnd ? query.pickupTimeEnd : null,
                signupTimeStart: query.signupTimeStart ? query.signupTimeStart : null,
                signupTimeEnd: query.signupTimeEnd ? query.signupTimeEnd : null
            };
        }

        Warehouse.getAll({ // 导入所有仓库
            deleted: false,
            sort: ['name'],
            warehouseIds: Auth.refreshManaged('warehouse')
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有快递公司
            return courierService.getAll({
                sort: ['name']
            }).then(function(couriers) {
                $scope.couriers = couriers;
            });
        }).then(function() { // 导入所有店铺
            return Shop.getAll({
                deleted: false,
                sort: ['name'],
                shopIds: Auth.refreshManaged('shop')
            }).then(function(shops) {
                $scope.shops = shops;
            });
        }).then(function() {
            //$scope.query.shipStatus = { id : 1, name : '已打印' };
            $scope.search( $scope.query );
        });

        $scope.searchData = function( query, number )
        {
            shipmentService.get( getQueryParamJSON( query, number ), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                Utils.initList(page, $scope.query);
            });
        };

        $scope.turnPage = function(number)
        {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData( $scope.query, number );
            }
        };

        $scope.search = function( query )
        {
            $scope.searchData( query );
        };

        $scope.reset = function()
        {
            $scope.query = angular.copy($scope.defaultQuery);
            $scope.searchData( $scope.query );
        };

        $scope.toggleShipmentCompleteSlide = function(){
            $scope.shipmentCompleteSlideChecked = !$scope.shipmentCompleteSlideChecked;

            /* 如果打开弹出层，则显示弹出层滚动条并隐藏body滚动条，反之亦然 */
            if( $scope.shipmentCompleteSlideChecked )
            {
                $('body').css('overflow','hidden');
                $('[ps-open="shipmentCompleteSlideChecked"]').css('overflow','auto');
            }
            else
            {
                $('body').css('overflow','auto');
                $('[ps-open="shipmentCompleteSlideChecked"]').css('overflow','hidden');
            }
        };

        $scope.toggleShipmentsCompleteSlide = function(){
            $scope.shipmentsCompleteSlideChecked = !$scope.shipmentsCompleteSlideChecked;
        };


        $scope.isCheckedAll = false;
        $scope.batchManipulationValue = 'batchManipulation';
        $scope.selectedCourier = {};
        $scope.startCourierNumber = '';

        $scope.checkAllOrders = function()
        {
            for( var shipment in $scope.page.content )
            {
                $scope.page.content[shipment].isSelected = $scope.isCheckedAll;
            }
        };

        /** 批量操作
         */
        $scope.batchManipulation = function()
        {
            var shipments = $scope.page.content;
            var ids = [];
            shipmentService.selectedShipments.length = 0;
            $.each(shipments, function(){
                var shipment = this;
                if (shipment.isSelected) {
                    shipmentService.selectedShipments.push(angular.copy(shipment));
                    ids.push( shipment.id );
                }
            });
            if ( shipmentService.selectedShipments.length > 0 )
            {
                if( $scope.batchManipulationValue === 'changeStatus' )
                {
                    //$scope.showShipmentsCompleteSlide();
                }
                else if( $scope.batchManipulationValue === 'shipmentExport' )
                {
                    window.location.href = '/api/shipment/export?ids=' + ids;
                    toastr.info('批量导出发货单');
                }
                else if( $scope.batchManipulationValue === 'shipmentPrint' )
                {
                    toastr.info('发货单打印！');
                }
                else if( $scope.batchManipulationValue === 'shipmentObsolete' )
                {
                    $('#obsoleteShipments').modal('show');
                }
            }
            else
            {
                toastr.error('请选择一到多个发货单来继续！');
            }


            $scope.batchManipulationValue = 'batchManipulation';
        };


        $scope.targetStatus = 0;
        $scope.targetStatusStr = '';
        $scope.StatusStrList =
        {
            1   :   '已打印',
            2   :   '配货完成',
            3   :   '已发出',
            4   :   '已签收',
            5   :   '配送异常',
            6   :   '已作废',
            7   :   '待处理'
        };

        /**
         * 单个发货单切换状态 : 开始
         */
        $scope.showChangeShipmentStatusModal = function( shipment, targetStatus )
        {
            $scope.selectedShipment = shipment;

            /** Modal层显示用
             */
            $scope.targetStatusStr = $scope.StatusStrList[ targetStatus ];

            if
            (
                /** 如果处于［已签收：4］或［已作废：6］中，则不能进行状态切换操作
                 */
                shipment.shipStatus === 4 || shipment.shipStatus === 6
            )
            {
                toastr.warning( '处于［' + $scope.StatusStrList[ shipment.shipStatus ] + '］的状态不能再切换到其他状态' );
            }
            else
            {
                /** 提交切换操作时用
                 */
                $scope.targetStatus = targetStatus;

                $('#changeShipmentStatus').modal('show');
            }
        };

        $scope.changeShipmentStatus = function()
        {
            /** 赋上目标状态
             */
            $scope.selectedShipment.shipStatus = $scope.targetStatus;

            shipmentService.changeShipmentStatus( $scope.selectedShipment, function()
            {
                $scope.search( $scope.query );
            });

            $('#changeShipmentStatus').modal('hide');

            toastr.success('切换状态至［' + $scope.targetStatusStr + '］');
        };
        /**
         * 单个发货单切换状态 : 结束
         */


        /**
         * 多个发货单切换状态 : 开始
         */
        /* 将批量选中的货单作废 */
        $scope.obsoleteShipments = function()
        {
            for( var shipmentIndex in shipmentService.selectedShipments )
            {
                /* 作废状态 */
                shipmentService.selectedShipments[ shipmentIndex ].shipStatus = 5;
            }

            var shipment = {};
            shipment.shipments = shipmentService.selectedShipments;

            shipmentService.saveShipments( shipment )
                .then(function(shipments) {

                    console.log( shipments );

                    shipmentService.get( getQueryParamJSON(), function(page) {
                        console.log('page:');
                        console.log(page);
                        $scope.page = page;
                        Utils.initList(page, $scope.query);
                    });
                });

            $('#obsoleteShipments').modal('hide');
            toastr.error('作废批量选中的发货单！');
        };
        /**
         * 多个发货单切换状态 : 结束
         */


        /**
         * 单个发货单完成 : 开始
         */
        //$scope.showShipmentCompleteSlide = function( shipment )
        //{
        //    shipmentService.setSelectedShipment( angular.copy( shipment ) );
        //
        //    executeOperationReviewCompleteShipment('VERIFY');
        //
        //    $scope.toggleShipmentCompleteSlide();
        //};
        //
        //
        //function executeOperationReviewCompleteShipment(action)
        //{
        //    var reviewDTO = {
        //        action                      : action,
        //        shipment                    : shipmentService.getSelectedShipment(),
        //        dataMap : {
        //            executeOperatorId                : $rootScope.user().id
        //        },
        //        ignoredMap : {
        //            emptyCourierError : false,
        //            emptyShipNumberError : false,
        //            emptyReceiveNameError : false,
        //            emptyReceivePhoneError : false,
        //            emptyReceiveAddressError : false
        //        }
        //    };
        //    console.log('Before Operation Review:');
        //    console.log(reviewDTO);
        //    shipmentService.confirmOperationReviewWhenCompleteShipment(reviewDTO).then(function(review){
        //        console.log('After Operation Review Complete Shipment:');
        //        console.log(review);
        //    });
        //}
        /**
         * 单个发货单完成 : 结束
         */


        /**
         * 多个发货单完成 : 开始
         */
        //$scope.showShipmentsCompleteSlide = function()
        //{
        //    executeOperationReviewCompleteShipments('VERIFY');
        //
        //    $scope.toggleShipmentsCompleteSlide();
        //};
        //
        //function executeOperationReviewCompleteShipments(action)
        //{
        //    var reviewDTO = {
        //        action                      : action,
        //        shipments                    : shipmentService.selectedShipments,
        //        dataMap : {
        //            executeOperatorId                : $rootScope.user().id
        //        },
        //        ignoredMap : {
        //            emptyCourierError : false,
        //            emptyShipNumberError : false,
        //            emptyReceiveNameError : false,
        //            emptyReceivePhoneError : false,
        //            emptyReceiveAddressError : false
        //        }
        //    };
        //    console.log('Before Operation Review:');
        //    console.log(reviewDTO);
        //    shipmentService.confirmOperationReviewWhenCompleteShipments(reviewDTO).then(function(review){
        //        console.log('After Operation Review Complete Shipments:');
        //        console.log(review);
        //    });
        //}
        /**
         * 多个发货单完成 : 结束
         */


        /** 打开新标签页准备好可供打印的配货单
         */
        //$scope.printDistributedOrders = function()
        //{
        //    if( $scope.orders && $scope.orders.length > 0 )
        //    {
        //        var orderIds = [];
        //        $.each( $scope.orders, function()
        //        {
        //            orderIds.push( this.id );
        //        });
        //
        //        $window.open( '/#/order-print?orderId=' + ( orderIds || '') );
        //    }
        //    else
        //    {
        //        toastr.warning('没有可以打印的配货单');
        //    }
        //};

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
                            items[ itemIndex ].holdName = items[ itemIndex ].printName ? items[ itemIndex ].printName : items[ itemIndex ].shortName ? items[ itemIndex ].shortName : items[ itemIndex ].fullName;
                        }
                        items[ itemIndex ].printName = item.printName;
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
                item.holdName = item.printName ? item.printName : item.shortName ? item.shortName : item.fullName;
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
                items[ itemIndex ].printName = items[ itemIndex ].holdName ? items[ itemIndex ].holdName : items[ itemIndex ].printName;
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
                    if( shipItems[ shipItemIndex ].qtyShipped > 1 )
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
                        items[ itemIndex ].isShipmentSeparationActivate = true;
                    }
                    shipment.isShipmentSeparationActivate = true;
                }
                /* 当激活时再进来就是［确认分单］操作 */
                else
                {
                    var isSeparateSuccessful = false;
                    var clonedItems = [];
                    var deletedItems = [];

                    var totalQtyShipped = 0;
                    var totalQtySeparated = 0;
                    for( itemIndex in items )
                    {
                        totalQtyShipped += items[ itemIndex ].qtyShipped;
                        totalQtySeparated += items[ itemIndex ].qtySeparated;
                    }
                    /** 如果［拆分数量］大于等于［发货数量］，则不予以通过
                     */
                    if( totalQtySeparated >= totalQtyShipped )
                    {
                        toastr.error('总［拆分数量］不可以大于或等于总［发货数量］，请调整后重试');
                        return;
                    }

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
                            clonedItem.id = null;
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

                    var shipments = $scope.page.content;
                    /* 拆分成功 */
                    if( isSeparateSuccessful )
                    {
                        var sameOrderLastShipmentIndex = 0;
                        var clonedShipment = {};
                        for( var shipmentIndex in shipments )
                        {
                            if( shipments[ shipmentIndex ].orderId === shipment.orderId )
                            {
                                sameOrderLastShipmentIndex = shipmentIndex;
                                clonedShipment = angular.copy( shipments[ shipmentIndex ] );
                                clonedShipment.id = null;
                            }
                        }
                        clonedShipment.shipmentItems = clonedItems;

                        sameOrderLastShipmentIndex++;

                        shipments.splice( sameOrderLastShipmentIndex , 0, clonedShipment );

                        shipment.isShipmentSeparationActivate = false;
                        clonedShipment.isShipmentSeparationActivate = false;
                    }
                    /* 拆分失败 */
                    else
                    {
                        toastr.warning('请在需要拆分的商品后面［拆分数量］输入框内填写数量');
                    }

                    /* 如果运单没有详情，则剔除 */
                    if( shipment.shipmentItems.length < 1 && ! shipment.id )
                    {
                        shipments.splice( shipments.indexOf( shipment ), 1 );
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
            var shipments = $scope.page.content;
            var selectedShipments = [];
            if( shipments && shipments.length > 0 )
            {
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
                            var finalAppendShipmentItems = angular.copy( mergedShipmentItems );
                            for( var finalShipmentItemIndex in finalShipmentItems )
                            {
                                for( mergedShipmentItemIndex in mergedShipmentItems )
                                {
                                    /* 如果该合并的详情存在，则累加其［发货数量］ */
                                    if( mergedShipmentItems[ mergedShipmentItemIndex ].orderItemId === finalShipmentItems[ finalShipmentItemIndex ].orderItemId )
                                    {
                                        finalShipmentItems[ finalShipmentItemIndex ].qtyShipped += mergedShipmentItems[ mergedShipmentItemIndex ].qtyShipped;
                                        finalAppendShipmentItems.splice( finalAppendShipmentItems.indexOf( mergedShipmentItems[ mergedShipmentItemIndex ] ), 1 );
                                    }
                                }
                            }
                            /* 如果由合并的详情不存在最终发货单内，则追加详情 */
                            if( finalAppendShipmentItems.length > 0 )
                            {
                                for( var finalAppendShipmentItemIndex in finalAppendShipmentItems )
                                {
                                    finalAppendShipmentItems[ finalAppendShipmentItemIndex ].id = null;
                                    finalShipmentItems.push( angular.copy( finalAppendShipmentItems[ finalAppendShipmentItemIndex ] ) );
                                }
                            }
                            console.log( selectedShipments[ finalShipmentIndex ] );
                            $scope.deleteIds.push( selectedShipments[ finalShipmentIndex].id );
                            shipments.splice( shipments.indexOf( selectedShipments[ finalShipmentIndex ] ), 1 );
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

        $scope.saveShipmentsChangesModal = function()
        {
            $('#saveShipmentsChanges').modal('show');
        };

        /** 保存改动
         */
        $scope.saveShipmentsChanges = function()
        {
            var initShipments = $scope.page.content;
            if( initShipments && initShipments.length > 0 )
            {
                var finalShipments = [];
                for( var initShipmentIndex in initShipments )
                {
                    var initShipment = initShipments[ initShipmentIndex ];

                    var initShipmentItems = initShipments[ initShipmentIndex].shipmentItems;
                    var finalShipmentItems = [];
                    if( initShipmentItems && initShipmentItems.length > 0 )
                    {
                        for( var initShipmentItemIndex in initShipmentItems )
                        {
                            var initShipmentItem = initShipmentItems[ initShipmentItemIndex ];
                            var finalShipmentItem =
                            {
                                id              :   initShipmentItem.id,
                                fullName        :   initShipmentItem.fullName,
                                orderItemId     :   initShipmentItem.orderItemId,
                                printName       :   initShipmentItem.printName,
                                qtyShipped      :   initShipmentItem.qtyShipped,
                                shipmentId      :   initShipmentItem.shipmentId,
                                shortName       :   initShipmentItem.shortName
                            };

                            finalShipmentItems.push( finalShipmentItem );
                        }
                    }

                    var finalShipment =
                    {
                        id                      :   initShipment.id,
                        operatorId              :   initShipment.operatorId,
                        executeOperatorId       :   initShipment.executeOperatorId,
                        orderId                 :   initShipment.orderId,
                        shipWarehouseId         :   initShipment.shipWarehouseId,
                        shopId                  :   initShipment.shopId,
                        createTime              :   initShipment.createTime,
                        deliveryMethod          :   initShipment.deliveryMethod,
                        qtyTotalItemShipped     :   initShipment.qtyTotalItemShipped,
                        receiveAddress          :   initShipment.receiveAddress,
                        receiveCity             :   initShipment.receiveCity,
                        receiveCountry          :   initShipment.receiveCountry,
                        receiveEmail            :   initShipment.receiveEmail,
                        receiveName             :   initShipment.receiveName,
                        receivePhone            :   initShipment.receivePhone,
                        receivePost             :   initShipment.receivePost,
                        receiveProvince         :   initShipment.receiveProvince,
                        senderAddress           :   initShipment.senderAddress,
                        senderEmail             :   initShipment.senderEmail,
                        senderName              :   initShipment.senderName,
                        senderPhone             :   initShipment.senderPhone,
                        senderPost              :   initShipment.senderPost,
                        shipNumber              :   initShipment.shipNumber,
                        shipStatus              :   initShipment.shipStatus,
                        shipfeeCost             :   initShipment.shipfeeCost,
                        signupTime              :   initShipment.signupTime,
                        totalWeight             :   initShipment.totalWeight,
                        shippingDescription     :   initShipment.shippingDescription,
                        shipmentItems           :   finalShipmentItems
                    };

                    finalShipments.push( finalShipment );
                }

                shipmentService.saveShipmentsChanges
                ({
                    shipments   :   finalShipments,
                    deleteIds   :   $scope.deleteIds
                }).then( function()
                {
                    console.log( '发货单改动保存完毕' );
                    toastr.success('发货单改动保存完毕');
                    $scope.search( $scope.query );
                });
            }
            else
            {
                toastr.warning('保存改动之前确保该界面存在至少一张发货单');
            }

            $('#saveShipmentsChanges').modal('hide');
        };


        /** 向下批量生成快递单号
         */
        $scope.downwardGeneratingShipNumbers = function( shipment )
        {
            var shipments = $scope.page.content;
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