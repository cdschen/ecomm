'use strict';

angular.module('ecommApp')

.factory('Resource', ['$resource', '$http', function($resource, $http) {
    return {
        getResource: function() {
            return $http.get('/api/resource').then(function(res) {
                return res.data;
            });
        }
    };
}]);
