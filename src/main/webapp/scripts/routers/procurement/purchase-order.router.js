angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('purchaseOrder', {
            parent: 'site',
            url: '/purchase-orders',
            views: {
                'content@': {
                    templateUrl: 'views/procurement/purchase-order/purchase-order.html?' + t,
                    controller: 'PurchaseOrderController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.PURCHASE_ADMIN]
            }
        })
        .state('purchaseOrder.operator', {
            url: '/purchase-order/:id',
            params: {
                purchasedProducts: null
            },
            views: {
                'content@': {
                    templateUrl: 'views/procurement/purchase-order/purchase-order.operator.html?' + t,
                    controller: 'PurchaseOrderOperatorController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.PURCHASE_ADMIN]
            }
        });
}]);
