angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now(); 

    $stateProvider
        .state('outInventorySheet', {
            parent: 'site',
            url: '/out-inventory-sheets',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/out-inventory-sheet/out-inventory-sheet.html?' + t,
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
                    templateUrl: 'views/inventory/out-inventory-sheet/out-inventory-sheet.operator.html?' + t,
                    controller: 'OutInventorySheetOperatorController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        })
        .state('outInventorySheet.orderOutInventory', {
            url: '/order-out-inventory',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/out-inventory-sheet/out-inventory-sheet.order-out-inventory.html?' + t,
                    controller: 'OrderOutInventoryController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        });

}]);
