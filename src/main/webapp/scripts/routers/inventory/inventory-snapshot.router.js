angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('inventorySnapshot', {
                parent: 'site',
                url: '/inventories-snapshot',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory-snapshot/inventory-snapshot.html?' + (new Date()),
                        controller: 'InventorySnapshotController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
