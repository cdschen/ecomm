angular.module('ecommApp')

.factory('Process', ['$resource', '$http', function($resource, $http) {

    var process = $resource('/api/processes/:id');

    process.getAll = function(process) {
        return $http.get('/api/processes/get/all', {
            params: process
        }).then(function(res) {
            return res.data;
        });
    };

    return process;
}])

.factory('ProcessStep', ['$resource', '$http', function($resource, $http) {

    var $ = angular.element;
    var step = $resource('/api/processsteps/:id');

    step.getAll = function() {
        return $http.get('/api/processsteps/get/all').then(function(res) {
            return res.data;
        });
    };

    step.refresh = function(steps) {
        $.each(steps, function(index) {
            this.sequence = index;
        });
        return steps;
    };

    return step;
}])

.factory('ObjectProcess', ['$resource', '$http', function($resource, $http) {

    var objectProcess = $resource('/api/objectprocesses/:id');

    objectProcess.getAll = function(objectProcess) {
        return $http.get('/api/objectprocesses/get/all', {
            params: objectProcess
        }).then(function(res) {
            return res.data;
        });
    };

    return objectProcess;
}]);
