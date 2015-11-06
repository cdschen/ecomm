angular.module('ecommApp')

.factory('ObjectProcess', ['$resource', '$http', function($resource, $http) {

    var objectProcess = $resource('/api/object-processes/:id');

    objectProcess.getAll = function(params) {
        return $http.get('/api/object-processes/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    objectProcess.getCount = function(params) {
        return $http.get('/api/object-processes/get/count', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    return objectProcess;
    
}]);
