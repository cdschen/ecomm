angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('tag', {
                parent: 'site',
                url: '/tags',
                views: {
                    'content@': {
                        templateUrl: 'views/product/tag/tag.html?' + (new Date()),
                        controller: 'TagController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
