angular.module('ecommApp')

.controller('UserController', ['$rootScope', '$scope', 'User', 'Utils',
    function($rootScope, $scope, User, Utils) {

        $scope.defaultQuery = {
            pageSize: 20,
            totalPagesList: [],
            sort: ['username']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query, number) {
            User.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort
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
    }
]);
