angular.module('ecommApp')

.controller('UserController', ['$rootScope', '$scope', 'User', 'Utils',
    function($rootScope, $scope, User, Utils) {

        $scope.defaultQuery = {
            size: 20,
            sort: ['username']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query, number) {
            User.get({
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
        
    }
]);
