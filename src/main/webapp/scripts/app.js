'use strict';

angular.module('ecommApp', ['toastr', 'ngTinyScrollbar', 'LocalStorageModule', 'ui.router', 'ngResource', 'ngMessages', 'ngCookies', 'ui.select', 'ngSanitize', 'ngAnimate', 'ngDragDrop', 'pageslide-directive', 'ui.bootstrap'])

.constant('ROLES', {
    sysadmin: 'sysadmin'
})

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
}])

.config(function(toastrConfig) {
    angular.extend(toastrConfig, {
        allowHtml: false,
        closeButton: false,
        closeHtml: '<button>&times;</button>',
        extendedTimeOut: 1000,
        iconClasses: {
            error: 'toast-error',
            info: 'toast-info',
            success: 'toast-success',
            warning: 'toast-warning'
        },
        messageClass: 'toast-message',
        onHidden: null,
        onShown: null,
        onTap: null,
        progressBar: false,
        tapToDismiss: true,
        templates: {
            toast: 'directives/toast/toast.html',
            progressbar: 'directives/progressbar/progressbar.html'
        },
        timeOut: 1000,
        titleClass: 'toast-title',
        toastClass: 'toast',
        autoDismiss: false,
        containerId: 'toast-container',
        maxOpened: 0,
        newestOnTop: true,
        positionClass: 'toast-bottom-right',
        preventDuplicates: false,
        preventOpenDuplicates: false,
        target: 'body'
    });
});