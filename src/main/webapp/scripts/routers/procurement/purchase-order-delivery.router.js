angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('purchaseOrderDelivery', {
            parent: 'site',
            url: '/purchase-order-deliveries',
            views: {
                'content@': {
                    templateUrl: 'views/procurement/purchase-order-delivery/purchase-order-delivery.html?' + t,
                    controller: 'PurchaseOrderDeliveryController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.RECEIVE_ADMIN]
            }
        })
        .state('purchaseOrderDelivery.purchaseOrderDeliveryGenerate', {
            url: '/purchase-order-delivery-generate',
            views: {
                'content@': {
                    templateUrl: 'views/procurement/purchase-order-delivery/purchase-order-delivery-generate.html?' + t,
                    controller: 'PurchaseOrderDeliveryGenerateController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.RECEIVE_ADMIN]
            }
        });
}]);
