angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        var t = new Date().getTime();

        $stateProvider
            .state('supplier', {
                parent: 'site',
                url: '/suppliers',
                views: {
                    'content@': {
                        templateUrl: 'views/system/supplier/supplier.html?' + t,
                        controller: 'SupplierController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
