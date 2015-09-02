'use strict';

angular.module('ecommApp')

.controller('LoginController', ['$rootScope', '$scope', '$state', 'Auth',
    function($rootScope, $scope, $state, Auth) {

        $scope.error = false;

        $scope.login = function(credentials) {
            Auth.login(credentials).then(function() {
                $scope.error = false;
                $state.go('dashboard');
            }, function() {
            	$scope.error = true;
            });
        };

    }
]);
