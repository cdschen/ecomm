'use strict';

angular.module('ecommApp')

.factory('orderService', ['$resource', '$http', function($resource, $http) {

	var order = $resource('/api/orders/:id', {}, {});

	order.getAll = function(params) {
        return $http.get('/api/orders/get/all', {
        	params: params
        }).then(function(res) {
            return res.data;
        });
    };

	return order;
}]);
