angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('inventory', {
                parent: 'site',
                url: '/inventories',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory/inventory.html?' + (new Date()),
                        controller: 'InventoryController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory.enter', {
                url: '/enter/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory/inventory.enter.html?' + (new Date()),
                        controller: 'InventoryEnterController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory.out', {
                url: '/out/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory/inventory.out.html?' + (new Date()),
                        controller: 'InventoryOutController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
