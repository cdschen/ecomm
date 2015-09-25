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
            });
    }
]);
