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