angular.module('ecommApp')

.controller('InventoryController', ['$rootScope', '$scope', '$state', 'Warehouse', 'Inventory', 'Auth', 'InventoryBatchItem', 'Utils', 'InventoryBatch',
    function($rootScope, $scope, $state, Warehouse, Inventory, Auth, InventoryBatchItem, Utils, InventoryBatch) {

        var t = $.now();

        $scope.Math = Math;

        $scope.template = {
            outInventoryList: {
                url: '/views/inventory/inventory/inventory.out-inventory-list-slide.html?' + t
            },
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
         * 出库清单
         */

        $scope.outInventoryListSlideCheck = false;

        $scope.toggleOutInventoryList = function() {
            $scope.outInventoryListSlideCheck = !$scope.outInventoryListSlideCheck;
            $('body').css('overflow', 'auto');
            $('div[ps-open="outInventoryListSlideCheck"]').css('overflow', 'hidden');
            if ($scope.outInventoryListSlideCheck) {
                $('body').css('overflow', 'hidden');
                $('div[ps-open="outInventoryListSlideCheck"]').css('overflow', 'auto');
            }
        };

        $scope.defaultBatch = {
            operate: 2, // 出库操作
            type: 2, // 出库状态，已完成
            operateTime: undefined,
            warehouse: undefined,
            user: {
                id: $rootScope.user().id
            },
            executeOperator: {
                id: $rootScope.user().id
            },
            memo: '',
            items: []
        };

        $scope.batch = angular.copy($scope.defaultBatch);

        $scope.addOutInventory = function(product, detail) {
            $scope.detail = detail;
            var warehouse = {
                id: $scope.warehouse.selected.id
            };
            $scope.batch.warehouse = warehouse;

            var item = {
                batchOperate: 2,
                product: product,
                warehouse: $scope.warehouse.selected,
                position: detail.position,
                outBatchId: detail.batchId,
                outBatch: {
                    id: detail.batchId
                },
                user: {
                    id: $rootScope.user().id
                },
                executeOperator: {
                    id: $rootScope.user().id
                },
                actualQuantity: detail.quantity,
                changedQuantity: detail.quantity,
                detail: detail,
                keep: detail.quantity
            };

            detail.enteredOutInventoryList = true;

            $scope.batch.items.push(item);

            $scope.toggleOutInventoryList();
        };

        $scope.removeOutInventory = function(item) {
            $.each($scope.batch.items, function(i) {
                if (this === item) {
                    $scope.batch.items.splice(i, 1);
                    item.detail.enteredOutInventoryList = false;
                    return false;
                }
            });
        };

        $scope.saveOutInventory = function(batch) {
            console.log('saveOutInventory:');
            console.log(batch);

            // 把实际出库数量负数化
            $.each($scope.batch.items, function(){
                this.actualQuantity = -this.actualQuantity;
                this.changedQuantity = -this.changedQuantity;
            });

            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log(batch);
                $scope.toggleOutInventoryList();
                $state.reload('inventory');
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
