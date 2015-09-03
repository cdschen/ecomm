'use strict';

var ecommApp = angular.module('ecommApp');

ecommApp.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('process', {
                parent: 'site',
                url: '/processes',
                views: {
                    'content@': {
                        templateUrl: 'views/process/process.html?' + (new Date()),
                        controller: 'ProcessController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
