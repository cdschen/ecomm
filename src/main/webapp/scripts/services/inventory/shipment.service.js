
angular.module('ecommApp')

.factory('courierService', ['$resource', '$http', function($resource, $http) {

    var courier = $resource('/api/couriers/:id', {}, {});

    courier.getAll = function(params) {
        return $http.get('/api/couriers/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    return courier;
}]);