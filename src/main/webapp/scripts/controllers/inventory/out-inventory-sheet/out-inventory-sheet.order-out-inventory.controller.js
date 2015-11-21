angular.module('ecommApp')

.controller('OrderOutInventoryController', ['$rootScope', '$scope', 'toastr', 'Warehouse', 'Shop', 'orderService', 'Utils', 'OrderItem', 'Auth',
    function($rootScope, $scope, toastr, Warehouse, Shop, orderService, Utils, OrderItem, Auth) {

        var t = $.now();

        $scope.template = {
            confirmOutInventorySheet: {
                url: 'views/inventory/out-inventory-sheet/out-inventory-sheet.order-out-inventory.confirm-out-inventory-sheet.html?' + t
            }
        };

        $scope.defaultQuery = {
            size: 20,
            sort: ['internalCreateTime,desc'],
            warehouse: undefined,
            shop: undefined
        };

        $scope.query = angular.copy($scope.defaultQuery);

        $scope.warehouses = [];
        $scope.shops = [];

        $scope.confirmOutInventorySheetSlideChecked = false;

        $scope.searchData = function(query, number) {
            orderService.getPagedOrdersForOrderDeploy({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort,
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                warehouseIds: Auth.refreshManaged('warehouse'),
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                shopIds: Auth.refreshManaged('shop'),
                deleted: false
            }).then(function(page) {
                console.log(page);
                $.each(page.content, function() {
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.page = page;
                Utils.initList(page, query);
                $scope.selectAll = false;
            });
        };

        Warehouse.getAll({ // 导入所有仓库
            enabled: true,
            sort: ['name'],
            warehouseIds: Auth.refreshManaged('warehouse')
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有店铺
            return Shop.getAll({
                enabled: true,
                sort: ['name'],
                shopIds: Auth.refreshManaged('shop')
            }).then(function(shops) {
                $scope.shops = shops;
            });
        }).then(function() {
            $scope.searchData($scope.query);
        });

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        $scope.search = function(query) {
            $scope.searchData(query);
        };

        $scope.reset = function() {
            $scope.query = angular.copy($scope.defaultQuery);
            $scope.searchData($scope.query);
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
            $('body').css('overflow', 'auto');
            $('div[ps-open="confirmOutInventorySheetSlideChecked"]').css('overflow', 'hidden');
            if ($scope.confirmOutInventorySheetSlideChecked) {
                $('body').css('overflow', 'hidden');
                $('div[ps-open="confirmOutInventorySheetSlideChecked"]').css('overflow', 'auto');
            }
        };

        $scope.selectAllOrders = function() {
            $.each($scope.page.content, function() {
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
