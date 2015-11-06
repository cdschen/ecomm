angular.module('ecommApp')

.factory('Source', ['$resource', '$http', function($resource, $http) {

    var source = $resource('/api/sources/:id', {}, {});

    source.getAll = function() {
        return $http.get('/api/sources/get/all').then(function(res) {
            return res.data;
        });
    };

    return source;

}]);
