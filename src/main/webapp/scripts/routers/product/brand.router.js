angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('brand', {
            parent: 'site',
            url: '/brands',
            views: {
                'content@': {
                    templateUrl: 'views/product/brand/brand.html?' + t,
                    controller: 'BrandController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.BRAND_ADMIN]
            }
        });

}]);
