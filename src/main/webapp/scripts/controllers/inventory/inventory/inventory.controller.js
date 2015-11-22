angular.module('ecommApp')

.controller('InventoryController', ['$rootScope', '$scope', 'Warehouse', 'Inventory', 'Auth', 'InventoryBatchItem', 'Utils',
    function($rootScope, $scope, Warehouse, Inventory, Auth, InventoryBatchItem, Utils) {

        var t = $.now();

        $scope.Math = Math;

        $scope.template = {
            snapshot: {
                url: '/views/inventory/inventory/inventory.snapshot-slide.html?' + t
            }
        };

        $scope.warehouses = [];
        $scope.warehouse = {
            selected: undefined
        };
        $scope.products = [];

        $scope.defautlQuery = {
            size: 50,
            sort: ['productId', 'inventoryBatchId']
        };

        $scope.query = angular.copy($scope.defautlQuery);

        $scope.searchData = function(query, number) {
            Inventory.get({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort,
                nameOrSku: query.nameOrSku,
                warehouseId: $rootScope.usingWarehouseId
            }, function(page) {
                $scope.page = page;
                Utils.initList(page, query);
                $scope.products = Inventory.refresh(page.content);
            });
        };

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        $scope.search = function(query) {
            $scope.searchData(query);
        };

        $scope.reset = function() {
            $scope.query = angular.copy($scope.defautlQuery);
            $scope.searchData($scope.query);
        };

        Warehouse.getAll({
            enabled: true,
            sort: ['name'],
            warehouseIds: Auth.refreshManaged('warehouse')
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() {
            if ($rootScope.usingWarehouseId) {
                $.each($scope.warehouses, function() {
                    if (this.id === $rootScope.usingWarehouseId) {
                        $scope.warehouse.selected = angular.copy(this);
                    }
                });
            } else {
                $scope.warehouse.selected = $scope.warehouses[0];
                $rootScope.usingWarehouseId = $scope.warehouse.selected.id;
            }
            $scope.search($scope.query);
        });

        $scope.changeWarehouse = function($item) {
            $rootScope.usingWarehouseId = $item.id;
            $scope.search($scope.query);
        };

        /*
         * Snapshot
         */

        $scope.snapshotSlideChecked = false;

        $scope.defautlSnapshotQuery = {
            size: 50,
            sort: ['createTime,desc', 'id,desc']
        };

        $scope.snapshotQuery = angular.copy($scope.defautlSnapshotQuery);

        $scope.searchSnapshotData = function(snapshotQuery, number) {
            InventoryBatchItem.get({
                page: number ? number : 0,
                size: snapshotQuery.size,
                sort: snapshotQuery.sort,
                productId: snapshotQuery.productId,
                warehouseId: snapshotQuery.warehouseId
            }, function(page) {
                $scope.snapshotPage = page;
                $.each(page.content, function() {
                    this.inventorySnapshot = angular.fromJson(this.inventorySnapshot);
                });
                Utils.initList(page, snapshotQuery);
            });
        };

        $scope.turnSnapshotPage = function(number) {
            if (number > -1 && number < $scope.snapshotPage.totalPages) {
                $scope.searchSnapshotData($scope.snapshotQuery, number);
            }
        };

        $scope.searchSnapshot = function(snapshotQuery) {
            $scope.searchSnapshotData(snapshotQuery);
        };

        $scope.toggleSnapshot = function(productId, warehouseId) {
            $scope.snapshotSlideChecked = !$scope.snapshotSlideChecked;
            $('body').css('overflow', 'auto');
            $('div[ps-open="snapshotSlideChecked"]').css('overflow', 'hidden');
            if ($scope.snapshotSlideChecked) {
                $('body').css('overflow', 'hidden');
                $('div[ps-open="snapshotSlideChecked"]').css('overflow', 'auto');
                $scope.snapshotQuery.productId = productId;
                $scope.snapshotQuery.warehouseId = warehouseId;
                $scope.searchSnapshot($scope.snapshotQuery);
            }
        };
    }
]);
