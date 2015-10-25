angular.module('ecommApp')

.controller('WarehouseOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse',
    function($rootScope, $scope, $state, $stateParams, Warehouse) {

        var $ = angular.element;
        var positions = ['default', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';
        $scope.action = 'create';

        $scope.warehouse = {
            deleted: false
        };
        $scope.defaultPositions = [];

        $.each(positions, function() {
            $scope.defaultPositions.push({
                name: this
            });
        });

        if ($stateParams.id && $stateParams.id !== '') {
            $scope.action = 'update';
            Warehouse.get({
                id: $stateParams.id
            }, {}, function(warehouse) {
                $scope.warehouse = warehouse;
                console.log(warehouse);
            });
        }

        $scope.save = function(warehouse) {
            console.log(warehouse);
            if ($scope.action === 'create') {
                if (warehouse.enablePosition) {
                    warehouse.positions = $scope.defaultPositions;
                } else {
                    warehouse.positions = [$scope.defaultPositions[0]];
                }
            } else if ($scope.action === 'update') {
                if (warehouse.enablePosition) {
                    if (warehouse.positions.length === 1 || warehouse.positions.length === 0) {
                        warehouse.positions = $scope.defaultPositions;
                    }
                } else {
                    warehouse.positions = [$scope.defaultPositions[0]];
                }
            }

            Warehouse.save({}, warehouse, function() {
                $state.go('warehouse');
            });

        };

        $('#warehouseDeleteModal').on('hidden.bs.modal', function(){
            Warehouse.save({}, $scope.warehouse, function() {
                $state.go('warehouse');
            });
        });

        $scope.showRemoveWarehouse = function() {
            $('#warehouseDeleteModal').modal('show');
        };

        $scope.removeWarehouse = function() {
            $scope.warehouse.deleted = true;
            $('#warehouseDeleteModal').modal('hide'); 
        };

    }
]);
