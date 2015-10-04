angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    $stateProvider
        .state('shop', {
            parent: 'site',
            url: '/shops',
            views: {
                'content@': {
                    templateUrl: 'views/system/shop/shop.html?' + (new Date()),
                    controller: 'ShopController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        })
        .state('shop.operator', {
            url: '/shop/:id',
            views: {
                'content@': {
                    templateUrl: 'views/system/shop/shop.operator.html?' + (new Date()),
                    controller: 'ShopOperatorController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        });
}]);
