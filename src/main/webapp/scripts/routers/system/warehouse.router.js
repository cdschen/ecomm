angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

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
                roles: [ROLES.SYSTEM_ADMIN, ROLES.WAREHOUSE_ADMIN]
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
                roles: [ROLES.SYSTEM_ADMIN, ROLES.WAREHOUSE_ADMIN]
            }
        });
        
}]);
