'use strict';

angular.module('ecommApp')

.controller('InventoryController', ['$rootScope', '$scope', 'Warehouse', 'Inventory',
    function($rootScope, $scope, Warehouse, Inventory) {

        $scope.warehouses = [];
        $scope.warehouse = {};
        $scope.products = [];

        Warehouse.getAll().then(function(warehouses) {
            $scope.warehouses = warehouses;
            $scope.warehouse = angular.copy(warehouses[0]);
        }).then(function() {
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
        });

        $scope.changeWarehouse = function() {
            //console.log($scope.warehouse.id);
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
        };
    }
])

.controller('InventoryEnterController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse', 'Product', 'InventoryBatch',
    function($rootScope, $scope, $state, $stateParams, Warehouse, Product, InventoryBatch) {

        var $ = angular.element;
        $scope.warehouses = [];
        $scope.warehouse = {};
        $scope.products = [];
        $scope.batch = {
            operate: 1,
            user: {
                id: $rootScope.user().id
            },
            operateTime: undefined,
            memo: '',
            items: []
        };
        $scope.item = {
            changedQuantity: 1
        };
        $scope.product = {
            selected: undefined
        };
        $scope.position = {
            selected: undefined
        };

        function setPositionSelected() {
            console.log($scope.warehouse);
            if ($scope.warehouse.enablePosition === true) {
                $scope.position.selected = $scope.warehouse.positions[0];
            } else {
                $scope.position.selected = undefined;
            }
        }

        $scope.changeWarehouse = function() {
            $scope.batch.items = [];
            angular.forEach($scope.warehouses, function(warehouse) {
                if (warehouse.id === $scope.warehouse.id) {
                    $scope.warehouse = angular.copy(warehouse);
                    return;
                }
            });
            setPositionSelected();
            $scope.batch.warehouseId = $scope.warehouse.id;
        };

        var $date = $('#sandbox-container input').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'top left',
            todayHighlight: true,
        }).on('changeDate', function(e) {
            console.log(e.format());
            if (e.date && e.date !== '') {
                $scope.$apply(function() {
                    $scope.item.expireDate = e.format();
                });
            }
        });

        Warehouse.getAll().then(function(warehouses) {
            $scope.warehouses = warehouses;
            if ($stateParams.id && $stateParams.id !== '') {
                angular.forEach(warehouses, function(warehouse) {
                    if (warehouse.id === $stateParams.id) {
                        $scope.warehouse = angular.copy(warehouse);
                        return;
                    }
                });
            } else {
                $scope.warehouse = angular.copy(warehouses[0]);
            }
            setPositionSelected();
            $scope.batch.warehouseId = $scope.warehouse.id;
        }).then(function() {
            Product.getAll().then(function(products) {
                $scope.products = products;
            });
        });

        $scope.add = function() {
            var item = {
                product: $scope.product.selected,
                warehouseId: $scope.warehouse.id,
                position: $scope.position.selected,
                userId: $rootScope.user().id,
                changedQuantity: $scope.item.changedQuantity,
                expireDate: $scope.item.expireDate
            };
            $scope.batch.items.push(item);
            $scope.product.selected = undefined;
            setPositionSelected();
            $scope.item = {
                changedQuantity: 1,
                expireDate: undefined
            };
            $date.datepicker('clearDates');
        };

        $scope.remove = function($index) {
            $scope.batch.items.splice($index, 1);
        };

        $scope.save = function() {
            console.log($scope.batch);
            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log(batch);
                $state.go('inventory');
            });
        };

    }
])

