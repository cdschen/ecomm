angular.module('ecommApp')

.directive('ensureUnique', ['$timeout', '$http', function($timeout, $http) {
    return {
        require: 'ngModel',
        link: function(scope, ele, attrs, ctrl) {
            var timeout, url;
            scope.$watch(attrs.ngModel, function(val) {
                if (val) {

                    if (timeout) {
                        $timeout.cancel(timeout);
                    }
                    timeout = $timeout(function() {
                        if (attrs.checkId !== '') {
                            url = attrs.ensureUnique + val + '/' + attrs.checkId;
                        } else {
                            url = attrs.ensureUnique + val;
                        }
                        $http.get(url)
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
