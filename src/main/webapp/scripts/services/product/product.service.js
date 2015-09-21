angular.module('ecommApp')

.factory('Product', ['$resource', '$http', function($resource, $http) {

	var product = $resource('/api/products/:id', {}, {});

	product.getAll = function(params) {
        return $http.get('/api/products/get/all', {
        	params: params
        }).then(function(res) {
            return res.data;
        });
    };

	return product;
}])

.factory('ProductMultiLanguage', ['$resource', function($resource) {

	var multiLanguage = $resource('/api/productmultilanguages/:id', {}, {});

	return multiLanguage;
}])

.factory('ProductMultiCurrency', ['$resource', function($resource) {

	var multiCurrency = $resource('/api/productmulticurrencies/:id', {}, {});

	return multiCurrency;
}])

.factory('ProductMember', ['$resource', function($resource) {

	var member = $resource('/api/productmembers/:id', {}, {});

	return member;
}])

.factory('ProductShopTunnel', ['$resource', function($resource) {

	var shopTunnel = $resource('/api/productshoptunnels/:id', {}, {});

	return shopTunnel;
}]);


