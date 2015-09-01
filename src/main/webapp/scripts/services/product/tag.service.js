'use strict';

angular.module('ecommApp')

.factory('Tag', ['$resource', '$http', function($resource, $http) {

	var tag = $resource('/api/tags/:id');

	tag.getAll = function() {
        return $http.get('/api/tags/get/all').then(function(res) {
            return res.data;
        });
    };

    return tag;
}]);
