angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('order', {
                parent: 'site',
                url: '/orders',
                views: {
                    'content@': {
                        templateUrl: 'views/order/order.html?' + (new Date()),
                        controller: 'OrderController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('order.operator', {
                url: '/order/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/order/order.operator.html?' + (new Date()),
                        controller: 'OrderOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
