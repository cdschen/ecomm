angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('madefrom', {
                parent: 'site',
                url: '/madefroms',
                views: {
                    'content@': {
                        templateUrl: 'views/product/madefrom/madefrom.html?' + (new Date()),
                        controller: 'MadeFromController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
