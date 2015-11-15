angular.module('ecommApp')

.controller('InventoryOutController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse', 'Product', 'InventoryBatch', 'Utils', 'Inventory', 'Auth',
    function($rootScope, $scope, $state, $stateParams, Warehouse, Product, InventoryBatch, Utils, Inventory, Auth) {

        $scope.warehouses = [];

        $scope.products = [];

        $scope.defaultBatch = {
            operate: 2, // 出库操作
            type: 1, // 出库状态，待完成
            operateTime: undefined,
            warehouse: undefined,
            user: $rootScope.user(),
            memo: '',
            items: []
        };

        $scope.defaultItem = {
            product: undefined,
            warehouse: undefined,
            position: undefined,
            user: $rootScope.user(),
            changedQuantity: 1,
            expireDate: undefined
        };

        $scope.keepQuantity = 0;

        $scope.batches = [];

        $scope.batch = angular.copy($scope.defaultBatch);

        $scope.item = angular.copy($scope.defaultItem);

        function setKeepQuantity(item) {
            if (item.product) {
                if (item.product.positions.length > 0) {
                    if (item.position) {
                        if (item.outBatch) {
                            $scope.keepQuantity = item.outBatch.total;
                        } else {
                            $scope.keepQuantity = item.position.total;
                        }
                    } else {
                        if (item.outBatch) {
                            $scope.keepQuantity = item.outBatch.total;
                        } else {
                            $scope.keepQuantity = item.product.total;
                        }
                    }
                } else {
                    if (item.outBatch) {
                        $scope.keepQuantity = item.outBatch.total;
                    } else {
                        $scope.keepQuantity = item.product.total;
                    }
                }
            } else {
                $scope.keepQuantity = 0;
            }
        }

        function setBatches(item) {
            if (item.product) {
                if (item.product.positions.length > 0) {
                    if (item.position) {
                        $scope.batches = angular.copy(item.position.batches);
                    } else {
                        $scope.batches = angular.copy(item.product.batches);
                    }
                } else {
                    $scope.batches = angular.copy(item.product.batches);
                }
            } else {
                $scope.batches.length = 0;
            }
        }

        Warehouse.getAll({
            enabled: true,
            sort: ['name'],
            warehouseIds: Auth.refreshManaged('warehouse')
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
            if ($stateParams.id && $stateParams.id !== '') {
                $.each(warehouses, function() {
                    if (this.id === parseInt($stateParams.id)) {
                        $scope.batch.warehouse = angular.copy(this);
                        return false;
                    }
                });
            }
        }).then(function() {
            if ($scope.batch.warehouse) {
                Inventory.getAll({
                    warehouseId: $scope.batch.warehouse.id,
                    sort: ['productId', 'inventoryBatchId']
                }).then(function(inventories) {
                    $scope.products = Inventory.refresh(inventories);
                });
            }
        });

        $scope.changeWarehouse = function($item) {
            $scope.batch.items.length = 0;
            Inventory.getAll({
                warehouseId: $item.id,
                sort: ['productId', 'inventoryBatchId']
            }).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
                $scope.item = angular.copy($scope.defaultItem);
                setBatches($scope.item);
                setKeepQuantity($scope.item);
            });
        };

        $scope.changeProduct = function($item) {
            console.log('选择商品');
            console.log($item);
            $scope.item.position = undefined;
            $scope.item.outBatch = undefined;
            setBatches($scope.item);
            setKeepQuantity($scope.item);
        };

        $scope.changePosition = function($item) {
            console.log('选择库位');
            console.log($item);
            if (angular.isString($item) && $item === 'remove') {
                $scope.item.position = undefined;
            }
            $scope.item.outBatch = undefined;
            setBatches($scope.item);
            setKeepQuantity($scope.item);
        };

        $scope.changeOutBatch = function($item) {
            console.log('选择批次');
            console.log($item);
            if (angular.isString($item) && $item === 'remove') {
                $scope.item.outBatch = undefined;
            }
            setKeepQuantity($scope.item);
        };

        $scope.saveItem = function(item, itemAddForm) {
            item.warehouse = $scope.batch.warehouse;
            item.$field = 't' + Date.parse(Date());
            console.log('saveItem:');
            console.log(item);
            $scope.batch.items.push(angular.copy(item));
            Inventory.refrechProducts($scope.products, item, 'add');
            console.log('$scope.products:');
            console.log($scope.products);
            //console.log($scope.products);
            $scope.item = angular.copy($scope.defaultItem);
            itemAddForm.$setPristine();
            setBatches($scope.item);
            setKeepQuantity($scope.item);
        };

        $scope.removingItem = undefined;

        $scope.showRemoveItem = function(item, $index) {
            console.clear();
            console.log('showRemoveItem $index: ' + $index);
            console.log(item);

            $scope.removingItem = angular.copy(item);
            $scope.removingItem.$index = $index;
            $('#itemDeleteModal').modal('show');
        };

        $scope.removeItem = function() {
            console.clear();
            console.log('removeItem:');
            console.log($scope.removingItem);

            if (angular.isDefined($scope.removingItem)) {
                $scope.batch.items.splice($scope.removingItem.$index, 1);
                Inventory.refrechProducts($scope.products, $scope.removingItem, 'remove');
                $scope.item = angular.copy($scope.defaultItem);
                $scope.itemAddForm.$setPristine();
                setBatches($scope.item);
                setKeepQuantity($scope.item);
                $scope.removingItem = undefined;
                $('#itemDeleteModal').modal('hide');
            }
        };

        $scope.saveBatch = function(batch) {
            console.clear();
            console.log('saveBatch:');
            console.log(batch);

            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log(batch);
                $state.go('inventory');
            });
        };

    }
]);
