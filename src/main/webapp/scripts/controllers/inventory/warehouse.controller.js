angular.module('ecommApp')

.controller('WarehouseController', ['$rootScope', '$scope', 'Warehouse', 'Utils',
    function($rootScope, $scope, Warehouse, Utils) {

        var $ = angular.element;
        $scope.totalPagesList = [];
        $scope.pageSize = 20;

        Warehouse.get({
            page: 0,
            size: $scope.pageSize
        }, function(page) {
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        });

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Warehouse.get({
                    page: number,
                    size: $scope.pageSize
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.positionsModal = function(warehouse) {
            $scope.positions = warehouse.positions;
            $('#positionsModal').modal('show');
        };

        $scope.savePositions = function(positions) {
            console.log(positions);
            Warehouse.savePositions(positions).then(function() {
                $('#positionsModal').modal('hide');
                $scope.positions = [];
            }, function(err) {
                console.log(err);
            });
        };
    }
])

.controller('WarehouseOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse',
    function($rootScope, $scope, $state, $stateParams, Warehouse) {

        $scope.warehouse = {};
        $scope.action = 'create';

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
            if (warehouse.enablePosition) {
                warehouse.positions = [{
                    name: 'A'
                }, {
                    name: 'B'
                }, {
                    name: 'C'
                }, {
                    name: 'D'
                }, {
                    name: 'E'
                }, {
                    name: 'F'
                }, {
                    name: 'G'
                }, {
                    name: 'H'
                }, {
                    name: 'I'
                }, {
                    name: 'J'
                }, {
                    name: 'K'
                }, {
                    name: 'L'
                }, {
                    name: 'M'
                }, {
                    name: 'N'
                }, {
                    name: 'O'
                }, {
                    name: 'P'
                }, {
                    name: 'Q'
                }, {
                    name: 'R'
                }, {
                    name: 'S'
                }, {
                    name: 'T'
                }, {
                    name: 'U'
                }, {
                    name: 'V'
                }, {
                    name: 'W'
                }, {
                    name: 'X'
                }, {
                    name: 'Y'
                }, {
                    name: 'Z'
                }];
            } else {
                warehouse.positions = [];
            }
            Warehouse.save({}, warehouse, function() {
                $state.go('warehouse');
            }, function(err) {
                console.log(err);
            });

        };

        $scope.remove = function() {
            Warehouse.remove({
                id: $stateParams.id
            }, {}, function() {
                $state.go('warehouse');
            }, function(err) {
                console.log(err);
            });
        };

    }
]);
