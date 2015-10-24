angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();
    
    $stateProvider
        .state('madefrom', {
            parent: 'site',
            url: '/madefroms',
            views: {
                'content@': {
                    templateUrl: 'views/product/madefrom/madefrom.html?' + t,
                    controller: 'MadeFromController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SOURCE_ADMIN]
            }
        });
}]);
