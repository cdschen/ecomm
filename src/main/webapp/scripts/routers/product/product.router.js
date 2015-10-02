angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    $stateProvider
        .state('product', {
            parent: 'site',
            url: '/products',
            views: {
                'content@': {
                    templateUrl: 'views/product/product/product.html?' + (new Date()),
                    controller: 'ProductController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        })
        .state('product.operator', {
            url: '/product/:id',
            views: {
                'content@': {
                    templateUrl: 'views/product/product/product.operator.html?' + (new Date()),
                    controller: 'ProductOperatorController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        });

}]);
