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
                Inventory.getAll({
                    warehouseId: $rootScope.usingWarehouseId,
                    sort: ['productId', 'inventoryBatchId']
                }).then(function(inventories) {
                    $scope.products = Inventory.refresh(inventories);
                });
            }
        });

        $scope.changeWarehouse = function($item) {
            Inventory.getAll({
                warehouseId: $item.id,
                sort: ['productId', 'inventoryBatchId']
            }).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
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
