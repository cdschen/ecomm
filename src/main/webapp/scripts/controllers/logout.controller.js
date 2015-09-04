angular.module('ecommApp')

.controller('LogoutController', function (Auth) {
    Auth.logout();
});
