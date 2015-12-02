angular.module('ecommApp')

.factory('Principal', ['$q', 'Account', function($q, Account) {
    var _identity,
        _authenticated = false,
        //_authorities = [],
        _roles = [];

    return {
        get: function() {
            return _identity;
        },
        isResolved: function() {
            return angular.isDefined(_identity);
        },
        isAuthenticated: function() {
            return _authenticated;
        },
        isInRole: function(role) {
            // console.log('[DEBUG][principal.service.js]---[Principal.isInRole(role)]---[' + role + ']');
            if (!_authenticated || !_identity || !_identity.roles) {
                return false;
            }
            // console.log('[DEBUG][principal.service.js]---[Principal.isInRole(role), _roles]---[' + JSON.stringify(_roles) + ']');
            // console.log('[DEBUG][principal.service.js]---[Principal.isInRole(role), _roles.indexOf(role)]---[' + (_roles.indexOf(role) !== -1) + ']');
            return _roles.indexOf(role) !== -1;
        },
        isInAnyRole: function(roles) {
            if (!_authenticated || !_identity.roles) {
                return false;
            }
            for (var i = 0; i < roles.length; i++) {
                if (this.isInRole(roles[i])) {
                    return true;
                }
            }
            return false;
        },
        authenticate: function(identity) {
            _identity = identity;
            _authenticated = identity !== null;
        },
        identify: function(force) {
            // console.log('[DEBUG][principal.service.js]---[Principal.identify(force)]---[' + force + ']');
            var deferred = $q.defer();
            if (force === true) {
                _identity = undefined;
            }
            var isDefined = angular.isDefined(_identity);
            // console.log('[DEBUG][principal.service.js]---[Principal.identify(force){angular.isDefined(_identity)}]---[' + isDefined + ']');
            if (isDefined) {
                deferred.resolve(_identity);
                return deferred.promise;
            }
            Account.get().$promise
                .then(function(user) {
                    // console.log('[DEBUG][principal.service.js]---[Principal.identify(force){Account.get().$promise.then()}]');
                    //console.log(JSON.stringify(user));
                    _identity = user;
                    _authenticated = true;
                    _roles.length = 0;
                    $.each(user.roles, function() {
                        _roles.push(this.code);
                    });
                    deferred.resolve(_identity);
                })
                .catch(function() {
                    // console.log('[DEBUG][principal.service.js]---[Principal.identify(force){Account.get().$promise.catch()}]');
                    _identity = null;
                    _authenticated = false;
                    deferred.resolve(_identity);
                });
            return deferred.promise;
        }
    };

}]);
