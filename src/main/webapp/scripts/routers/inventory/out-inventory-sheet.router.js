angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('outInventorySheet', {
                parent: 'site',
                url: '/out-inventory-sheets',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/out-inventory-sheet/out-inventory-sheet.html?' + (new Date()),
                        controller: 'OutInventorySheetController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('outInventorySheet.operator', {
                url: '/out-inventory-sheet/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/out-inventory-sheet/out-inventory-sheet.operator.html?' + (new Date()),
                        controller: 'OutInventorySheetOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('orderOutInventory', {
                parent: 'site',
                url: '/order-out-inventory',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/out-inventory-sheet/order-out-inventory.html?' + (new Date()),
                        controller: 'OrderOutInventoryController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
            
    }
]);
