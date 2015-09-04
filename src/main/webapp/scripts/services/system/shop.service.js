angular.module('ecommApp')

.factory('Shop', ['$resource', function($resource) {
    return $resource('/api/shops/:id', {}, {});
}]);
