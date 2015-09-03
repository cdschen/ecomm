app.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider

            //流程模块路由
            .state('process', {
                parent: 'site',
                url: '/processes',
                views: {
                    'content@': {
                        templateUrl: 'views/process/process.html?' + (new Date()),
                        controller: 'ProcessController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
