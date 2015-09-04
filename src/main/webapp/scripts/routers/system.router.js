angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
        //系统模块路由
        .state('user', {
            parent: 'site',
            url: '/users',
            views: {
                'content@': {
                    templateUrl: 'views/system/user.html?' + (new Date()),
                    controller: 'UserController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        })
            .state('user.operator', {
                url: '/user/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/system/user.operator.html?' + (new Date()),
                        controller: 'UserOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('user.role', {
                url: '/roles',
                views: {
                    'content@': {
                        templateUrl: 'views/system/user.role.html?' + (new Date()),
                        controller: 'RoleController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('shop', {
                parent: 'site',
                url: '/shops',
                views: {
                    'content@': {
                        templateUrl: 'views/system/shop/shop.html?' + (new Date()),
                        controller: 'ShopController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('shop.operator', {
                url: '/shop/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/system/shop/shop.operator.html?' + (new Date()),
                        controller: 'ShopOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
