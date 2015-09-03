'use strict';

// LocalStorageModule 提供 web存储功能
// ui.router 提供 路由服务
// ngResource Angular官方提供， 与服务器交互的模块
var app = angular.module('ecommApp', ['toastr', 'ngTinyScrollbar', 'LocalStorageModule', 'ui.router', 'ngResource', 'ngMessages', 'ngCookies', 'ui.select', 'ngSanitize', 'ngAnimate', 'ngDragDrop', 'pageslide-directive', 'ui.bootstrap'])

.constant('ROLES', {
    sysadmin: 'sysadmin'
})

/*.constant('AUTHORITIES', {
    userManagement: '用户管理',
    productManagement: '商品管理'
})*/

.run(['$rootScope', '$state', '$window', 'Auth', 'Principal', 'Resource',
    function($rootScope, $state, $window, Auth, Principal, Resource) {

        Resource.getResource().then(function(resource) {
            $rootScope.resource = resource;
        });

        $rootScope.$on('$stateChangeStart', function(event, toState, toParams) {
            $rootScope.toState = toState;
            $rootScope.toStateParams = toParams;
            var isResolved = Principal.isResolved();
            //console.log('[DEBUG][app.js]---[run().$stateChangeStart.Principal.isResolved()]---[' + isResolved + ']');
            if (isResolved) {
                Auth.identify();
            }
        });
        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
            //console.log('[DEBUG][app.js]---[run().$stateChangeSuccess]');
            var titleKey = 'EComm';
            $rootScope.previousStateName = fromState.name;
            $rootScope.previousStateParams = fromParams;
            if (toState.data.title) {
                titleKey = toState.data.title;
            }
            $window.document.title = titleKey;
        });
        $rootScope.back = function() {
            if ($rootScope.previousStateName === 'activate' || $state.get($rootScope.previousStateName) === null) {
                $state.go('home');
            } else {
                $state.go($rootScope.previousStateName, $rootScope.previousStateParams);
            }
        };
    }
])

.factory('authExpiredInterceptor', ['$rootScope', '$q', '$injector', function($rootScope, $q, $injector) {
    return {
        responseError: function(response) {
            //console.log('[DEBUG][app.js]---[factory().responseError()]');
            //console.log(response);
            if (response.status === 401 && response.data.path !== undefined && response.data.path.indexOf('/api/csrf') === -1) {
                var Auth = $injector.get('Auth');
                var $state = $injector.get('$state');
                var to = $rootScope.toState;
                var params = $rootScope.toStateParams;
                Auth.logout();
                $rootScope.returnToState = to;
                $rootScope.returnToStateParams = params;
                $state.go('login');
            }
            return $q.reject(response);
        }
    };
}]);