.controller('InventoryOutController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse', 'Product', 'InventoryBatch', 'Utils', 'Inventory',
    function($rootScope, $scope, $state, $stateParams, Warehouse, Product, InventoryBatch, Utils, Inventory) {

        $scope.warehouses = [];
        $scope.warehouse = {};
        $scope.products = [];
        $scope.batch = {
            operate: 2,
            user: {
                id: $rootScope.user().id
            },
            operateTime: undefined,
            memo: '',
            items: []
        };
        $scope.quantity = 0;
        $scope.item = {
            changedQuantity: 1
        };
        $scope.product = {
            selected: undefined
        };
        $scope.position = {
            selected: undefined
        };
        $scope.selectedBatch = {
            selected: undefined
        };
        $scope.batches = [];

        function setQuantity() {
            if ($scope.product.selected) {
                // console.log('有选商品');
                if ($scope.product.selected.positions.length > 0) {
                    console.log('有库位');
                    if ($scope.position.selected) {
                        // console.log('有选库位');
                        $scope.quantity = $scope.position.selected.total;
                        if ($scope.selectedBatch.selected) {
                            $scope.quantity = $scope.selectedBatch.selected.total;
                        } else {
                            //console.log('没选批次');
                        }
                    } else {
                        // console.log('没选库位');
                    }
                } else {
                    //console.log('没库位');
                    $scope.quantity = $scope.product.selected.total;
                    if ($scope.selectedBatch.selected) {
                        $scope.quantity = $scope.selectedBatch.selected.total;
                    } else {
                        //console.log('没选批次');
                    }
                }
            } else {
                //console.log('没选商品');
                $scope.quantity = 0;
            }
        }

        function setPositionSelected() {
            if ($scope.product.selected && $scope.product.selected.positions.length > 0) {
                $scope.position.selected = $scope.product.selected.positions[0];
            } else {
                $scope.position.selected = undefined;
            }
        }

        function setChangedQuantity() {
            $scope.item.changedQuantity = 1;
        }

        function setSelectedBatch() {
            $scope.selectedBatch.selected = undefined;
        }

        function setBatches() {
            if ($scope.product.selected) {
                if ($scope.product.selected.positions.length > 0) {
                    if ($scope.position.selected) {
                        //console.log('装入所选库位的批次列表')
                        $scope.batches = $scope.position.selected.batches;
                    } else {
                        //console.log('装入第一个库位的批次列表')
                        $scope.batches = $scope.product.selected.positions[0].batches;
                    }
                } else {
                    //console.log('装入所有批次列表')
                    $scope.batches = $scope.product.selected.batches;
                }
            } else {
                $scope.batches = [];
            }
        }

        Warehouse.getAll().then(function(warehouses) {
            $scope.warehouses = warehouses;
            if ($stateParams.id && $stateParams.id !== '') {
                angular.forEach(warehouses, function(warehouse) {
                    if (warehouse.id === $stateParams.id) {
                        $scope.warehouse = angular.copy(warehouse);
                        return;
                    }
                });
            } else {
                $scope.warehouse = angular.copy(warehouses[0]);
            }
            setPositionSelected();
            $scope.batch.warehouseId = $scope.warehouse.id;
        }).then(function() {
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
        });

        $scope.changeWarehouse = function() {
            $scope.batch.items = [];
            angular.forEach($scope.warehouses, function(warehouse) {
                if (warehouse.id === $scope.warehouse.id) {
                    $scope.warehouse = angular.copy(warehouse);
                    return;
                }
            });
            $scope.batch.warehouseId = $scope.warehouse.id;
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
                $scope.product.selected = undefined;
                setSelectedBatch();
                setPositionSelected();
                setBatches();
                setQuantity();
                setChangedQuantity();
            });
        };

        $scope.changeProduct = function() {
            console.log('选择商品');
            console.log($scope.product.selected);
            setSelectedBatch();
            setPositionSelected();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.changePosition = function() {
            console.log('选择库位');
            console.log($scope.position.selected);
            setSelectedBatch();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.changeBatch = function() {
            console.log('选择批次');
            console.log($scope.selectedBatch.selected);
            setQuantity();
            setChangedQuantity();
        };

        $scope.add = function() {
            var item = {
                product: $scope.product.selected,
                warehouseId: $scope.warehouse.id,
                position: $scope.position.selected,
                userId: $rootScope.user().id,
                changedQuantity: $scope.item.changedQuantity,
                outBatch: $scope.selectedBatch.selected,
                $index: 't' + Date.parse(Date())
            };
            console.log(item);
            $scope.batch.items.push(item);

            $scope.products = Inventory.refrechProducts($scope.products, item, 'add');

            $scope.product.selected = undefined;
            setSelectedBatch();
            setPositionSelected();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.remove = function($index, item) {
            console.log(item);
            $scope.batch.items.splice($index, 1);
            $scope.products = Inventory.refrechProducts($scope.products, item, 'remove');

            $scope.product.selected = undefined;
            setSelectedBatch();
            setPositionSelected();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.save = function() {
            //$scope.batch.operateTime = Utils.convertLocaleDateToServer(new Date());
            console.log($scope.batch);
            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log(batch);
                $state.go('inventory');
            });
        };

    }
]);
