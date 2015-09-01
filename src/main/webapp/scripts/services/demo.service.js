'use strict';

angular.module('ecommApp')

.factory('', ['$resource', '$http', function($resource, $http) {

    var demo = $resource('/api/demos/:id');

    demo.getAll = function(demo) {
        return $http.get('/api/demos/get/all', {
            params: demo
        }).then(function(res) {
            return res.data;
        });
    };

    return demo;
}]);
