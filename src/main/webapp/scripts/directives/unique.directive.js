angular.module('ecommApp')

.directive('checkUnique', ['$timeout', '$http', function($timeout, $http) {
    return {
        require: 'ngModel',
        link: function(scope, ele, attrs, ctrl) {
            var timeout;
            scope.$watch(attrs.ngModel, function(val) {
                if (val) {
                    if (timeout) {
                        $timeout.cancel(timeout);
                    }
                    timeout = $timeout(function() {
                        var params = '{"checkUnique": true, ' + '"' + attrs.checkProperty + '": "' + val + '"';
                        if (attrs.checkId !== '') {
                            params += ', "id":' + attrs.checkId;
                        }
                        params += '}';
                        $http.get(attrs.checkUnique, {
                                params: angular.fromJson(params)
                            })
                            .success(function(data) {
                                ctrl.$setValidity('unique', !data);
                            }).error(function() {
                                ctrl.$setValidity('unique', false);
                            });
                    }, 350);
                }
            });
        }
    };
}]);
