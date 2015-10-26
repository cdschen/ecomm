angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('inventorySnapshot', {
            parent: 'site',
            url: '/inventories-snapshot',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/inventory-snapshot/inventory-snapshot.html?' + t,
                    controller: 'InventorySnapshotController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.INVENTORY_SNAPSHOT_ADMIN]
            }
        });
}]);
