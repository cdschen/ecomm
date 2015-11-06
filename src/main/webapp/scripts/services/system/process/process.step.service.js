angular.module('ecommApp')

.factory('ProcessStep', ['$resource', '$http', function($resource, $http) {

    var step = $resource('/api/process-steps/:id');

    step.getAll = function() {
        return $http.get('/api/process-steps/get/all').then(function(res) {
            return res.data;
        });
    };

    step.refresh = function(steps) {
        $.each(steps, function(index) {
            this.sequence = index;
        });
    };

    return step;

}]);
