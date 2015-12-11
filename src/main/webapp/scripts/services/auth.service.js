angular.module('ecommApp')

.factory('Auth', ['$rootScope', '$http', '$state', 'Account', 'Principal', 'localStorageService',
    function($rootScope, $http, $state, Account, Principal, localStorageService) {
        return {
            login: function(credentials) {
                var data = 'j_username=' + credentials.username + '&j_password=' + credentials.password;
                return $http.post('/api/authenticate', data, {
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    })
                    .success(function() {
                        Principal.identify(true).then(function() {});
                    })
                    .error(function() {});
            },
            logout: function() {
                return $http.post('/api/logout').success(function() {
                    Principal.authenticate(null);
                    localStorageService.clearAll(); // 清楚本地存储
                    //$http.get('/api/csrf'); // 到服务器拉取新的 csrf token
                });
            },
            refreshManaged: function(thing) {
                if (Principal.isInRole('SYSTEM_ADMIN')) {
                    return null;
                } else {
                    if (thing === 'warehouse') {
                        if (Principal.get().managedWarehouses.length > 0) {
                            return [Principal.get().managedWarehouses];
                        } else {
                            return [];
                        }
                    } else if (thing === 'shop') {
                        if (Principal.get().managedShops.length > 0) {
                            return [Principal.get().managedShops];
                        } else {
                            return [];
                        }
                    }
                }
            },
            identify: function(force) {
                return Principal.identify(force)
                    .then(function() {
                        var isAuthenticated = Principal.isAuthenticated();
                        // console.log('[DEBUG][auth.service.js]---[Auth.identify(force).then(), Principal.isAuthenticated()]---[' + isAuthenticated + ']');
                        // console.log('[DEBUG][auth.service.js]---[$rootScope.toState.data.roles]');
                        //console.log(JSON.stringify($rootScope.toState.data.roles));
                        if ($rootScope.toState.data.roles && $rootScope.toState.data.roles.length > 0 && !Principal.isInAnyRole($rootScope.toState.data.roles)) {
                            if (isAuthenticated) {
                                console.log('[DEBUG][auth.service.js]---[Auth.identify(force).then(), state.go(accessdenied)]');
                                $state.go('accessDenied');
                            } else {
                                console.log('[DEBUG][auth.service.js]---[Auth.identify(force).then(), state.go(login)]');
                                $rootScope.returnToState = $rootScope.toState;
                                $rootScope.returnToStateParams = $rootScope.toStateParams;
                                $state.go('login');
                            }
                        }
                        // console.log('[DEBUG]---[go to state]---[' + $rootScope.toState.name + ']');
                    });
            }
        };
    }
]);
