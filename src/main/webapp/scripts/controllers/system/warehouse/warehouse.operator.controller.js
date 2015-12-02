angular.module('ecommApp')

.controller('WarehouseOperatorController', ['$scope', '$state', '$stateParams', 'Warehouse',
    function($scope, $state, $stateParams, Warehouse) {

        var positions = ['default', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'];

        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';
        $scope.defaultPositions = [];
        $scope.action = 'create';

        $scope.isorno = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];

        $scope.defaultWarehouse = {
            enablePosition: {
                label: '是',
                value: true
            },
            enabled: {
                label: '是',
                value: true
            }
        };

        $scope.warehouse = angular.copy($scope.defaultWarehouse);

        $.each(positions, function() {
            $scope.defaultPositions.push({
                name: this
            });
        });

        function initProperties(warehouse) {
            warehouse.enabled = $scope.isorno[warehouse.enabled ? 0 : 1];
            warehouse.enablePosition = $scope.isorno[warehouse.enablePosition ? 0 : 1];
        }

        function refreshProperties(warehouse) {
            warehouse.enabled = warehouse.enabled.value;
            warehouse.enablePosition = warehouse.enablePosition.value;
        }

        if ($stateParams.id && $stateParams.id !== '') {
            $scope.action = 'update';
            Warehouse.get({
                id: $stateParams.id
            }, function(warehouse) {
                $scope.warehouse = warehouse;
                initProperties(warehouse);
            });
        }

        $scope.saveWarehouse = function(warehouse) {

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

            refreshProperties(warehouse);

            Warehouse.save({}, warehouse, function() {
                $state.go('warehouse');
            });

        };

    }
]);
