'use strict';

angular.module('ecommApp')

.controller('UserController', ['$rootScope', '$scope', 'User',
    function($rootScope, $scope, User) {

        $scope.totalPagesList = [];

        User.get({
            page: 0,
            size: 20
        }, function(page) {
            console.log(page);
            $scope.page = page;
            rolesToString($scope.page.content);
            if (page.totalPages > 0) {
                for (var i = 0, len = page.totalPages; i < len; i++) {
                    $scope.totalPagesList.push(i);
                }
            }
        });

        function rolesToString(users) {
            if (!angular.isArray(users)) {
                users = [users];
            }
            angular.forEach(users, function(user) {
                user.roleString = '';
                var roles = user.roles;
                for (var i = 0, len = roles.length - 1; i < len; i++) {
                    user.roleString += roles[i].name + ', ';
                }
                user.roleString += roles[roles.length - 1].name;
            });
        }

        $scope.turnPage = function(number) {
            console.log(number);
            if (number > -1 && number < $scope.page.totalPages) {
                User.get({
                    page: number,
                    size: 5
                }, function(page) {
                    $scope.page = page;
                    rolesToString($scope.page.content);
                });
            }
        };
    }
])

.controller('UserOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', '$q', 'User', 'Role',
    function($rootScope, $scope, $state, $stateParams, $q, User, Role) {

        $scope.user = {};
        $scope.roles = {};
        $scope.action = 'create';
        var oldpassword = '';

        Role.query().$promise.then(function(roles) {
            $scope.roles = roles;
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                User.get({
                    id: $stateParams.id
                }, {}, function(user) {
                    oldpassword = user.password;
                    user.password = '********';
                    $scope.user = user;
                    angular.forEach($scope.roles, function(role) {
                        angular.forEach($scope.user.roles, function(userRole) {
                            if (role.id === userRole.id) {
                                role.done = true;
                                return;
                            }
                        });
                    });
                }, function(err) {
                    console.log(err);
                });
            }
        });

        $scope.save = function(user) {
            $scope.user.roles = [];
            angular.forEach($scope.roles, function(role) {
                if (role.done) {
                    $scope.user.roles.push(role);
                }
            });
            if ($scope.user.roles.length === 0) {
                $scope.rolesDoneWarning = true;
                return false;
            } else {
                $scope.rolesDoneWarning = false;
            }

            if ($scope.action === 'update' && $scope.user.password === '********') {
                $scope.user.password = oldpassword;
            }

            User.save({}, user, function() {
                $state.go('user');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.remove = function() {
            User.remove({
                id: $stateParams.id
            }, {}, function() {
                $state.go('user');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.clean = function() {
            if ($scope.action === 'update') {
                $scope.user.password = '';
            }
        };
    }
]);
