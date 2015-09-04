angular.module('ecommApp')

.factory('Account', function Account($resource) {
    return $resource('api/account', {}, {});
});
