angular.module('ecommApp')

.directive('hasAnyRole', ['Principal', function(Principal) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var setVisible = function() {
                element.removeClass('hidden');
            };
            var setHidden = function() {
                element.addClass('hidden');
            };
            var defineVisibility = function(reset) {
                var result;
                if (reset) {
                    setVisible();
                }
                result = Principal.isInAnyRole(roles);
                if (result) {
                    setVisible();
                } else {
                    setHidden();
                }
            };
            var roles = attrs.hasAnyRole.replace(/\s+/g, '').split(',');
            if (roles.length > 0) {
                defineVisibility(true);
            }
        }
    };
}])

.directive('hasRole', ['Principal', function(Principal) {
    return {
        restrict: 'A',
        link: function(scope, element, attrs) {
            var setVisible = function() {
                element.removeClass('hidden');
            };
            var setHidden = function() {
                element.addClass('hidden');
            };
            var defineVisibility = function(reset) {
                var result;
                if (reset) {
                    setVisible();
                }
                result = Principal.isInRole(role);
                if (result) {
                    setVisible();
                } else {
                    setHidden();
                }
            };
            var role = attrs.hasRole.replace(/\s+/g, '');
            if (role.length > 0) {
                defineVisibility(true);
            }
        }
    };
}]);
