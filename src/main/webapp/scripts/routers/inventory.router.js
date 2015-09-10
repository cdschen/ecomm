angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('warehouse', {
                parent: 'site',
                url: '/warehouses',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/warehouse/warehouse.html?' + (new Date()),
                        controller: 'WarehouseController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('warehouse.operator', {
                url: '/warehouse/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/warehouse/warehouse.operator.html?' + (new Date()),
                        controller: 'WarehouseOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory', {
                parent: 'site',
                url: '/inventories',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory.html?' + (new Date()),
                        controller: 'InventoryController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory.enter', {
                parent: 'site',
                url: '/inventories/enter/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory.enter.html?' + (new Date()),
                        controller: 'InventoryEnterController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory.out', {
                parent: 'site',
                url: '/inventories/out/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory.out.html?' + (new Date()),
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
