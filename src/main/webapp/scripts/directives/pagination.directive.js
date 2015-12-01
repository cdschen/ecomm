angular.module('ecommApp')

.directive('coPagination', ['Utils', function(Utils) {

    var t = $.now();
    return {
        name: 'coPagination',
        scope: {
            page: '=',
            query: '=',
            searchData: '&',
            mode: '@'
        },
        restrict: 'A',
        templateUrl: 'scripts/directives/pagination.template.html?' + t,
        replace: true,
        link: function($scope) {
            $scope.$watch('page', function(page) {
                if (page) {
                    Utils.initList($scope.page, $scope.query);
                }
            });
            $scope.turnPage = function(page) {
                $scope.query.page = page;
                $scope.searchData($scope.query);
            };
        }
    };

}]);
