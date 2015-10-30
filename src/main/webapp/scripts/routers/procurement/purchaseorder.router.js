angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('purchaseorder', {
                parent: 'site',
                url: '/purchaseorders',
                views: {
                    'content@': {
                        templateUrl: 'views/procurement/purchaseorder/purchaseorder.html?' + (new Date()),
                        controller: 'PurchaseOrderController'
                    }
                },
                data: {
                    roles: [ROLES.SYSTEM_ADMIN, ROLES.PURCHASE_ADMIN],
                    authorities: []
                }
            })
            .state('purchaseorder.operator', {
                url: '/purchaseorder/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/procurement/purchaseorder/purchaseorder.operator.html?' + (new Date()),
                        controller: 'PurchaseOrderOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.SYSTEM_ADMIN, ROLES.PURCHASE_ADMIN],
                    authorities: []
                }
            });
    }
]);
