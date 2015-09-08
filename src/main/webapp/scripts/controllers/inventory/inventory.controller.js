angular.module('ecommApp')

.controller('InventoryController', ['$rootScope', '$scope', 'Warehouse', 'Inventory',
    function($rootScope, $scope, Warehouse, Inventory) {

        var $ = angular.element;
        $scope.warehouses = [];
        $scope.warehouse = {
            selected: undefined
        };
        $scope.products = [];

        Warehouse.getAll().then(function(warehouses) {
            console.clear();
            $scope.warehouses = warehouses;
        }).then(function() {
            if ($rootScope.usingWarehouseId) {
                $.each($scope.warehouses, function() {
                    if (this.id === $rootScope.usingWarehouseId) {
                        $scope.warehouse.selected = angular.copy(this);
                    }
                });
                Inventory.getAllByWarehouseId($rootScope.usingWarehouseId).then(function(inventories) {
                    $scope.products = Inventory.refresh(inventories);
                });
            }
        });

        $scope.changeWarehouse = function($item) {
            Inventory.getAllByWarehouseId($item.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
        };
    }
]);




