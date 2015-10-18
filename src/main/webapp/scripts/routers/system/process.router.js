angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        var t = new Date().getTime();

        $stateProvider
            .state('process', {
                parent: 'site',
                url: '/processes',
                views: {
                    'content@': {
                        templateUrl: 'views/system/process/process.html?' + t,
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
