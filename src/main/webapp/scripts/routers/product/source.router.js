angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();
    
    $stateProvider
        .state('source', {
            parent: 'site',
            url: '/sources',
            views: {
                'content@': {
                    templateUrl: 'views/product/source/source.html?' + t,
                    controller: 'SourceController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SOURCE_ADMIN]
            }
        });
}]);
