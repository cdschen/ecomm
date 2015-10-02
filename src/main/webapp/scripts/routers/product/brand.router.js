angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('brand', {
                parent: 'site',
                url: '/brands',
                views: {
                    'content@': {
                        templateUrl: 'views/product/brand/brand.html?' + (new Date()),
                        controller: 'BrandController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
            
    }
]);
