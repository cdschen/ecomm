angular.module('ecommApp')

.factory('Brand', ['$resource', '$http', function($resource, $http) {

	var brand = $resource('/api/brands/:id', {}, {});

	brand.getAll = function(){
		return $http.get('/api/brands/get/all').then(function(res) {
            return res.data;
        });
	};

	return brand;
}]);
