angular.module('ecommApp')

.factory('Shop', ['$resource', '$http', function($resource, $http) {

    var shop = $resource('/api/shops/:id');

        shop.getAll = function() {
            return $http.get('/api/shops/get/all').then(function(res) {
                return res.data;
            });
        };

    return shop;
}]);
