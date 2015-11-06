angular.module('ecommApp')

.factory('Process', ['$resource', '$http', function($resource, $http) {

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
    
}]);
