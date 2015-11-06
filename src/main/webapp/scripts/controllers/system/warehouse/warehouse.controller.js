angular.module('ecommApp')

.controller('WarehouseController', ['$scope', 'Warehouse', 'WarehousePosition', 'Utils', 'toastr',
    function($scope, Warehouse, WarehousePosition, Utils, toastr) {

        $scope.defaultQuery = {
            size: 20,
            sort: ['name']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query, number) {
            Warehouse.get({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
                Utils.initList(page, query);
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

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
