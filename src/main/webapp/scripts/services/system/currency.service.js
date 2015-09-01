'use strict';

angular.module('ecommApp')

.factory('Currency', ['$resource', '$http', function($resource, $http) {

	var currency = $resource('/api/currencies/:id');

	currency.getAll = function() {
        return $http.get('/api/currencies/get/all').then(function(res) {
            return res.data;
        });
    };

    return currency;
}]);
