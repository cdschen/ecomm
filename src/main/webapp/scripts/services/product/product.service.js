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

	var multiLanguage = $resource('/api/product-multilanguages/:id', {}, {});

	return multiLanguage;

}])

.factory('ProductMultiCurrency', ['$resource', function($resource) {

	var multiCurrency = $resource('/api/product-multicurrencies/:id', {}, {});

	return multiCurrency;

}])

.factory('ProductMember', ['$resource', function($resource) {

	var member = $resource('/api/product-members/:id', {}, {});

	return member;

}])

.factory('ProductShopTunnel', ['$resource', function($resource) {

	var shopTunnel = $resource('/api/product-shop-tunnels/:id', {}, {});

	return shopTunnel;

}]);


