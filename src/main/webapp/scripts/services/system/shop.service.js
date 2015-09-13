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

    return shop;
}]);
