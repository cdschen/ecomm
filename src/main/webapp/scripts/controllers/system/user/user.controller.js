angular.module('ecommApp')

.controller('UserController', ['$rootScope', '$scope', 'User',
    function($rootScope, $scope, User) {

        $scope.defaultQuery = {
            page: 0,
            size: 20,
            sort: ['username']
        };

        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query) {
            User.get({
                page: query.page,
                size: query.size,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
            });
        };

        $scope.searchData($scope.query);

    }
]);
