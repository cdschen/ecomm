angular.module('ecommApp')

.controller('WarehouseController', ['$rootScope', '$scope', 'Warehouse', 'Utils',
    function($rootScope, $scope, Warehouse, Utils) {

        var $ = angular.element;

        $scope.defaultQuery = {
            pageSize: 20,
            totalPagesList: [],
            sort: ['name']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query, number) {
            Warehouse.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort,
            }, function(page) {
                $scope.page = page;
                query.totalPagesList = Utils.setTotalPagesList(page);
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
            Warehouse.savePositions(positions).then(function() {
                $('#positionsModal').modal('hide');
                $scope.positions = [];
            });
        };
    }

]);

