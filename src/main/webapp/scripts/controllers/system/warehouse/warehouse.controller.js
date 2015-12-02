angular.module('ecommApp')

.controller('WarehouseController', ['$scope', 'Warehouse', 'WarehousePosition', 'toastr',
    function($scope, Warehouse, WarehousePosition, toastr) {

        $scope.defaultQuery = {
            page: 0,
            size: 20,
            sort: ['name']
        };

        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query) {
            Warehouse.get({
                page: query.page,
                size: query.size,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
            });
        };

        $scope.searchData($scope.query);

        $scope.positionsModal = function(warehouse) {
            $scope.positions = warehouse.positions;
            $('#positionsModal').modal('show');
        };

        $scope.savePositions = function(positions) {

            var unique = true;
            var positionNames = [];

            $.each(positions, function() {
                positionNames.push(this.name);
            });

            $.each(positions, function() {
                var name = this.name;
                var count = 0;
                $.each(positionNames, function() {
                    if (this === name) {
                        count++;
                    }
                });
                if (count > 1) {
                    unique = false;
                    return false;
                }
            });

            if (!unique) {
                toastr.warning('不能有同名的库位!');
            } else {
                WarehousePosition.savePositions(positions).then(function() {
                    $('#positionsModal').modal('hide');
                    $scope.positions = [];
                });
            }

        };

    }
]);
