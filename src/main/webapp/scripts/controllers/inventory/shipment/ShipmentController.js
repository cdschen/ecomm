angular.module('ecommApp')

.controller('ShipmentController', ['$scope', '$rootScope', 'toastr', '$modal', 'filterFilter', 'Warehouse', 'Shop', 'orderService', 'courierService', 'Process', 'Utils', 'Inventory', 'OrderItem', 'shipmentService',
    function($scope, $rootScope, toastr, $modal, filterFilter, Warehouse, Shop, orderService, courierService, Process, Utils, Inventory, OrderItem, shipmentService) {

        $scope.template = {
            shipmentComplete: {
                url: 'views/inventory/shipment/shipment-complete.html?' + (new Date())
            }
        };

        var $ = angular.element;

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
                id : 1, name : '待取件'
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

        $scope.shipmentCompleteSlideChecked = false;

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
            sort: ['name']
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
                sort: ['name']
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
        };


        $scope.isCheckedAll = false;
        $scope.batchManipulationValue = 'batchManipulation';
        $scope.selectedCourier = {};
        $scope.startCourierNumber = '';

        $scope.checkAllOrders = function()
        {
            for( var order in $scope.page.content )
            {
                $scope.page.content[order].isSelected = $scope.isCheckedAll;
            }
        };

        /* 批量操作 */
        $scope.batchManipulation = function()
        {
            var orders = $scope.page.content;
            orderService.selectedOrders.length = 0;
            $.each(orders, function(){
                var order = this;
                if (order.isSelected) {
                    orderService.selectedOrders.push(angular.copy(order));
                }
            });
            if (orderService.selectedOrders.length > 0)
            {
                if($scope.batchManipulationValue == 'shipmentExport')
                {
                    toastr.info('发货单导出！');
                }
                else if($scope.batchManipulationValue == 'shipmentPrint')
                {
                    toastr.info('发货单打印！');
                }
                else if($scope.batchManipulationValue == 'shipmentObsolete')
                {
                    toastr.info('发货单作废！');
                }
            }
            else
            {
                toastr.error('请选择一到多个发货单来继续！');
            }


            $scope.batchManipulationValue = 'batchManipulation';
        };

    }
]);
