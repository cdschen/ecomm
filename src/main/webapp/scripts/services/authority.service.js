angular.module('ecommApp')

.factory('Authority', ['$resource', function($resource) {
	return $resource('/api/authorities/:id', {}, {});
}]);
