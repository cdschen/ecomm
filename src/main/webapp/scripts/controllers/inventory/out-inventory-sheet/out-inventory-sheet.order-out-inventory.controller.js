angular.module('ecommApp')

.controller('OrderOutInventoryController', ['$rootScope', '$scope', 'toastr', 'Warehouse', 'Shop', 'orderService', 'Utils', 'OrderItem', 'Auth',
    function($rootScope, $scope, toastr, Warehouse, Shop, orderService, Utils, OrderItem, Auth) {

        var t = $.now();

        $scope.template = {
            confirmOutInventorySheet: {
                url: 'views/inventory/out-inventory-sheet/out-inventory-sheet.order-out-inventory.confirm-out-inventory-sheet.html?' + t
            }
        };

        $scope.pageSize = 20;
        $scope.totalPagesList = [];

        $scope.defaultQuery = {
            warehouse: undefined,
            shop: undefined
        };
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.warehouses = [];
        $scope.shops = [];
        $scope.confirmOutInventorySheetSlideChecked = false;

        $scope.searchData = function(){
            orderService.getPagedOrdersForOrderDeploy({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                warehouseIds: Auth.refreshManaged('warehouse'),
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                shopIds: Auth.refreshManaged('shop'),
                deleted: false
            }).then(function(page) {
                $.each(page.content, function() {
                    Shop.initShopDefaultTunnel(this.shop);
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.selectAll = false;
            });
        };

        Warehouse.getAll({ // 导入所有仓库
            deleted: false,
            sort: ['name'],
            warehouseIds: Auth.refreshManaged('warehouse')
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有店铺
            return Shop.getAll({
                deleted: false,
                sort: ['name'],
                shopIds: Auth.refreshManaged('shop')
            }).then(function(shops) {
                $scope.shops = shops;
            });
        }).then(function() {
            $scope.searchData();
        });

        $scope.search = function() {
            $scope.searchData();
        };

        $scope.reset = function() {
            $scope.query = angular.copy($scope.defaultQuery);
            $scope.searchData();
        };

        $scope.selectItemWarehouse = function(item, $item) {
            item.warehouseId = $item.id;
            OrderItem.save({}, item, function(item) {
                console.log('selectItemWarehouse complete');
                console.log(item);
            });
        };

        $scope.toggleOutInventorySheetSlide = function() {
            $scope.confirmOutInventorySheetSlideChecked = !$scope.confirmOutInventorySheetSlideChecked;
        };

        $scope.selectAllOrders = function(){
            $.each($scope.page.content, function(){
                this.checked = $scope.selectAll;
            });
        };

        $scope.selectOperatorValue = 'default';

        $scope.selectOperator = function() {
            var orders = $scope.page.content;
            orderService.selectedOrders.length = 0;
            $.each(orders, function() {
                var order = this;
                if (order.checked) {
                    orderService.selectedOrders.push(angular.copy(order));
                }
            });
            if (orderService.selectedOrders.length > 0) {
                if ($scope.selectOperatorValue === 'confirmOutInventorySheet') {
                    $scope.toggleOutInventorySheetSlide();
                    var reviewDTO = {
                        orders: orderService.selectedOrders,
                        assignWarehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                        ignoredMap: {
                            'productInventoryNotEnough': false,
                            'orderExistOutInventorySheet': false
                        }
                    };
                    console.log('reviewDTO:');
                    console.log(reviewDTO);
                    orderService.confirmOrderWhenGenerateOutInventory(reviewDTO).then(function(review) {
                        console.log('review:');
                        console.log(review);
                    });
                }
            } else {
                toastr.error('请选择一到多个订单来继续！');
            }

            $scope.selectOperatorValue = 'default';
        };

    }
]);
