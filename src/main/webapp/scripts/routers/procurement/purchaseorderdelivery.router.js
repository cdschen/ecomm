angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('purchaseorderdelivery', {
                parent: 'site',
                url: '/purchaseorderdeliveries',
                views: {
                    'content@': {
                        templateUrl: 'views/procurement/purchaseorderdelivery/purchaseorderdelivery.html?' + (new Date()),
                        controller: 'PurchaseOrderDeliveryController'
                    }
                },
                data: {
                    roles: [ROLES.SYSTEM_ADMIN, ROLES.PURCHASE_ADMIN],
                    authorities: []
                }
            })
            .state('purchaseorderdelivery.purchaseorderdeliveryGenerate', {
                url: '/purchaseorderdelivery-generate',
                views: {
                    'content@': {
                        templateUrl: 'views/procurement/purchaseorderdelivery/purchaseorderdelivery-generate.html?' + (new Date()),
                        controller: 'PurchaseOrderDeliveryGenerateController'
                    }
                },
                data: {
                    roles: [ROLES.SYSTEM_ADMIN, ROLES.PURCHASE_ADMIN],
                    authorities: []
                }
            });
    }
]);
