angular.module('ecommApp')

.factory('Warehouse', ['$resource', '$http', function($resource, $http) {

    var warehouse = $resource('/api/warehouses/:id', {}, {});

    warehouse.getAll = function(params) {
        return $http.get('/api/warehouses/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    return warehouse;
    
}]);
