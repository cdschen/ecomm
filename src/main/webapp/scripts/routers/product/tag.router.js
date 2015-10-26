angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('tag', {
            parent: 'site',
            url: '/tags',
            views: {
                'content@': {
                    templateUrl: 'views/product/tag/tag.html?' + t,
                    controller: 'TagController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.TAG_ADMIN]
            }
        });
}]);
