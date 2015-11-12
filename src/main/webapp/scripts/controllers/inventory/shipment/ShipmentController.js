angular.module('ecommApp')

.controller('ShipmentController', ['$scope', '$rootScope', 'toastr', 'Warehouse', 'Shop', 'courierService', 'Utils', 'shipmentService', 'Auth',
    function($scope, $rootScope, toastr, Warehouse, Shop, courierService, Utils, shipmentService, Auth) {

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
            orientation: 'top left',
            todayHighlight: true,
        });

        $scope.selectedShipment = {};

        $scope.shipmentCompleteSlideChecked = false;
        $scope.shipmentsCompleteSlideChecked = false;

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultQuery = {};
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.warehouses = [];
        $scope.shipments = [];
        $scope.shops = [];
        $scope.couriers = [];
        $scope.shipStatus = [
            {
                id : 1, name : '待打印'
            },
            {
                id : 2, name : '已发出'
            },
            {
                id : 3, name : '已签收'
            },
            {
                id : 4, name : '派送异常'
            },
            {
                id : 5, name : '作废'
            }
        ];

        /* 查询发货单分页数据所需查询参数 */
        function getQueryParamJSON()
        {
            return {
                page: 0,
                size: $scope.pageSize,
                sort: ['createTime,desc'],
                shipWarehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                orderId: $scope.query.orderId ? $scope.query.orderId : null,
                courierId: $scope.query.courier ? $scope.query.courier.id : null,
                shipNumber: $scope.query.shipNumber ? $scope.query.shipNumber : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                shipStatus: $scope.query.shipStatus ? $scope.query.shipStatus.id : null,
                createTimeStart: $scope.query.createTimeStart ? $scope.query.createTimeStart : null,
                createTimeEnd: $scope.query.createTimeEnd ? $scope.query.createTimeEnd : null,
                lastUpdateStart: $scope.query.lastUpdateStart ? $scope.query.lastUpdateStart : null,
                lastUpdateEnd: $scope.query.lastUpdateEnd ? $scope.query.lastUpdateEnd : null,
                pickupTimeStart: $scope.query.pickupTimeStart ? $scope.query.pickupTimeStart : null,
                pickupTimeEnd: $scope.query.pickupTimeEnd ? $scope.query.pickupTimeEnd : null,
                signupTimeStart: $scope.query.signupTimeStart ? $scope.query.signupTimeStart : null,
                signupTimeEnd: $scope.query.signupTimeEnd ? $scope.query.signupTimeEnd : null
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
            shipmentService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        });

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.query);
            shipmentService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.query = angular.copy($scope.defaultQuery);
            console.log($scope.query);
            shipmentService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
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

        /* 批量操作 */
        $scope.batchManipulation = function()
        {
            var shipments = $scope.page.content;
            shipmentService.selectedShipments.length = 0;
            $.each(shipments, function(){
                var shipment = this;
                if (shipment.isSelected) {
                    shipmentService.selectedShipments.push(angular.copy(shipment));
                }
            });
            if (shipmentService.selectedShipments.length > 0)
            {
                if($scope.batchManipulationValue === 'shipmentComplete')
                {
                    $scope.showShipmentsCompleteSlide();
                }
                else if($scope.batchManipulationValue === 'shipmentExport')
                {
                    toastr.info('发货单导出！');
                }
                else if($scope.batchManipulationValue === 'shipmentPrint')
                {
                    toastr.info('发货单打印！');
                }
                else if($scope.batchManipulationValue === 'shipmentObsolete')
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


        /**
         * 单个发货单作废 : 开始
         */
        $scope.showObsoleteShipmentModal = function( shipment )
        {
            $scope.selectedShipment = shipment;

            $('#obsoleteShipment').modal('show');
        };

        /* 将选中发货单作废 */
        $scope.obsoleteShipment = function()
        {
            /* 作废状态 */
            $scope.selectedShipment.shipStatus = 5;

            shipmentService.save({
                action: 'update'
            }, $scope.selectedShipment, function( shipment ) {

                    console.log( shipment );

                    shipmentService.get( getQueryParamJSON(), function(page) {
                        console.log('page:');
                        console.log(page);
                        $scope.page = page;
                        $scope.totalPagesList = Utils.setTotalPagesList(page);
                    });
                });

            $('#obsoleteShipment').modal('hide');

            toastr.error('作废选中发的货单！');
        };
        /**
         * 单个发货单作废 : 结束
         */


        /**
         * 多个发货单作废 : 开始
         */
        /* 将批量选中的货单作废 */
        $scope.obsoleteShipments = function()
        {
            for( var shipmentIndex in shipmentService.selectedShipments )
            {
                /* 作废状态 */
                shipmentService.selectedShipments[shipmentIndex].shipStatus = 5;
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
                        $scope.totalPagesList = Utils.setTotalPagesList(page);
                    });
                });

            $('#obsoleteShipments').modal('hide');
            toastr.error('作废批量选中的发货单！');
        };
        /**
         * 多个发货单作废 : 结束
         */


        /**
         * 单个发货单完成 : 开始
         */
        $scope.showShipmentCompleteSlide = function( shipment )
        {
            shipmentService.setSelectedShipment( angular.copy( shipment ) );

            executeOperationReviewCompleteShipment('VERIFY');

            $scope.toggleShipmentCompleteSlide();
        };


        function executeOperationReviewCompleteShipment(action)
        {
            var reviewDTO = {
                action                      : action,
                shipment                    : shipmentService.getSelectedShipment(),
                dataMap : {
                    executeOperatorId                : $rootScope.user().id
                },
                ignoredMap : {
                    emptyCourierError : false,
                    emptyShipNumberError : false,
                    emptyReceiveNameError : false,
                    emptyReceivePhoneError : false,
                    emptyReceiveAddressError : false
                }
            };
            console.log('Before Operation Review:');
            console.log(reviewDTO);
            shipmentService.confirmOperationReviewWhenCompleteShipment(reviewDTO).then(function(review){
                console.log('After Operation Review Complete Shipment:');
                console.log(review);
            });
        }
        /**
         * 单个发货单完成 : 结束
         */


        /**
         * 多个发货单完成 : 开始
         */
        $scope.showShipmentsCompleteSlide = function()
        {
            executeOperationReviewCompleteShipments('VERIFY');

            $scope.toggleShipmentsCompleteSlide();
        };

        function executeOperationReviewCompleteShipments(action)
        {
            var reviewDTO = {
                action                      : action,
                shipments                    : shipmentService.selectedShipments,
                dataMap : {
                    executeOperatorId                : $rootScope.user().id
                },
                ignoredMap : {
                    emptyCourierError : false,
                    emptyShipNumberError : false,
                    emptyReceiveNameError : false,
                    emptyReceivePhoneError : false,
                    emptyReceiveAddressError : false
                }
            };
            console.log('Before Operation Review:');
            console.log(reviewDTO);
            shipmentService.confirmOperationReviewWhenCompleteShipments(reviewDTO).then(function(review){
                console.log('After Operation Review Complete Shipments:');
                console.log(review);
            });
        }
        /**
         * 多个发货单完成 : 结束
         */

    }
]);
