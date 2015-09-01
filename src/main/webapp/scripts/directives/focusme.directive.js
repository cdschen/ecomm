'use strict';

angular.module('ecommApp')

.directive('focusMe', function($timeout) {
    return {
        scope: {
            trigger: '@focusMe'
        },
        link: function(scope, element, attrs) {
            scope.$watch('trigger', function(value) {
                if (value === 'true') {
                    setTimeout(function() {
                        element[0].focus();
                    }, 50);
                }
            });
        }
    };
});
