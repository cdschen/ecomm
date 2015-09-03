'use strict';

var ecommApp = angular.module('ecommApp');

ecommApp.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('supplier', {
                parent: 'site',
                url: '/suppliers',
                views: {
                    'content@': {
                        templateUrl: 'views/supplier/supplier.html?' + (new Date()),
                        controller: 'SupplierController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
