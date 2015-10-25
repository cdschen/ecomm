angular.module('ecommApp')

.factory('Shop', ['$resource', '$http', function($resource, $http) {

    var shop = $resource('/api/shops/:id');

    shop.getAll = function(params) {
        return $http.get('/api/shops/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    shop.initShopDefaultTunnel = function(shop){
    	$.each(shop.tunnels, function(){
    		var tunnel = this;
    		if (tunnel.defaultOption) {
    			shop.defaultTunnel = angular.copy(tunnel);
    			$.each(tunnel.warehouses, function(){
    				if (shop.defaultTunnel.defaultWarehouseId === this.id) {
    					shop.defaultTunnel.defaultWarehouse = angular.copy(this);
    					return false;
    				}
    			});
    			return false;
    		}
    	});
    };

    return shop;
}]);
