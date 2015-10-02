angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('category', {
                parent: 'site',
                url: '/categories',
                views: {
                    'content@': {
                        templateUrl: 'views/product/category/category.html?' + (new Date()),
                        controller: 'CategoryController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });

    }
]);
