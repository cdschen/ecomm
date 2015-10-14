angular.module('ecommApp')

.config(['$httpProvider', '$stateProvider', '$urlRouterProvider', 'ROLES',
    function($httpProvider, $stateProvider, $urlRouterProvider, ROLES) {

        var t = new Date().getTime();

        $httpProvider.interceptors.push('authExpiredInterceptor');

        $urlRouterProvider.otherwise('/login');

        $stateProvider
            .state('site', {
                abstract: true,
                views: {
                    'navbar@': {
                        templateUrl: 'views/navbar.html?' + t,
                        controller: 'NavbarController'
                    }
                },
                resolve: {
                    identify: ['Auth', function(Auth) {
                        //console.log('[DEBUG][app.js]---[config().state(stie).resolve:{Auth.identify()}]');
                        return Auth.identify();
                    }]
                }
            })
            .state('login', {
                parent: 'site',
                url: '/login',
                views: {
                    'content@': {
                        templateUrl: 'views/login.html?' + t,
                        controller: 'LoginController'
                    }
                },
                data: {
                    roles: [],
                    authorities: []
                }
            })
            .state('dashboard', {
                parent: 'site',
                url: '/dashboard',
                views: {
                    'content@': {
                        templateUrl: 'views/dashboard.html?' + t,
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('accessdenied', {
                parent: 'site',
                url: '/accessdenied',
                views: {
                    'content@': {
                        templateUrl: 'views/accessdenied.html?' + t,
                    }
                },
                data: {
                    roles: [],
                    authorities: []
                }
            });
    }
]);
