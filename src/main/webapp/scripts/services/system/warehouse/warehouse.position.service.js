angular.module('ecommApp')

.factory('WarehousePosition', ['$resource', '$http', function($resource, $http) {

    var position = $resource('/api/warehouse-positions/:id', {}, {});

    position.getAll = function(params) {
        return $http.get('/api/warehouse-positions/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    position.savePositions = function(positions) {
        return $http.post('/api/warehouse-positions/save/list', positions).then(function(res) {
            return res.data;
        });
    };

    return position;
    
}]);
