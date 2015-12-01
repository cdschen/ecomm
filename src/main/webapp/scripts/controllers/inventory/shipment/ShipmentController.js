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
            statuses: [],
        };
        $scope.query = angular.copy( $scope.defaultQuery );
        $scope.warehouses = [];
        $scope.shipments = [];
        $scope.shops = [];
        $scope.couriers = [];
        $scope.shipStatus = [
            {
                id : 1, name : '已打印'
            },
            {
                id : 2, name : '配货完成'
            },
            {
                id : 3, name : '已发出'
            },
            {
                id : 4, name : '已签收'
            },
            {
                id : 5, name : '派送异常'
            },
            {
                id : 6, name : '作废'
            }
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

        /* 批量操作 */
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
            if (shipmentService.selectedShipments.length > 0)
            {
                if($scope.batchManipulationValue === 'shipmentComplete')
                {
                    //$scope.showShipmentsCompleteSlide();
                }
                else if($scope.batchManipulationValue === 'shipmentExport')
                {
                    window.location.href = '/api/shipment/export?ids=' + ids;
                    toastr.info('批量导出发货单');
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
                        Utils.initList(page, $scope.query);
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
                        Utils.initList(page, $scope.query);
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
