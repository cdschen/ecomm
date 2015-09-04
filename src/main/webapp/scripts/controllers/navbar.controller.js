angular.module('ecommApp')

.controller('NavbarController', ['$rootScope', '$scope', '$state', 'Auth', 'Principal',
    function($rootScope, $scope, $state, Auth, Principal) {
        $rootScope.user = Principal.get;
        //console.log('[DEBUG][navbar.controller.js]---[NavbarController, Principal.get()]---[' + $scope.user + ']');
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;

        $scope.logout = function() {
            Auth.logout().then(function() {
                $state.go('login');
            });
        };
    }
]);
