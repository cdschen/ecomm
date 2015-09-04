angular.module('ecommApp')

.factory('Language', ['$resource', '$http', function($resource, $http) {
	
	var language = $resource('/api/languages/:id');

	language.getAll = function() {
        return $http.get('/api/languages/get/all').then(function(res) {
            return res.data;
        });
    };

    return language;
}]);
