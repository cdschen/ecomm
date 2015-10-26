angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('shop', {
            parent: 'site',
            url: '/shops',
            views: {
                'content@': {
                    templateUrl: 'views/system/shop/shop.html?' + t,
                    controller: 'ShopController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SHOP_ADMIN]
            }
        })
        .state('shop.operator', {
            url: '/shop/:id',
            views: {
                'content@': {
                    templateUrl: 'views/system/shop/shop.operator.html?' + t,
                    controller: 'ShopOperatorController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SHOP_ADMIN]
            }
        });
}]);
