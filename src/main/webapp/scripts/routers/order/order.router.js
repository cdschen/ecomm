angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('order', {
            parent: 'site',
            url: '/orders',
            views: {
                'content@': {
                    templateUrl: 'views/order/order.html?' + t,
                    controller: 'OrderController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.ORDER_ADMIN]
            }
        })
        .state('order.operator', {
            url: '/order/:id',
            views: {
                'content@': {
                    templateUrl: 'views/order/order.operator.html?' + t,
                    controller: 'OrderOperatorController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.ORDER_ADMIN]
            }
        })
        .state('order.print', {
            parent: 'site',
            url: '/order-print',
            views: {
                'content@': {
                    templateUrl: 'views/order/order-print.html?' + t,
                    controller: 'OrderPrintController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.ORDER_ADMIN]
            }
        });
}]);
