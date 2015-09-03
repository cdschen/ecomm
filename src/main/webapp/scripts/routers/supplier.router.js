app.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider

            //供应商模块路由
            .state('supplier', {
                parent: 'site',
                url: '/suppliers',
                views: {
                    'content@': {
                        templateUrl: 'views/supplier/supplier.html?' + (new Date()),
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
