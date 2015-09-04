angular.module('ecommApp')

.factory('Supplier', ['$resource', '$http', function($resource, $http) {

    var supplier = $resource('/api/suppliers/:id');

    supplier.getAll = function(supplier) {
        return $http.get('/api/suppliers/get/all', {
            params: supplier
        }).then(function(res) {
            return res.data;
        });
    };

    return supplier;
}]);
