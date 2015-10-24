angular.module('ecommApp')

.config(['$stateProvider', function($stateProvider) {

    var t = $.now();

    $stateProvider
        .state('dashboard', {
            parent: 'site',
            url: '/dashboard',
            views: {
                'content@': {
                    templateUrl: 'views/dashboard.html?' + t,
                }
            },
            data: {
                roles: []
            }
        })
        .state('accessDenied', {
            parent: 'site',
            url: '/access-denied',
            views: {
                'content@': {
                    templateUrl: 'views/access-denied.html?' + t,
                }
            },
            data: {
                roles: []
            }
        });
}]);
