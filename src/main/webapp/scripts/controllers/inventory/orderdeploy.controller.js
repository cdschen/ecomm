angular.module('ecommApp')

.controller('OrderDeployController', ['$scope', 'Warehouse', 'Shop', 'orderService', 'Process', 'Utils', 'Inventory',
    function($scope, Warehouse, Shop, orderService, Process, Utils, Inventory) {

        $scope.template = {
            status: {
                url: 'views/inventory/orderdeploy/order-deploy.status.html?' + (new Date())
            }
        };

        var $ = angular.element;
        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultQuery = {
            warehouse: undefined,
            shop: undefined,
            statuses: [],
        };
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.warehouses = [];
        $scope.shops = [];
        $scope.processes = [];
        $scope.inventory = {}; // 库存对象，里面每一个子属性都是一个仓库，仓库的值是一个归类好的产品数组
        $scope.statusSlideChecked = false;

        // 将所有店铺过滤，拿出所有配置了配送状态的店铺的ID
        $scope.selectAllShops = function(shops) {
            $.each(shops, function() {
                var shop = this;
                if (shop.deployStep) {
                    var exist = false
                    $.each($scope.query.statuses, function() {
                        if (this.id === shop.deployStep.id) {
                            exist = true;
                            return false;
                        }
                    });
                    if (!exist) {
                        $scope.query.statuses.push(shop.deployStep);
                    }
                }
            });
        };

        /*.then(function() { // 导入所有流程
            return Process.getAll({ 
                deleted: false,
                objectType: 1
            }).then(function(processes) {
                $scope.processes = processes;
                Process.initStatus(processes);
            });
        })*/

        Warehouse.getAll({ // 导入所有仓库
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有店铺
            return Shop.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(shops) {
                $scope.shops = shops;
                $scope.selectAllShops(shops);
                console.log($scope.query.statuses);
            });
        }).then(function() { // 导入所有库存, 按仓库分组
            return Inventory.getAll({
                sort: ['productId', 'inventoryBatchId']
            }).then(function(inventories) {
                $scope.inventory = Inventory.refreshByWarehouse($scope.warehouses, inventories);
                $.each($scope.inventory, function(key, value) {
                    var warehouse = value;
                    warehouse.products = Inventory.refresh(warehouse.inventories);
                });
                console.log('inventory:');
                console.log($scope.inventory);
            });
        }).then(function() {
            orderService.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                statusIds: Process.refreshStatus($scope.query.statuses),
                deleted: false
            }, function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $.each(page.content, function() {
                    Shop.initShopDefaultTunnel(this.shop);
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        });

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.query);
            orderService.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                statusIds: Process.refreshStatus($scope.query.statuses),
                deleted: false
            }, function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $.each(page.content, function() {
                    Shop.initShopDefaultTunnel(this.shop);
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.query = angular.copy($scope.defaultQuery);
            $scope.selectAllShops($scope.shops);
            console.log($scope.query);
            orderService.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                statusIds: Process.refreshStatus($scope.query.statuses),
                deleted: false
            }, function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $.each(page.content, function() {
                    Shop.initShopDefaultTunnel(this.shop);
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        // status
        $scope.closeStatusSlide = function() {
            $scope.statusSlideChecked = false;
        };

        $scope.loadStatus = function() {
            $scope.statusSlideChecked = true;
        };

        $scope.selectStatus = function(step) {
            if (step.selected && step.selected === true) {
                step.selected = false;
                $.each($scope.query.statuses, function(i) {
                    if (this.id === step.id) {
                        $scope.query.statuses.splice(i, 1);
                        return false;
                    }
                });
            } else {
                step.selected = true;
                $scope.query.statuses.push(step);
            }
            console.log('selectStatus:');
            console.log($scope.query.statuses);
        };

    }
]);
