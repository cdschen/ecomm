angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('user', {
            parent: 'site',
            url: '/users',
            views: {
                'content@': {
                    templateUrl: 'views/system/user/user.html?' + t,
                    controller: 'UserController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.USER_ADMIN]
            }
        })
        .state('user.operator', {
            url: '/user/:id',
            views: {
                'content@': {
                    templateUrl: 'views/system/user/user.operator.html?' + t,
                    controller: 'UserOperatorController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.USER_ADMIN]
            }
        });

}]);
