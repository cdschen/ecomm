angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

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
                roles: [ROLES.SYSTEM_ADMIN, ROLES.PROCESS_ADMIN]
            }
        })
        .state('process.operator', {
            url: '/process/:id',
            views: {
                'content@': {
                    templateUrl: 'views/system/process/process.operator.html?' + t,
                    controller: 'ProcessOperatorController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.PROCESS_ADMIN]
            }
        });
}]);
