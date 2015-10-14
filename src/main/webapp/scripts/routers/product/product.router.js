angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = new Date().getTime();

    $stateProvider
        .state('product', {
            parent: 'site',
            url: '/products',
            views: {
                'content@': {
                    templateUrl: 'views/product/product/product.html?' + t,
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
                    templateUrl: 'views/product/product/product.operator.html?' + t,
                    controller: 'ProductOperatorController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        });

}]);
