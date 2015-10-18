angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        var t = new Date().getTime();

        $stateProvider
            .state('warehouse', {
                parent: 'site',
                url: '/warehouses',
                views: {
                    'content@': {
                        templateUrl: 'views/system/warehouse/warehouse.html?' + t,
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
                        templateUrl: 'views/system/warehouse/warehouse.operator.html?' + t,
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
