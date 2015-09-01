'use strict';

angular.module('ecommApp')

.factory('Role', ['$resource', function($resource) {
	return $resource('/api/roles/:id', {}, {});
}]);
