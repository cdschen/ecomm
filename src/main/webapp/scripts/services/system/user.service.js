angular.module('ecommApp')

.factory('User', ['$resource', '$http', function($resource, $http) {

    var user = $resource('/api/users/:id', {}, {});

    user.getAll = function(params) {
        return $http.get('/api/users/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    user.updatePassword = function(data) {
        return $http.post('/api/users/update/password', data);
    };

    return user;

}]);
