'use strict';

angular.module('ecommApp')

.controller('RoleController', ['$rootScope', '$scope', 'Authority', 'Role',
    function($rootScope, $scope, Authority, Role) {

        $scope.reset = function() {
            $scope.authoritiesDoneWarning = false;
            $scope.role = {};
            if ($scope.userRoleForm) {
                $scope.userRoleForm.$setPristine();
            }
            angular.forEach($scope.authorities, function(authority) {
                authority.done = false;
            });
        };

        $scope.reset();

        $scope.roles = null;

        Role.query(function(roles) {
            console.log(roles);
            $scope.roles = roles;
            authoritiesToString($scope.roles);
        });

        $scope.authorities = [];

        Authority.query(function(authorities) {
            $scope.authorities = authorities;
        });

        $scope.save = function() {
            $scope.role.authorities = [];
            angular.forEach($scope.authorities, function(authority) {
                if (authority.done) {
                    $scope.role.authorities.push(authority);
                }
            });
            if ($scope.role.authorities.length === 0) {
                $scope.authoritiesDoneWarning = true;
                return false;
            } else {
                $scope.authoritiesDoneWarning = false;
            }

            Role.save({}, $scope.role, function(role) {
                authoritiesToString(role);
                refreshRoles(role);
                $scope.reset();
            }, function(err) {
                console.log(err);
            });
        };

        $scope.remove = function(id) {
            Role.remove({
                id: id
            }, {}, function() {
                var old = $scope.roles;
                $scope.roles = [];
                angular.forEach(old, function(role) {
                    if (role.id !== id) {
                        $scope.roles.push(role);
                    }
                });
                $scope.reset();
            }, function(err) {
                console.log(err);
            });
        };

        $scope.select = function(role) {
            $scope.role = role;
            angular.forEach($scope.authorities, function(authority) {
                angular.forEach($scope.role.authorities, function(roleAuhority) {
                    if (authority.id === roleAuhority.id) {
                        authority.done = true;
                        return;
                    }
                });
            });
        };

        function authoritiesToString(roles) {
            if (!angular.isArray(roles)) {
                roles = [roles];
            }
            angular.forEach(roles, function(role) {
                role.authorityString = '';
                var authorities = role.authorities;
                for (var i = 0, len = authorities.length - 1; i < len; i++) {
                    role.authorityString += authorities[i].name + ', ';
                }
                role.authorityString += authorities[authorities.length - 1].name;
            });
        }

        function refreshRoles(role) {
        	var isExist = false;
        	angular.forEach($scope.roles, function(r){
        		if (r.id === role.id) {
        			r.authorityString = role.authorityString;
        			console.log(r);
        			isExist = true;
        			return;
        		}
        	});
        	if (!isExist) {
                $scope.roles.push(role);
            }
        }

    }
]);
