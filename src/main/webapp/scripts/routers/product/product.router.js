angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

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
                roles: [ROLES.SYSTEM_ADMIN, ROLES.PRODUCT_ADMIN]
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
                roles: [ROLES.SYSTEM_ADMIN, ROLES.PRODUCT_ADMIN]
            }
        });

}]);
