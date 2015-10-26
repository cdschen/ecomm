angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('category', {
            parent: 'site',
            url: '/categories',
            views: {
                'content@': {
                    templateUrl: 'views/product/category/category.html?' + t,
                    controller: 'CategoryController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN]
            }
        });

}]);
