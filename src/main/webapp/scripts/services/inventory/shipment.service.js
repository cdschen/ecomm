
angular.module('ecommApp')

.factory('shipmentService', ['$resource', '$http', function($resource, $http) {

    var shipment = $resource('/api/shipments/:id', {}, {});

        shipment.getAll = function(params) {
        return $http.get('/api/shipments/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    return shipment;
}]);