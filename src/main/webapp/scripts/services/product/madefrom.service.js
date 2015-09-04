angular.module('ecommApp')

.factory('MadeFrom', ['$resource', '$http', function($resource, $http) {

    var madefrom = $resource('/api/madefroms/:id', {}, {});

    madefrom.getAll = function() {
        return $http.get('/api/madefroms/get/all').then(function(res) {
            return res.data;
        });
    };

    return madefrom;
}]);
