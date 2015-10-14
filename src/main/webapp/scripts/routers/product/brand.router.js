angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = new Date().getTime();

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
                roles: [ROLES.sysadmin],
                authorities: []
            }
        });

}]);
