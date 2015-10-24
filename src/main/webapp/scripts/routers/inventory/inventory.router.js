angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('inventory', {
            parent: 'site',
            url: '/inventories',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/inventory/inventory.html?' + t,
                    controller: 'InventoryController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.INVENTORY_ADMIN]
            }
        })
        .state('inventory.enter', {
            url: '/enter/:id',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/inventory/inventory.enter.html?' + t,
                    controller: 'InventoryEnterController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.INVENTORY_ADMIN]
            }
        })
        .state('inventory.out', {
            url: '/out/:id',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/inventory/inventory.out.html?' + t,
                    controller: 'InventoryOutController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.INVENTORY_ADMIN]
            }
        });
}]);
