angular.module('ecommApp')

.controller('ProcessController', ['$scope', 'Process', 'Utils',
    function($scope, Process, Utils) {

        $scope.defaultQuery = {
            size: 20,
            sort: ['name']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query, number) {
            Process.get({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort,
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

    }
]);
