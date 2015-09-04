angular.module('ecommApp')

.controller('AccountController', ['$scope', '$state',
    function($scope, $state) {
        $scope.login = function() {
            $state.go('dashboard');
        };
    }
]);


