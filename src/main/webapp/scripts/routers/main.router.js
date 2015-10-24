angular.module('ecommApp')

.config(['$httpProvider', '$stateProvider', '$urlRouterProvider',
    function($httpProvider, $stateProvider, $urlRouterProvider) {

        var t = $.now();

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
                    roles: []
                }
            });
    }
]);
