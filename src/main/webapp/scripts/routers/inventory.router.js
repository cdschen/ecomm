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
                url: '/warehouses/:id',
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
            })
            
            .state('outinventorysheet', {
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
            .state('outinventorysheet.orderoutinventory', {
                parent: 'site',
                url: '/out-inventory-sheets/order-out-inventory',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/out-inventory-sheet/out-inventory-sheet.order-out-inventory.html?' + (new Date()),
                        controller: 'OrderOutInventoryController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('outinventorysheet.operator', {
                parent: 'site',
                url: '/out-inventory-sheets/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/out-inventory-sheet/out-inventory-sheet.operator.html?' + (new Date()),
                        controller: 'OutInventorySheetController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('shipment', {
                parent: 'site',
                url: '/shipment',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/shipment/shipment.html?' + (new Date()),
                        controller: 'ShipmentController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });

    }
]);
