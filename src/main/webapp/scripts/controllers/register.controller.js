'use strict';

angular.module('ecommApp')

.controller('RegisterController', function($scope, Auth) {

	$scope.user = {
		username: 'cook1fan',
		password: 'kuangde43',
		email: '32582048@qq.com'
	};

    $scope.register = function() {
    	console.log($scope.user);
        Auth.createAccount($scope.user).then(function() {
            $scope.success = 'OK';
        }).catch(function(response) {  
            console.log(response);
        });
    };
});
