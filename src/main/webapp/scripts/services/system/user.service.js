angular.module('ecommApp')

.factory('User', ['$resource', '$http', function($resource, $http) {

    var user = $resource('/api/users/:id', {}, {});

    user.getAll = function() {
        return $http.get('/api/users/get/all').then(function(res) {
            return res.data;
        });
    };

    return user;

}]);
