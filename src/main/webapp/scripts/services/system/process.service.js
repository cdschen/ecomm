angular.module('ecommApp')

.factory('Process', ['$resource', '$http', function($resource, $http) {

    var $ = angular.element;
    var process = $resource('/api/processes/:id');

    process.getAll = function(params) {
        return $http.get('/api/processes/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    process.initStatus = function(processes) {
        $.each(processes, function() {
            var process = this;
            $.each(process.steps, function() {
                this.processName = process.name;
            });
        });
    };

    process.refreshStatus = function(statuses){
        var selectedStatusIds = [];
        $.each(statuses, function(){
            selectedStatusIds.push(this.id);
        });
        return selectedStatusIds;
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

    objectProcess.getAll = function(params) {
        return $http.get('/api/objectprocesses/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    objectProcess.getCount = function(params) {
        return $http.get('/api/objectprocesses/get/count', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    return objectProcess;
}]);
