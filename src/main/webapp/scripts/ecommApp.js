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
}]);
angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('warehouse', {
                parent: 'site',
                url: '/warehouses',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/warehouse.html?' + (new Date()),
                        controller: 'WarehouseController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('warehouse.operator', {
                url: '/warehouse/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/warehouse.operator.html?' + (new Date()),
                        controller: 'WarehouseOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory', {
                parent: 'site',
                url: '/inventories',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory.html?' + (new Date()),
                        controller: 'InventoryController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory.enter', {
                parent: 'site',
                url: '/inventories/enter/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory.enter.html?' + (new Date()),
                        controller: 'InventoryEnterController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('inventory.out', {
                parent: 'site',
                url: '/inventories/out/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/inventory.out.html?' + (new Date()),
                        controller: 'InventoryOutController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);

angular.module('ecommApp')

.config(['$httpProvider', '$stateProvider', '$urlRouterProvider', 'ROLES',
    function($httpProvider, $stateProvider, $urlRouterProvider, ROLES) {

        $httpProvider.interceptors.push('authExpiredInterceptor');

        $urlRouterProvider.otherwise('/login');

        $stateProvider
            .state('site', {
                abstract: true,
                views: {
                    'navbar@': {
                        templateUrl: 'views/navbar.html?' + (new Date()),
                        controller: 'NavbarController'
                    }
                },
                resolve: {
                    identify: ['Auth', function(Auth) {
                        //console.log('[DEBUG][app.js]---[config().state(stie).resolve:{Auth.identify()}]');
                        return Auth.identify();
                    }]
                }
            })
            .state('login', {
                parent: 'site',
                url: '/login',
                views: {
                    'content@': {
                        templateUrl: 'views/login.html',
                        controller: 'LoginController'
                    }
                },
                data: {
                    roles: [],
                    authorities: []
                }
            })
            .state('dashboard', {
                parent: 'site',
                url: '/dashboard',
                views: {
                    'content@': {
                        templateUrl: 'views/dashboard.html'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('accessdenied', {
                parent: 'site',
                url: '/accessdenied',
                views: {
                    'content@': {
                        templateUrl: 'views/accessdenied.html'
                    }
                },
                data: {
                    roles: [],
                    authorities: []
                }
            });
    }
]);

angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('order', {
                parent: 'site',
                url: '/orders',
                views: {
                    'content@': {
                        templateUrl: 'views/order/order.html?' + (new Date()),
                        controller: 'OrderController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('order.operator', {
                url: '/order/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/order/order.operator.html?' + (new Date()),
                        controller: 'OrderOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);

angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('process', {
                parent: 'site',
                url: '/processes',
                views: {
                    'content@': {
                        templateUrl: 'views/process/process.html?' + (new Date()),
                        controller: 'ProcessController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);

angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('product', {
                parent: 'site',
                url: '/products',
                views: {
                    'content@': {
                        templateUrl: 'views/product/product.html?' + (new Date()),
                        controller: 'ProductController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('product.operator', {
                url: '/product/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/product/product.operator.html?' + (new Date()),
                        controller: 'ProductOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('brand', {
                parent: 'site',
                url: '/brands',
                views: {
                    'content@': {
                        templateUrl: 'views/product/brand/brand.html?' + (new Date()),
                        controller: 'BrandController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('category', {
                parent: 'site',
                url: '/categories',
                views: {
                    'content@': {
                        templateUrl: 'views/product/category.html?' + (new Date()),
                        controller: 'CategoryController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('madefrom', {
                parent: 'site',
                url: '/madefroms',
                views: {
                    'content@': {
                        templateUrl: 'views/product/madefrom/madefrom.html?' + (new Date()),
                        controller: 'MadeFromController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('tag', {
                parent: 'site',
                url: '/tags',
                views: {
                    'content@': {
                        templateUrl: 'views/product/tag/tag.html?' + (new Date()),
                        controller: 'TagController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);

angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('supplier', {
                parent: 'site',
                url: '/suppliers',
                views: {
                    'content@': {
                        templateUrl: 'views/supplier/supplier.html?' + (new Date()),
                        controller: 'SupplierController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);

angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
        //系统模块路由
        .state('user', {
            parent: 'site',
            url: '/users',
            views: {
                'content@': {
                    templateUrl: 'views/system/user.html?' + (new Date()),
                    controller: 'UserController'
                }
            },
            data: {
                roles: [ROLES.sysadmin],
                authorities: []
            }
        })
            .state('user.operator', {
                url: '/user/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/system/user.operator.html?' + (new Date()),
                        controller: 'UserOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('user.role', {
                url: '/roles',
                views: {
                    'content@': {
                        templateUrl: 'views/system/user.role.html?' + (new Date()),
                        controller: 'RoleController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('shop', {
                parent: 'site',
                url: '/shops',
                views: {
                    'content@': {
                        templateUrl: 'views/system/shop/shop.html?' + (new Date()),
                        controller: 'ShopController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('shop.operator', {
                url: '/shop/:id',
                views: {
                    'content@': {
                        templateUrl: 'views/system/shop/shop.operator.html?' + (new Date()),
                        controller: 'ShopOperatorController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);

angular.module('ecommApp')

.factory('Account', function Account($resource) {
    return $resource('api/account', {}, {});
});

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
                    $http.get('/api/csrf'); // 到服务器拉取新的 csrf token
                });
            },
            identify: function(force) {
                return Principal.identify(force)
                    .then(function() {
                        var isAuthenticated = Principal.isAuthenticated();
                        //console.log('[DEBUG][auth.service.js]---[Auth.identify(force).then(), Principal.isAuthenticated()]---[' + isAuthenticated + ']');
                        //console.log('[DEBUG][auth.service.js]---[$rootScope.toState.data.roles]');
                        //console.log(JSON.stringify($rootScope.toState.data.roles));
                        if ($rootScope.toState.data.roles && $rootScope.toState.data.roles.length > 0 && !Principal.isInAnyRole($rootScope.toState.data.roles)) {
                            if (isAuthenticated) {
                                //console.log('[DEBUG][auth.service.js]---[Auth.identify(force).then(), state.go(accessdenied)]');
                                $state.go('accessdenied');
                            } else {
                                //console.log('[DEBUG][auth.service.js]---[Auth.identify(force).then(), state.go(login)]');
                                $rootScope.returnToState = $rootScope.toState;
                                $rootScope.returnToStateParams = $rootScope.toStateParams;
                                $state.go('login');
                            }
                        }
                        //console.log('[DEBUG]---[go to state]---[' + $rootScope.toState.name + ']');
                    });
            }
        };
    }
]);

angular.module('ecommApp')

.factory('Authority', ['$resource', function($resource) {
	return $resource('/api/authorities/:id', {}, {});
}]);

angular.module('ecommApp')

.factory('Inventory', ['$resource', '$http', function($resource, $http) {

    var inventory = $resource('/api/inventories/:id');

    inventory.getAll = function() {
        return $http.get('/api/inventories/get/all').then(function(res) {
            return res.data;
        });
    };

    inventory.getAllByWarehouseId = function(warehouseId) {
        return $http.get('/api/inventories/get/all/' + warehouseId).then(function(res) {
            return res.data;
        });
    };

    inventory.refresh = function(inventories) {
        var products = [];
        angular.forEach(inventories, function(inventory) {
            var exist = false;
            angular.forEach(products, function(product) {
                if (product.sku === inventory.product.sku) {
                    if (inventory.position) {
                        var existPosition = false;
                        angular.forEach(product.positions, function(position) {
                            if (position.id === inventory.position.id) {
                                var existPositionBatch = false;
                                angular.forEach(position.batches, function(batch) {
                                    if (batch.id === inventory.inventoryBatchId) {
                                        batch.total += inventory.quantity;
                                        existPositionBatch = true;
                                        return;
                                    }
                                });
                                if (!existPositionBatch) {
                                    position.batches.push({
                                        id: inventory.inventoryBatchId,
                                        total: inventory.quantity
                                    });
                                }
                                position.total += inventory.quantity;
                                existPosition = true;
                                return;
                            }
                        });
                        if (!existPosition) {
                            inventory.position.total = inventory.quantity;
                            inventory.position.batches = [{
                                id: inventory.inventoryBatchId,
                                total: inventory.quantity
                            }];
                            product.positions.push(inventory.position);
                        }
                        product.existPosition = true;
                    } else {
                        var existBatch = false;
                        angular.forEach(product.batches, function(batch) {
                            if (batch.id === inventory.inventoryBatchId) {
                                batch.total += inventory.quantity;
                                existBatch = true;
                                return;
                            }
                        });
                        if (!existBatch) {
                            product.batches.push({
                                id: inventory.inventoryBatchId,
                                total: inventory.quantity
                            });
                        }
                    }
                    var detail = {
                        position: inventory.position,
                        quantity: inventory.quantity,
                        expireDate: inventory.expireDate,
                        batchId: inventory.inventoryBatchId
                    };
                    product.total += inventory.quantity;
                    product.details.push(detail);
                    exist = true;
                    return;
                }
            });

            if (!exist) {
                inventory.product.positions = [];
                if (inventory.position) {
                    inventory.position.total = inventory.quantity;
                    inventory.position.batches = [{
                        id: inventory.inventoryBatchId,
                        total: inventory.quantity
                    }];
                    inventory.product.positions.push(inventory.position);
                    inventory.product.existPosition = true;
                } else {
                    inventory.product.batches = [{
                        id: inventory.inventoryBatchId,
                        total: inventory.quantity
                    }];
                }
                inventory.product.details = [{
                    position: inventory.position,
                    quantity: inventory.quantity,
                    expireDate: inventory.expireDate,
                    batchId: inventory.inventoryBatchId
                }];
                inventory.product.total = inventory.quantity;
                products.push(inventory.product);
            }
        });
        return products;
    };

    var itemHasBatch = function(parent, item, action) {
        for (var i = 0, len = parent.batches.length; i < len; i++) {
            var batch = parent.batches[i];
            if (batch.id === item.outBatch.id) {
                if (action === 'add') {
                    batch.total -= item.changedQuantity;
                    parent.total -= item.changedQuantity;
                } else if (action === 'remove') {
                    batch.total += item.changedQuantity;
                    parent.total += item.changedQuantity;
                }
                break;
            }
        }
        return parent;
    };

    var itemNoBatch = function(parent, item, action) {
        var batch;
        if (action === 'add') {
            parent.total -= item.changedQuantity;
            var temp = item.changedQuantity;
            for (var i = 0, len = parent.batches.length; i < len; i++) {
                batch = parent.batches[i];
                if (batch.total - temp < 0) {
                	batch[item.$index] = batch.total;
                    temp = temp - batch.total;
                    batch.total = 0;
                } else {
                	batch[item.$index] = temp;
                    batch.total -= temp;
                    break;
                }
            }
        } else if (action === 'remove') {
            parent.total += item.changedQuantity;
            for (var j = 0, jlen = parent.batches.length; j < jlen; j++) {
                batch = parent.batches[j];
                batch.total += batch[item.$index] !== undefined && batch[item.$index];
            }
        }
        return parent;
    };

    var itemHasPosition = function(parent, item, action) {
        for (var i = 0, len = parent.positions.length; i < len; i++) {
            if (parent.positions[i].id === item.position.id) {
                if (item.outBatch) {
                    console.log('item有批次');
                    parent.positions[i] = itemHasBatch(parent.positions[i], item, action);

                } else {
                    console.log('item没批次');
                    parent.positions[i] = itemNoBatch(parent.positions[i], item, action);
                }
                console.log(parent.positions[i]);
                break;
            }
        }
        return parent;
    };

    inventory.refrechProducts = function(products, item, action) {
        for (var i = 0, len = products.length; i < len; i++) {
            if (products[i].sku === item.product.sku) {
                console.log('匹配到商品');
                if (item.position) {
                    console.log('item有库位');
                    products[i] = itemHasPosition(products[i], item, action);
                    console.log(products[i]);
                } else {
                    console.log('item没库位');
                    if (item.outBatch) {
                        console.log('item有批次');
                        products[i] = itemHasBatch(products[i], item, action);
                    } else {
                        console.log('item没批次');
                        products[i] = itemNoBatch(products[i], item, action);
                    }
                }
                break;
            }
        }
        return products;
    };

    return inventory;
}])

.factory('InventoryBatch', ['$resource', '$http', function($resource, $http) {

    var batch = $resource('/api/inventorybatches/:id');

    batch.getAll = function() {
        return $http.get('/api/inventorybatches/get/all').then(function(res) {
            return res.data;
        });
    };

    return batch;
}]);

angular.module('ecommApp')

.factory('Warehouse', ['$resource', '$http', function($resource, $http) {

	var warehouse = $resource('/api/warehouses/:id', {}, {});

	warehouse.getAll = function() {
        return $http.get('/api/warehouses/get/all').then(function(res) {
            return res.data;
        });
    };

    warehouse.savePositions = function(positions){
    	return $http.post('/api/warehousepositions/save/list', positions).then(function(res) {
            return res.data;
        });
    };

    return warehouse;
}]);


'use strict';

angular.module('ecommApp')

.factory('Order', ['$resource', '$http', function($resource, $http) {

	var order = $resource('/api/orders/:id', {}, {});

	order.getAll = function(order) {
        return $http.get('/api/orders/get/all', {
        	params: order
        }).then(function(res) {
            return res.data;
        });
    };

	return order;
}]);

angular.module('ecommApp')

.factory('Principal', ['$q', 'Account', function($q, Account) {
    var _identity,
        _authenticated = false,
        _authorities = [],
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
            //console.log('[DEBUG][principal.service.js]---[Principal.isInRole(role)]---[' + role + ']');
            if (!_authenticated || !_identity || !_identity.roles) {
                return false;
            }
            //console.log('[DEBUG][principal.service.js]---[Principal.isInRole(role), _roles]---[' + JSON.stringify(_roles) + ']');
            //console.log('[DEBUG][principal.service.js]---[Principal.isInRole(role), _roles.indexOf(role)]---[' + (_roles.indexOf(role) !== -1) + ']');
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
            //console.log('[DEBUG][principal.service.js]---[Principal.identify(force)]---[' + force + ']');
            var deferred = $q.defer();
            if (force === true) {
                _identity = undefined;
            }
            var isDefined = angular.isDefined(_identity);
            //console.log('[DEBUG][principal.service.js]---[Principal.identify(force){angular.isDefined(_identity)}]---[' + isDefined + ']');
            if (isDefined) {
                deferred.resolve(_identity);
                return deferred.promise;
            }
            Account.get().$promise
                .then(function(user) {
                    //console.log('[DEBUG][principal.service.js]---[Principal.identify(force){Account.get().$promise.then()}]');
                    //console.log(JSON.stringify(user));
                    _identity = user;
                    _authenticated = true;
                    angular.forEach(user.roles, function(role) {
                        _roles.push(role.name);
                        angular.forEach(role.authorities, function(authority) {
                            _authorities.push(authority.name);
                        });
                    });
                    deferred.resolve(_identity);
                })
                .catch(function() {
                    //console.log('[DEBUG][principal.service.js]---[Principal.identify(force){Account.get().$promise.catch()}]');
                    _identity = null;
                    _authenticated = false;
                    deferred.resolve(_identity);
                });
            return deferred.promise;
        }
    };

}]);

angular.module('ecommApp')

.factory('Process', ['$resource', '$http', function($resource, $http) {

    var process = $resource('/api/processes/:id');

    process.getAll = function(process) {
        return $http.get('/api/processes/get/all', {
            params: process
        }).then(function(res) {
            return res.data;
        });
    };

    return process;
}])

.factory('ProcessStep', ['$resource', '$http', function($resource, $http) {

    var $ = angular.element;
    var step = $resource('/api/processsteps/:id');

    step.getAll = function() {
        return $http.get('/api/processsteps/get/all').then(function(res) {
            return res.data;
        });
    };

    step.refresh = function(steps) {
        $.each(steps, function(index) {
            this.sequence = index;
        });
        return steps;
    };

    return step;
}])

.factory('ObjectProcess', ['$resource', '$http', function($resource, $http) {

    var objectProcess = $resource('/api/objectprocesses/:id');

    objectProcess.getAll = function(objectProcess) {
        return $http.get('/api/objectprocesses/get/all', {
            params: objectProcess
        }).then(function(res) {
            return res.data;
        });
    };

    return objectProcess;
}]);

angular.module('ecommApp')

.factory('Brand', ['$resource', '$http', function($resource, $http) {

	var brand = $resource('/api/brands/:id', {}, {});

	brand.getAll = function(){
		return $http.get('/api/brands/get/all').then(function(res) {
            return res.data;
        });
	};

	return brand;
}]);

angular.module('ecommApp')

.factory('Category', ['$resource', '$http', function($resource, $http) {

    var category = $resource('/api/categories/:id', {}, {});

    category.getAll = function() {
        return $http.get('/api/categories/get/all').then(function(res) {
            return res.data;
        });
    };

    return category;
}]);

angular.module('ecommApp')

.factory('MadeFrom', ['$resource', '$http', function($resource, $http) {

    var madefrom = $resource('/api/madefroms/:id', {}, {});

    madefrom.getAll = function() {
        return $http.get('/api/madefroms/get/all').then(function(res) {
            return res.data;
        });
    };

    return madefrom;
}]);

angular.module('ecommApp')

.factory('Product', ['$resource', '$http', function($resource, $http) {

	var product = $resource('/api/products/:id', {}, {});

	product.getAll = function(product) {
        return $http.get('/api/products/get/all', {
        	params: product
        }).then(function(res) {
            return res.data;
        });
    };

	return product;
}])

.factory('ProductMultiLanguage', ['$resource', function($resource) {

	var multiLanguage = $resource('/api/productmultilanguages/:id', {}, {});

	return multiLanguage;
}])

.factory('ProductMultiCurrency', ['$resource', function($resource) {

	var multiCurrency = $resource('/api/productmulticurrencies/:id', {}, {});

	return multiCurrency;
}])

.factory('ProductMember', ['$resource', function($resource) {

	var member = $resource('/api/productmembers/:id', {}, {});

	return member;
}]);



angular.module('ecommApp')

.factory('Tag', ['$resource', '$http', function($resource, $http) {

    var tag = $resource('/api/tags/:id');

    tag.getAll = function(tag) {
        return $http.get('/api/tags/get/all', {
            params: tag
        }).then(function(res) {
            return res.data;
        });
    };

    return tag;
}]);

angular.module('ecommApp')

.factory('Resource', ['$resource', '$http', function($resource, $http) {
    return {
        getResource: function() {
            return $http.get('/api/resource').then(function(res) {
                return res.data;
            });
        }
    };
}]);

angular.module('ecommApp')

.factory('Supplier', ['$resource', '$http', function($resource, $http) {

    var supplier = $resource('/api/suppliers/:id');

    supplier.getAll = function(supplier) {
        return $http.get('/api/suppliers/get/all', {
            params: supplier
        }).then(function(res) {
            return res.data;
        });
    };

    return supplier;
}]);

angular.module('ecommApp')

.factory('Currency', ['$resource', '$http', function($resource, $http) {

	var currency = $resource('/api/currencies/:id');

	currency.getAll = function() {
        return $http.get('/api/currencies/get/all').then(function(res) {
            return res.data;
        });
    };

    return currency;
}]);

angular.module('ecommApp')

.factory('Language', ['$resource', '$http', function($resource, $http) {
	
	var language = $resource('/api/languages/:id');

	language.getAll = function() {
        return $http.get('/api/languages/get/all').then(function(res) {
            return res.data;
        });
    };

    return language;
}]);

angular.module('ecommApp')

.factory('Role', ['$resource', function($resource) {
	return $resource('/api/roles/:id', {}, {});
}]);

angular.module('ecommApp')

.factory('Shop', ['$resource', function($resource) {
    return $resource('/api/shops/:id', {}, {});
}]);

angular.module('ecommApp')

.factory('User', ['$resource', '$http', function($resource, $http) {

    var user = $resource('/api/users/:id', {}, {});

    user.getAll = function() {
        return $http.get('/api/users/get/all').then(function(res) {
            return res.data;
        });
    };

    return user;

}]);

angular.module('ecommApp')

.factory('Utils', [function() {

    return {
        setTotalPagesList: function(page) {
            var totalPagesList = [];
            if (page.totalPages > 0) {
                for (var i = 0, len = page.totalPages; i < len; i++) {
                    totalPagesList.push(i);
                }
            }
            return totalPagesList;
        },
        convertLocaleDateToServer: function(date) {
            if (date) {
                var utcDate = new Date();
                utcDate.setUTCDate(date.getDate());
                utcDate.setUTCMonth(date.getMonth());
                utcDate.setUTCFullYear(date.getFullYear());
                return utcDate;
            } else {
                return null;
            }
        },
        convertLocaleDateFromServer: function(date) {
            if (date) {
                var dateString = date.split('-');
                return new Date(dateString[0], dateString[1] - 1, dateString[2]);
            }
            return null;
        },
        convertDateTimeFromServer: function(date) {
            if (date) {
                return new Date(date);
            } else {
                return null;
            }
        }
    };

}]);

angular.module('ecommApp')

.controller('AccountController', ['$scope', '$state',
    function($scope, $state) {
        $scope.login = function() {
            $state.go('dashboard');
        };
    }
]);



angular.module('ecommApp')

.controller('InventoryController', ['$rootScope', '$scope', 'Warehouse', 'Inventory',
    function($rootScope, $scope, Warehouse, Inventory) {

        $scope.warehouses = [];
        $scope.warehouse = {};
        $scope.products = [];

        Warehouse.getAll().then(function(warehouses) {
            $scope.warehouses = warehouses;
            $scope.warehouse = angular.copy(warehouses[0]);
        }).then(function() {
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
        });

        $scope.changeWarehouse = function() {
            //console.log($scope.warehouse.id);
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
        };
    }
])

.controller('InventoryEnterController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse', 'Product', 'InventoryBatch',
    function($rootScope, $scope, $state, $stateParams, Warehouse, Product, InventoryBatch) {

        var $ = angular.element;
        $scope.warehouses = [];
        $scope.warehouse = {};
        $scope.products = [];
        $scope.batch = {
            operate: 1,
            user: {
                id: $rootScope.user().id
            },
            operateTime: undefined,
            memo: '',
            items: []
        };
        $scope.item = {
            changedQuantity: 1
        };
        $scope.product = {
            selected: undefined
        };
        $scope.position = {
            selected: undefined
        };

        function setPositionSelected() {
            console.log($scope.warehouse);
            if ($scope.warehouse.enablePosition === true) {
                $scope.position.selected = $scope.warehouse.positions[0];
            } else {
                $scope.position.selected = undefined;
            }
        }

        $scope.changeWarehouse = function() {
            $scope.batch.items = [];
            angular.forEach($scope.warehouses, function(warehouse) {
                if (warehouse.id === $scope.warehouse.id) {
                    $scope.warehouse = angular.copy(warehouse);
                    return;
                }
            });
            setPositionSelected();
            $scope.batch.warehouseId = $scope.warehouse.id;
        };

        var $date = $('#sandbox-container input').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'top left',
            todayHighlight: true,
        }).on('changeDate', function(e) {
            console.log(e.format());
            if (e.date && e.date !== '') {
                $scope.$apply(function() {
                    $scope.item.expireDate = e.format();
                });
            }
        });

        Warehouse.getAll().then(function(warehouses) {
            $scope.warehouses = warehouses;
            if ($stateParams.id && $stateParams.id !== '') {
                angular.forEach(warehouses, function(warehouse) {
                    if (warehouse.id === $stateParams.id) {
                        $scope.warehouse = angular.copy(warehouse);
                        return;
                    }
                });
            } else {
                $scope.warehouse = angular.copy(warehouses[0]);
            }
            setPositionSelected();
            $scope.batch.warehouseId = $scope.warehouse.id;
        }).then(function() {
            Product.getAll().then(function(products) {
                $scope.products = products;
            });
        });

        $scope.add = function() {
            var item = {
                product: $scope.product.selected,
                warehouseId: $scope.warehouse.id,
                position: $scope.position.selected,
                userId: $rootScope.user().id,
                changedQuantity: $scope.item.changedQuantity,
                expireDate: $scope.item.expireDate
            };
            $scope.batch.items.push(item);
            $scope.product.selected = undefined;
            setPositionSelected();
            $scope.item = {
                changedQuantity: 1,
                expireDate: undefined
            };
            $date.datepicker('clearDates');
        };

        $scope.remove = function($index) {
            $scope.batch.items.splice($index, 1);
        };

        $scope.save = function() {
            console.log($scope.batch);
            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log(batch);
                $state.go('inventory');
            });
        };

    }
])

.controller('InventoryOutController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse', 'Product', 'InventoryBatch', 'Utils', 'Inventory',
    function($rootScope, $scope, $state, $stateParams, Warehouse, Product, InventoryBatch, Utils, Inventory) {

        $scope.warehouses = [];
        $scope.warehouse = {};
        $scope.products = [];
        $scope.batch = {
            operate: 2,
            user: {
                id: $rootScope.user().id
            },
            operateTime: undefined,
            memo: '',
            items: []
        };
        $scope.quantity = 0;
        $scope.item = {
            changedQuantity: 1
        };
        $scope.product = {
            selected: undefined
        };
        $scope.position = {
            selected: undefined
        };
        $scope.selectedBatch = {
            selected: undefined
        };
        $scope.batches = [];

        function setQuantity() {
            if ($scope.product.selected) {
                // console.log('有选商品');
                if ($scope.product.selected.positions.length > 0) {
                    console.log('有库位');
                    if ($scope.position.selected) {
                        // console.log('有选库位');
                        $scope.quantity = $scope.position.selected.total;
                        if ($scope.selectedBatch.selected) {
                            $scope.quantity = $scope.selectedBatch.selected.total;
                        } else {
                            //console.log('没选批次');
                        }
                    } else {
                        // console.log('没选库位');
                    }
                } else {
                    //console.log('没库位');
                    $scope.quantity = $scope.product.selected.total;
                    if ($scope.selectedBatch.selected) {
                        $scope.quantity = $scope.selectedBatch.selected.total;
                    } else {
                        //console.log('没选批次');
                    }
                }
            } else {
                //console.log('没选商品');
                $scope.quantity = 0;
            }
        }

        function setPositionSelected() {
            if ($scope.product.selected && $scope.product.selected.positions.length > 0) {
                $scope.position.selected = $scope.product.selected.positions[0];
            } else {
                $scope.position.selected = undefined;
            }
        }

        function setChangedQuantity() {
            $scope.item.changedQuantity = 1;
        }

        function setSelectedBatch() {
            $scope.selectedBatch.selected = undefined;
        }

        function setBatches() {
            if ($scope.product.selected) {
                if ($scope.product.selected.positions.length > 0) {
                    if ($scope.position.selected) {
                        //console.log('装入所选库位的批次列表')
                        $scope.batches = $scope.position.selected.batches;
                    } else {
                        //console.log('装入第一个库位的批次列表')
                        $scope.batches = $scope.product.selected.positions[0].batches;
                    }
                } else {
                    //console.log('装入所有批次列表')
                    $scope.batches = $scope.product.selected.batches;
                }
            } else {
                $scope.batches = [];
            }
        }

        Warehouse.getAll().then(function(warehouses) {
            $scope.warehouses = warehouses;
            if ($stateParams.id && $stateParams.id !== '') {
                angular.forEach(warehouses, function(warehouse) {
                    if (warehouse.id === $stateParams.id) {
                        $scope.warehouse = angular.copy(warehouse);
                        return;
                    }
                });
            } else {
                $scope.warehouse = angular.copy(warehouses[0]);
            }
            setPositionSelected();
            $scope.batch.warehouseId = $scope.warehouse.id;
        }).then(function() {
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
            });
        });

        $scope.changeWarehouse = function() {
            $scope.batch.items = [];
            angular.forEach($scope.warehouses, function(warehouse) {
                if (warehouse.id === $scope.warehouse.id) {
                    $scope.warehouse = angular.copy(warehouse);
                    return;
                }
            });
            $scope.batch.warehouseId = $scope.warehouse.id;
            Inventory.getAllByWarehouseId($scope.warehouse.id).then(function(inventories) {
                $scope.products = Inventory.refresh(inventories);
                $scope.product.selected = undefined;
                setSelectedBatch();
                setPositionSelected();
                setBatches();
                setQuantity();
                setChangedQuantity();
            });
        };

        $scope.changeProduct = function() {
            console.log('选择商品');
            console.log($scope.product.selected);
            setSelectedBatch();
            setPositionSelected();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.changePosition = function() {
            console.log('选择库位');
            console.log($scope.position.selected);
            setSelectedBatch();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.changeBatch = function() {
            console.log('选择批次');
            console.log($scope.selectedBatch.selected);
            setQuantity();
            setChangedQuantity();
        };

        $scope.add = function() {
            var item = {
                product: $scope.product.selected,
                warehouseId: $scope.warehouse.id,
                position: $scope.position.selected,
                userId: $rootScope.user().id,
                changedQuantity: $scope.item.changedQuantity,
                outBatch: $scope.selectedBatch.selected,
                $index: 't' + Date.parse(Date())
            };
            console.log(item);
            $scope.batch.items.push(item);

            $scope.products = Inventory.refrechProducts($scope.products, item, 'add');

            $scope.product.selected = undefined;
            setSelectedBatch();
            setPositionSelected();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.remove = function($index, item) {
            console.log(item);
            $scope.batch.items.splice($index, 1);
            $scope.products = Inventory.refrechProducts($scope.products, item, 'remove');

            $scope.product.selected = undefined;
            setSelectedBatch();
            setPositionSelected();
            setBatches();
            setQuantity();
            setChangedQuantity();
        };

        $scope.save = function() {
            //$scope.batch.operateTime = Utils.convertLocaleDateToServer(new Date());
            console.log($scope.batch);
            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log(batch);
                $state.go('inventory');
            });
        };

    }
]);

angular.module('ecommApp')

.controller('WarehouseController', ['$rootScope', '$scope', 'Warehouse', 'Utils',
    function($rootScope, $scope, Warehouse, Utils) {

        var $ = angular.element;
        $scope.totalPagesList = [];
        $scope.pageSize = 20;

        Warehouse.get({
            page: 0,
            size: $scope.pageSize
        }, function(page) {
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        });

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Warehouse.get({
                    page: number,
                    size: $scope.pageSize
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.positionsModal = function(warehouse) {
            $scope.positions = warehouse.positions;
            $('#positionsModal').modal('show');
        };

        $scope.savePositions = function(positions) {
            console.log(positions);
            Warehouse.savePositions(positions).then(function() {
                $('#positionsModal').modal('hide');
                $scope.positions = [];
            }, function(err) {
                console.log(err);
            });
        };
    }
])

.controller('WarehouseOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse',
    function($rootScope, $scope, $state, $stateParams, Warehouse) {

        $scope.warehouse = {};
        $scope.action = 'create';

        if ($stateParams.id && $stateParams.id !== '') {
            $scope.action = 'update';
            Warehouse.get({
                id: $stateParams.id
            }, {}, function(warehouse) {
                $scope.warehouse = warehouse;
                console.log(warehouse);
            });
        }

        $scope.save = function(warehouse) {
            console.log(warehouse);
            if (warehouse.enablePosition) {
                warehouse.positions = [{
                    name: 'A'
                }, {
                    name: 'B'
                }, {
                    name: 'C'
                }, {
                    name: 'D'
                }, {
                    name: 'E'
                }, {
                    name: 'F'
                }, {
                    name: 'G'
                }, {
                    name: 'H'
                }, {
                    name: 'I'
                }, {
                    name: 'J'
                }, {
                    name: 'K'
                }, {
                    name: 'L'
                }, {
                    name: 'M'
                }, {
                    name: 'N'
                }, {
                    name: 'O'
                }, {
                    name: 'P'
                }, {
                    name: 'Q'
                }, {
                    name: 'R'
                }, {
                    name: 'S'
                }, {
                    name: 'T'
                }, {
                    name: 'U'
                }, {
                    name: 'V'
                }, {
                    name: 'W'
                }, {
                    name: 'X'
                }, {
                    name: 'Y'
                }, {
                    name: 'Z'
                }];
            } else {
                warehouse.positions = [];
            }
            Warehouse.save({}, warehouse, function() {
                $state.go('warehouse');
            }, function(err) {
                console.log(err);
            });

        };

        $scope.remove = function() {
            Warehouse.remove({
                id: $stateParams.id
            }, {}, function() {
                $state.go('warehouse');
            }, function(err) {
                console.log(err);
            });
        };

    }
]);

angular.module('ecommApp')

.controller('LoginController', ['$rootScope', '$scope', '$state', 'Auth',
    function($rootScope, $scope, $state, Auth) {

        $scope.error = false;

        $scope.login = function(credentials) {
            Auth.login(credentials).then(function() {
                $scope.error = false;
                $state.go('dashboard');
            }, function() {
            	$scope.error = true;
            });
        };

    }
]);

angular.module('ecommApp')

.controller('LogoutController', function (Auth) {
    Auth.logout();
});

angular.module('ecommApp')

.controller('NavbarController', ['$rootScope', '$scope', '$state', 'Auth', 'Principal',
    function($rootScope, $scope, $state, Auth, Principal) {
        $rootScope.user = Principal.get;
        //console.log('[DEBUG][navbar.controller.js]---[NavbarController, Principal.get()]---[' + $scope.user + ']');
        $scope.isAuthenticated = Principal.isAuthenticated;
        $scope.$state = $state;

        $scope.logout = function() {
            Auth.logout().then(function() {
                $state.go('login');
            });
        };
    }
]);

'use strict';

angular.module('ecommApp')

.controller('OrderController', ['$scope', 'Order', 'Utils', 'Process',
    function($scope, Order, Utils, Process) {

        //var $ = angular.element;
        $scope.template = {
            details: {
                url: 'views/order/order.details.html?' + (new Date())
            },
            process: {
                url: 'views/order/order.process.html?' + (new Date())
            },
            status: {
                url: 'views/order/order.status.html?' + (new Date())
            }
        };
        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.order = {};
        $scope.shop = {};
        $scope.shops = [
            { label:'杀阡陌', value:1 },
            { label:'淘宝宝', value:2 }
        ];
        $scope.processes = [];
        $scope.status = [];
        $scope.selected = {
            status: []
        };
        $scope.popover = {
            url: 'process-tmpl.html'
        };
        $scope.detailsSlideChecked = false;
        $scope.processSlideChecked = false;
        $scope.statusSlideChecked = false;
        $scope.processOrder = undefined;

        function initStatus(processes) {
            angular.forEach(processes, function(process) {
                angular.forEach(process.steps, function(step) {
                    step.processName = process.name;
                    $scope.status.push(step);
                });
            });
            // console.log('$scope.status:');
            // console.log($scope.status);
        }

        function refreshStatus(status) {
            var selectedStatus = [];
            angular.forEach(status, function(state) {
                selectedStatus.push(state.id);
            });
            return selectedStatus;
        }

        Order.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['id'],
            deleted: false
        }).$promise.then(function(page) {
            console.clear();
            console.log('page:');
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        }).then(function() {
            Process.getAll({
                deleted: false,
                objectType: 2
            }).then(function(processes) {
                console.log('Process.getAll:');
                console.log(processes);
                $scope.processes = processes;
                initStatus(processes);
            });
        });

        $scope.turnPage = function(number) {
            console.clear();
            console.log('turnPage:');
            console.log($scope.order);
            if (number > -1 && number < $scope.page.totalPages) {
                Order.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id'],
                    status: refreshStatus($scope.selected.status)
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.order);
            Order.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id'],
                status: refreshStatus($scope.selected.status)
            }, function(page) {
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.order = {};
            $scope.selected.status.length = 0;
            console.log($scope.order);
            Order.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id']
            }, function(page) {
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        // status

        $scope.closeStatusSlide = function() {
            $scope.statusSlideChecked = false;
        };

        $scope.loadStatus = function() {
            $scope.statusSlideChecked = true;
        };

        $scope.selectState = function(step) {
            if (step.selected && step.selected === true) {
                step.selected = false;
                for (var i = 0, len = $scope.selected.status.length; i < len; i++) {
                    if ($scope.selected.status[i].id === step.id) {
                        $scope.selected.status.splice(i, 1);
                        break;
                    }
                }
            } else {
                step.selected = true;
                $scope.selected.status.push(step);
            }
            console.log('$scope.selectState():');
            console.log($scope.selected.status);
        };

        // process

        $scope.colseProcessSlide = function() {
            $scope.processSlideChecked = false;
            if ($scope.processOrder) {
                $scope.processOrder.active = false;
            }
        };

        $scope.loadProcesses = function(order) {
            console.log(order);
            $scope.processSlideChecked = true;
            $scope.processOrder = order;
            $scope.processOrder.active = true;
        };

        $scope.updateStep = function(order) {
            console.log('updateStep:');
            $scope.processOrder = order;
        };

        $scope.saveUpdateStep = function(process, stepId) {
            console.log('saveUpdateStep:');
            process.step.id = stepId;
            console.log(process);
            ObjectProcess.save({}, process, function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    console.log('refresh Processes:');
                    console.log(objectProcesses);
                    $scope.processOrder.processes = angular.copy(objectProcesses);
                });
            });
        };

        // details

        $scope.colseDetailsSlide = function() {
            $scope.detailsSlideChecked = false;
        };

        $scope.loadDetails = function(order) {
            $scope.detailsSlideChecked = true;
            $scope.processOrder = order;
        };

    }
])

.controller('OrderItemsController', ['$scope', function($scope) {
    var $ = angular.element;

    $scope.initOrderDetailsTabs = function() {
        $('#orderDetailsTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    };

}])

.controller('OrderProcessController', ['$scope', '$filter', 'Process', 'ObjectProcess',
    function($scope, $filter, Process, ObjectProcess) {

        $scope.applyProcess = function(process) {
            
            var objectProcess = {
                objectId: $scope.processOrder.id,
                objectType: 2,
                process: {
                    id: process.id
                }
            };

            process.steps = $filter('orderBy')(process.steps, 'sequence');

            if (process.defaultStepId) {
                objectProcess.step = {
                    id: process.defaultStepId
                };
            } else {
                objectProcess.step = {
                    id: process.steps[0].id
                };
            }

            ObjectProcess.save({}, objectProcess).$promise.then(function(objectProcess) {
                return objectProcess;
            }).then(function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    console.log('refresh Processes:');
                    console.log(objectProcesses);
                    $scope.processOrder.processes = angular.copy(objectProcesses);
                    $scope.colseProcessSlide();
                });
            });

        };

        $scope.removeProcess = function(process) {
            var objectProcesses = $scope.processOrder.processes;
            var objectProcess;
            for (var i = 0, len = objectProcesses.length; i < len; i++) {
                objectProcess = objectProcesses[i];
                if (process.id === objectProcess.process.id) {
                    break;
                }
            }
            if (objectProcess) {
                ObjectProcess.remove({
                    id: objectProcess.id
                }, {}, function() {
                    ObjectProcess.getAll({
                        objectId: objectProcess.objectId
                    }).then(function(objectProcesses) {
                        console.log('refresh Processes:');
                        console.log(objectProcesses);
                        $scope.processOrder.processes = angular.copy(objectProcesses);
                        $scope.colseProcessSlide();
                    });
                });
            }

        };

        $scope.forObjectProcesses = function(step) {
            if ($scope.processOrder) {
                var objectProcesses = $scope.processOrder.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (objectProcess.process.id === step.processId) {
                        if (objectProcess.process.type === 1) {
                            if (step.sequence > objectProcess.step.sequence) {
                                return false;
                            } else if (step.sequence <= objectProcess.step.sequence) {
                                return true;
                            }
                        } else if (objectProcess.process.type === 2) {
                            if (step.id === objectProcess.step.id) {
                                return true;
                            } else if (step.id !== objectProcess.step.id) {
                                return false;
                            }
                        }
                    } else {
                        continue;
                    }
                }
                return false;
            }
        };

        $scope.appliedProcess = function(process) {
            if ($scope.processOrder) {
                var objectProcesses = $scope.processOrder.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (process.id === objectProcess.process.id) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
])

.controller('OrderOperatorController', ['$scope', '$state', '$stateParams', 'Order',
    function($scope, $state, $stateParams, Order) {

        console.clear();
        var $ = angular.element;
        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.template = {
            info: {
                url: 'views/order/order.operator.info.html?' + new Date(),
                items: {
                    url: 'views/order/order.operator.info.items.html?' + new Date()
                }
            }
        };

        $scope.action = 'create';

            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                Order.get({
                        id: $stateParams.id
                    }, {}).$promise
                    .then(function(order) {
                        console.log('[' + $scope.action + '] loading order');
                        console.log(order);
                        $scope.order = order;
                        return order;
                    });
            }

        $('#orderTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    }
])

.controller('OrderInformationController', ['$scope', '$state', 'Order',
    function($scope, $state, Order) {

        $scope.save = function(order) {
            //console.clear();
            console.log('[' + $scope.action + '] save order');
            console.log(order);
            Order.save({
                action: $scope.action
            }, order, function(order) {
                console.log('[' + $scope.action + '] save order complete:');
                console.log(order);
                $state.go('order');
            });
        };
    }
]);

angular.module('ecommApp')

.controller('ProcessController', ['$scope', '$timeout', 'Process', 'ProcessStep',
    function($scope, $timeout, Process, ProcessStep) {
        var $ = angular.element;
        var defaultProcess = {
            type: {
                label: '线性流程',
                value: 1
            },
            defaultStep: undefined,
            hideWhenComplete: {
                label: '否',
                value: false
            },
            steps: [],
            deleted: false,
            editable: true
        };
        var defaultStep = {
            id: null,
            processId: null,
            name: '',
            sequence: 0,
            type: 1,
            $index: 't' + Date.parse(Date()),
            editable: true
        };
        defaultProcess.steps.push(angular.copy(defaultStep));
        $scope.processes = [];
        $scope.types = [{
            label: '线性流程',
            value: 1
        }, {
            label: '开关流程',
            value: 2
        }];
        $scope.objectTypes = [{
            label: '订单',
            value: 1
        }, {
            label: '商品',
            value: 2
        }, {
            label: '库存',
            value: 3
        }, {
            label: '采购',
            value: 4
        }];
        $scope.isorno = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];

        function initField(process) {
            process.type = $scope.types[process.type - 1];
            process.objectType = $scope.objectTypes[process.objectType - 1];
            if (process.defaultStepId !== null) {
                for (var i = 0, len = process.steps.length; i < len; i++) {
                    var step = process.steps[i];
                    if (process.defaultStepId === step.id) {
                        process.defaultStep = angular.copy(step);
                        break;
                    }
                }
            }
            process.hideWhenComplete = $scope.isorno[process.hideWhenComplete ? 0 : 1];
        }

        function refreshField(process) {
            process.type = process.type.value;
            process.objectType = process.objectType.value;
            process.defaultStepId = process.defaultStep && process.defaultStep.id;
            process.defaultStepName = process.defaultStep && process.defaultStep.name;
            process.hideWhenComplete = process.hideWhenComplete.value;
        }

        Process.getAll({
            deleted: false
        }).then(function(processes) {
            console.log('Process.getAll():');
            console.log(processes);
            $scope.processes = processes;
            angular.forEach($scope.processes, function(process) {
                initField(process);
            });
        });

        // process

        $scope.addProcess = function() {
            $scope.processes.unshift(angular.copy(defaultProcess));
        };

        $scope.updateProcess = function(process) {
            process.editable = true;
        };

        $scope.saveProcess = function(process, processForm, $index) {

            console.log('saveProcess:');
            console.log(process);

            refreshField(process);

            Process.save({}, process).$promise.then(function(process) {
                console.log('saveProcess complete:');
                angular.forEach(process.steps, function(step) {
                    step.processId = process.id;
                });
                console.log(process);
                processForm.$setPristine();

                initField(process);

                $scope.processes[$index] = angular.copy(process);

                console.log('$scope.processes:');
                console.log($scope.processes);

            });
        };

        var removingProcess;

        $scope.showRemoveProcess = function(process, $index) {
            removingProcess = process;
            removingProcess.$index = $index;
            $('#processDeleteModal').modal('show');
        };

        $scope.removeProcess = function() {
            if (removingProcess.id) {

                refreshField(removingProcess);
                removingProcess.deleted = true;

                Process.save({}, removingProcess).$promise.then(function() {
                    $scope.processes.splice(removingProcess.$index, 1);
                    $('#processDeleteModal').modal('hide');
                    removingProcess = undefined;
                });
            } else {
                $scope.processes.splice(removingProcess.$index, 1);
                $('#processDeleteModal').modal('hide');
                removingProcess = undefined;
            }
        };

        // step

        $scope.addStep = function(process, step, $index) {
            defaultStep.$index = 't' + $index + Date.parse(Date());
            process.steps.push(angular.copy(defaultStep));
            process.steps = ProcessStep.refresh(process.steps);
        };

        $scope.removeStep = function(process, step, $index) {
            var len = process.steps.length;
            if (len === 1) {
                return;
            }
            process.defaultStep = undefined;
            process.steps.splice($index, 1);
            process.steps = ProcessStep.refresh(process.steps);

        };

        $scope.drop = function(e, ui, process) {
            process.steps = ProcessStep.refresh(process.steps);
            console.log('drop:');
            console.log(process.steps);
        };

    }
]);

angular.module('ecommApp')

.controller('BrandController', ['$rootScope', '$scope', 'Brand', 'Utils',
    function($rootScope, $scope, Brand, Utils) {

        var $ = angular.element;

        $scope.template = {
            operator: {
                url: 'views/product/brand/brand.operator.html?' + (new Date())
            }
        };

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.brandSlideChecked = false;
        $scope.title = '';

        $scope.refresh = function() {
            Brand.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id,desc']
            }, function(page) {
                console.clear();
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.closeBrandSlide();
            });
        };

        $scope.refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Brand.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id,desc']
                }, function(page) {
                    console.clear();
                    console.log('turnPage:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.updateBrand = function(brand) {
            console.clear();
            console.log('updateBrand:');
            console.log(brand);
            $scope.brand = brand;
            $scope.operateBrand();
        };

        $scope.removingBrand = undefined;

        $scope.showRemoveBrand = function(brand, $index) {
            console.clear();
            console.log('showRemoveBrand $index: ' + $index);
            console.log(brand);

            $scope.removingBrand = brand;
            $('#brandDeleteModal').modal('show');
        };

        $scope.removeBrand = function() {
            console.clear();
            console.log('removeBrand:');
            console.log($scope.removingBrand);

            if (angular.isDefined($scope.removingBrand)) {
                Brand.remove({
                    id: $scope.removingBrand.id
                }, {}, function() {
                    $scope.removingBrand = undefined;
                    $('#brandDeleteModal').modal('hide');
                    $scope.refresh();
                });
            }
        };

        $scope.saveBrand = function(brandForm, brand) {
            console.clear();
            console.log('saveBrand:');
            console.log(brand);

            Brand.save({}, brand, function(brand) {
                console.log('saveBrand complete:');
                console.log(brand);
                brandForm.$setPristine();
                $scope.refresh();
            });
        };

        // operator

        $scope.closeBrandSlide = function() {
            $scope.brandSlideChecked = false;
        };

        $scope.operateBrand = function(action) {
            $scope.title = '编辑';
            if (action === 'create') {
                $scope.title = '创建';
                $scope.brand = {};
            }
            $scope.brandSlideChecked = true;
        };
    }
]);

angular.module('ecommApp');

// .controller('CategoryController', ['$scope', 'Category', 'Utils',
//     function($scope, Category, Utils) {

        
//     }
// ]);

angular.module('ecommApp')

.controller('MadeFromController', ['$rootScope', '$scope', 'MadeFrom', 'Utils',
    function($rootScope, $scope, MadeFrom, Utils) {

        var $ = angular.element;

        $scope.template = {
            operator: {
                url: 'views/product/madefrom/madefrom.operator.html?' + (new Date())
            }
        };

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.madeFromSlideChecked = false;
        $scope.title = '';

        $scope.refresh = function() {
            MadeFrom.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id,desc']
            }, function(page) {
                console.clear();
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.closeMadeFromSlide();
            });
        };

        $scope.refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                MadeFrom.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id,desc']
                }, function(page) {
                    console.clear();
                    console.log('turnPage:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.updateMadeFrom = function(madeFrom) {
            console.clear();
            console.log('updateMadeFrom:');
            console.log(madeFrom);
            $scope.madeFrom = madeFrom;
            $scope.operateMadeFrom();
        };

        $scope.removingMadeFrom = undefined;

        $scope.showRemoveMadeFrom = function(madeFrom, $index) {
            console.clear();
            console.log('showRemoveMadeFrom $index: ' + $index);
            console.log(madeFrom);

            $scope.removingMadeFrom = madeFrom;
            $('#madeFromDeleteModal').modal('show');
        };

        $scope.removeMadeFrom = function() {
            console.clear();
            console.log('removeMadeFrom:');
            console.log($scope.removingMadeFrom);

            if (angular.isDefined($scope.removingMadeFrom)) {
                MadeFrom.remove({
                    id: $scope.removingMadeFrom.id
                }, {}, function() {
                    $scope.removingMadeFrom = undefined;
                    $('#madeFromDeleteModal').modal('hide');
                    $scope.refresh();
                });
            }
        };

        $scope.saveMadeFrom = function(madeFromForm, madeFrom) {
            console.clear();
            console.log('saveMadeFrom:');
            console.log(madeFrom);

            MadeFrom.save({}, madeFrom, function(madeFrom) {
                console.log('saveMadeFrom complete:');
                console.log(madeFrom);
                madeFromForm.$setPristine();
                $scope.refresh();
            });
        };

        // operator

        $scope.closeMadeFromSlide = function() {
            $scope.madeFromSlideChecked = false;
        };

        $scope.operateMadeFrom = function(action) {
            $scope.title = '编辑';
            if (action === 'create') {
                $scope.title = '创建';
                $scope.madeFrom = {};
            }
            $scope.madeFromSlideChecked = true;
        };
    }
]);

angular.module('ecommApp')

.controller('ProductController', ['$scope', 'Product', 'Utils', 'Process', 'ObjectProcess',
    function($scope, Product, Utils, Process, ObjectProcess) {

        //var $ = angular.element;
        $scope.template = {
            details: {
                url: 'views/product/product.details.html?' + (new Date())
            },
            process: {
                url: 'views/product/product.process.html?' + (new Date())
            },
            status: {
                url: 'views/product/product.status.html?' + (new Date())
            }
        };
        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.product = {
            sku: '', 
            name: ''
        };
        $scope.processes = [];
        $scope.status = [];
        $scope.selected = {
            status: []
        };
        $scope.popover = {
            url: 'process-tmpl.html'
        };
        $scope.detailsSlideChecked = false;
        $scope.processSlideChecked = false;
        $scope.statusSlideChecked = false;
        $scope.processProduct = undefined;

        function initStatus(processes) {
            angular.forEach(processes, function(process) {
                angular.forEach(process.steps, function(step) {
                    step.processName = process.name;
                    $scope.status.push(step);
                });
            });
            // console.log('$scope.status:');
            // console.log($scope.status);
        }

        function refreshStatus(status) {
            var selectedStatus = [];
            angular.forEach(status, function(state) {
                selectedStatus.push(state.id);
            });
            return selectedStatus;
        }

        Product.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['name'],
            deleted: false
        }).$promise.then(function(page) {
            console.clear();
            console.log('page:');
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        }).then(function() {
            Process.getAll({
                deleted: false,
                objectType: 2
            }).then(function(processes) {
                console.log('Process.getAll:');
                console.log(processes);
                $scope.processes = processes;
                initStatus(processes);
            });
        });

        $scope.turnPage = function(number) {
            console.clear();
            console.log('turnPage:');
            console.log($scope.product);
            if (number > -1 && number < $scope.page.totalPages) {
                Product.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['name'],
                    sku: $scope.product.sku,
                    name: $scope.product.name,
                    deleted: false,
                    status: refreshStatus($scope.selected.status)
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.product);
            Product.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['name'],
                sku: $scope.product.sku,
                name: $scope.product.name,
                status: refreshStatus($scope.selected.status)
            }, function(page) {
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.product = {
                sku: '',
                name: ''
            };
            $scope.selected.status.length = 0;
            console.log($scope.product);
            Product.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['name']
            }, function(page) {
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        // status

        $scope.closeStatusSlide = function() {
            $scope.statusSlideChecked = false;
        };

        $scope.loadStatus = function() {
            $scope.statusSlideChecked = true;
        };

        $scope.selectState = function(step) {
            if (step.selected && step.selected === true) {
                step.selected = false;
                for (var i = 0, len = $scope.selected.status.length; i < len; i++) {
                    if ($scope.selected.status[i].id === step.id) {
                        $scope.selected.status.splice(i, 1);
                        break;
                    }
                }
            } else {
                step.selected = true;
                $scope.selected.status.push(step);
            }
            console.log('$scope.selectState():');
            console.log($scope.selected.status);
        };

        // process

        $scope.closeProcessSlide = function() {
            $scope.processSlideChecked = false;
            if ($scope.processProduct) {
                $scope.processProduct.active = false;
            }
        };

        $scope.loadProcesses = function(product) {
            console.log(product);
            $scope.processSlideChecked = true;
            $scope.processProduct = product;
            $scope.processProduct.active = true;
        };

        $scope.updateStep = function(product) {
            console.log('updateStep:');
            $scope.processProduct = product;
        };

        $scope.saveUpdateStep = function(process, stepId) {
            console.log('saveUpdateStep:');
            process.step.id = stepId;
            console.log(process);
            ObjectProcess.save({}, process, function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    console.log('refresh Processes:');
                    console.log(objectProcesses);
                    $scope.processProduct.processes = angular.copy(objectProcesses);
                });
            });
        };

        // details

        $scope.closeDetailsSlide = function() {
            $scope.detailsSlideChecked = false;
        };

        $scope.loadDetails = function(product) {
            $scope.detailsSlideChecked = true;
            $scope.processProduct = product;
        };

    }
])

.controller('ProductDetailsController', ['$scope', function($scope) {
    var $ = angular.element;

    $scope.initProductDetailsTabs = function() {
        $scope.defaultHeight = {
            height: $(window).height() - 100
        };
        
        $('#productDetailsTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    };

}])

.controller('ProductProcessController', ['$scope', '$filter', 'Process', 'ObjectProcess',
    function($scope, $filter, Process, ObjectProcess) {

        $scope.applyProcess = function(process) {
            
            var objectProcess = {
                objectId: $scope.processProduct.id,
                objectType: 2,
                process: {
                    id: process.id
                }
            };

            process.steps = $filter('orderBy')(process.steps, 'sequence');

            if (process.defaultStepId) {
                objectProcess.step = {
                    id: process.defaultStepId
                };
            } else {
                objectProcess.step = {
                    id: process.steps[0].id
                };
            }

            ObjectProcess.save({}, objectProcess).$promise.then(function(objectProcess) {
                return objectProcess;
            }).then(function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    console.log('refresh Processes:');
                    console.log(objectProcesses);
                    $scope.processProduct.processes = angular.copy(objectProcesses);
                    $scope.closeProcessSlide();
                });
            });

        };

        $scope.removeProcess = function(process) {
            var objectProcesses = $scope.processProduct.processes;
            var objectProcess;
            for (var i = 0, len = objectProcesses.length; i < len; i++) {
                objectProcess = objectProcesses[i];
                if (process.id === objectProcess.process.id) {
                    break;
                }
            }
            if (objectProcess) {
                ObjectProcess.remove({
                    id: objectProcess.id
                }, {}, function() {
                    ObjectProcess.getAll({
                        objectId: objectProcess.objectId
                    }).then(function(objectProcesses) {
                        console.log('refresh Processes:');
                        console.log(objectProcesses);
                        $scope.processProduct.processes = angular.copy(objectProcesses);
                        $scope.closeProcessSlide();
                    });
                });
            }

        };

        $scope.forObjectProcesses = function(step) {
            if ($scope.processProduct) {
                var objectProcesses = $scope.processProduct.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (objectProcess.process.id === step.processId) {
                        if (objectProcess.process.type === 1) {
                            if (step.sequence > objectProcess.step.sequence) {
                                return false;
                            } else if (step.sequence <= objectProcess.step.sequence) {
                                return true;
                            }
                        } else if (objectProcess.process.type === 2) {
                            if (step.id === objectProcess.step.id) {
                                return true;
                            } else if (step.id !== objectProcess.step.id) {
                                return false;
                            }
                        }
                    } else {
                        continue;
                    }
                }
                return false;
            }
        };

        $scope.appliedProcess = function(process) {
            if ($scope.processProduct) {
                var objectProcesses = $scope.processProduct.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (process.id === objectProcess.process.id) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
])

.controller('ProductOperatorController', ['$scope', '$state', '$stateParams', 'Product', 'Brand', 'Category', 'MadeFrom', 'Language', 'Currency', 'Tag',
    function($scope, $state, $stateParams, Product, Brand, Category, MadeFrom, Language, Currency, Tag) {

        console.clear();
        var $ = angular.element;
        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.template = {
            info: {
                url: 'views/product/product.operator.info.html?' + new Date(),
                attr: {
                    url: 'views/product/product.operator.info.attr.html?' + new Date()
                },
                price: {
                    url: 'views/product/product.operator.info.price.html?' + new Date()
                },
                group: {
                    url: 'views/product/product.operator.info.group.html?' + new Date()
                }
            },
            img: {
                url: 'views/product/product.operator.img.html?' + new Date()
            },
            multilanguage: {
                url: 'views/product/product.operator.multilanguage.html?' + new Date()
            },
            multicurrency: {
                url: 'views/product/product.operator.multicurrency.html?' + new Date()
            }
        };

        $scope.productTypes = [{
            label: '简单商品',
            value: 0
        }, {
            label: '组合商品',
            value: 1
        }];

        $scope.brands = [];
        $scope.categories = [];
        $scope.madeFroms = [];
        $scope.languages = [];
        $scope.currencies = [];
        $scope.tags = [];
        $scope.members = [];
        $scope.product = {
            weight: 0,
            priceL1: 0.00,
            priceL2: 0.00,
            priceL3: 0.00,
            priceL4: 0.00,
            priceL5: 0.00,
            priceL6: 0.00,
            priceL7: 0.00,
            priceL8: 0.00,
            priceL9: 0.00,
            priceL10: 0.00,
            productType: $scope.productTypes[0],
            multiLanguages: [],
            multiCurrencies: [],
            members: []
        };
        $scope.action = 'create';

        Brand.getAll().then(function(brands) {
            $scope.brands = brands;
        }).then(function() {
            return Category.getAll().then(function(categories) {
                $scope.categories = categories;
            });
        }).then(function() {
            return MadeFrom.getAll().then(function(madeFroms) {
                $scope.madeFroms = madeFroms;
            });
        }).then(function() {
            return Language.getAll().then(function(languages) {
                $scope.languages = languages;
            });
        }).then(function() {
            return Currency.getAll().then(function(currencies) {
                $scope.currencies = currencies;
            });
        }).then(function() {
            return Tag.getAll().then(function(tags) {
                $scope.tags = tags;
            });
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                Product.get({
                        id: $stateParams.id
                    }, {}).$promise
                    .then(function(product) {
                        console.log('[' + $scope.action + '] loading product');
                        console.log(product);
                        product.productType = $scope.productTypes[product.productType];
                        $scope.product = product;
                        return product;
                    }).then(function(product) {
                        $scope.title = product.productType.label;
                        if (product.productType.value === 1) {
                            Product.getAll({
                                productType: 0,
                                deleted: false
                            }).then(function(members) {
                                // console.log('members:');
                                // console.log(members);
                                $scope.members = members;
                            });
                        }
                    });
            }
        });

        $('#productTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    }
])

.controller('ProductInformationController', ['$scope', '$state', '$stateParams', 'Product',
    function($scope, $state, $stateParams, Product) {

        var $ = angular.element;
        var defaultMember = {
            quantity: 1
        };
        $scope.member = angular.copy(defaultMember);
        var defaultSum = {
            totalPriceL1: 0,
            totalPriceL2: 0,
            totalPriceL3: 0,
            totalPriceL4: 0,
            totalPriceL5: 0,
            totalPriceL6: 0,
            totalPriceL7: 0,
            totalPriceL8: 0,
            totalPriceL9: 0,
            totalPriceL10: 0
        };
        $scope.sum = angular.copy(defaultSum);

        $scope.changeProductType = function($item) {
            //console.clear();
            console.log('$item:');
            console.log($item);

            if ($item.value === 1) {
                Product.getAll({
                    productType: 0,
                }).then(function(members) {
                    console.log('members:');
                    console.log(members);
                    $scope.members = members;
                });
            }
        };

        $scope.save = function(product) {
            //console.clear();
            console.log('[' + $scope.action + '] save product');
            product.productType = product.productType.value;
            if (product.productType === 0) {
                product.members.length = 0;
            }
            console.log(product);
            Product.save({
                action: $scope.action
            }, product, function(product) {
                console.log('[' + $scope.action + '] save product complete:');
                console.log(product);
                $state.go('product');
            });
        };

        $scope.saveMember = function(memberAddForm, member) {
            //console.clear();
            console.log('[' + $scope.action + '] saveMember complete:');
            console.log(member);
            $scope.product.members.push(angular.copy(member));
            memberAddForm.$setPristine();
            $scope.member = angular.copy(defaultMember);
        };

        $scope.updateMember = function(member) {
            //console.clear();
            console.log('updateMember:');
            console.log(member);
            member.editable = true;
        };

        $scope.saveUpdateMember = function(member, memberForm) {
            //console.clear();
            console.log('[' + $scope.action + '] saveUpdateMember complete:');
            console.log(member);
            member.editable = false;
            memberForm.$setPristine();

        };

        $scope.removingMember = undefined;

        $scope.showRemoveMember = function(member, $index) {
            console.clear();
            console.log('showRemoveMember $index: ' + $index);
            console.log(member);

            $scope.removingMember = member;
            $scope.removingMember.$index = $index;
            $('#memberDeleteModal').modal('show');
        };

        $scope.removeMember = function() {
            console.clear();
            console.log('removeMember:');
            console.log($scope.removingBrand);

            if (angular.isDefined($scope.removingMember)) {
                $scope.product.members.splice($scope.removingMember.$index, 1);
                $scope.removingBrand = undefined;
                $('#memberDeleteModal').modal('hide');
            }
        };

        $scope.$watch('product.productType', function(obj) {
            // console.log('$watch -> product.productType:');
            // console.log(obj);
            if (obj) {
                $scope.title = obj.label;
                if (obj.value === 0) {
                    $scope.passed = true;
                } else if (obj.value === 1 && $scope.product.members.length > 0) {
                    $scope.passed = true;
                } else {
                    $scope.passed = false;
                }
            } else {
                $scope.passed = false;
            }
        });

        $scope.$watch('product.members', function(members) {
            // console.log('$watch -> product.members:');
            // console.log(members);
            if ($scope.product.productType && $scope.product.productType.value === 1) {
                if (members.length > 0) {
                    $scope.passed = true;
                } else {
                    $scope.passed = false;
                }
            }
            $scope.sum = angular.copy(defaultSum);
            angular.forEach(members, function(member) {
                var quantity = parseInt(member.quantity);
                $scope.sum.totalPriceL1 += parseFloat(member.product.priceL1) * quantity;
                $scope.sum.totalPriceL2 += parseFloat(member.product.priceL2) * quantity;
                $scope.sum.totalPriceL3 += parseFloat(member.product.priceL3) * quantity;
                $scope.sum.totalPriceL4 += parseFloat(member.product.priceL4) * quantity;
                $scope.sum.totalPriceL5 += parseFloat(member.product.priceL5) * quantity;
                $scope.sum.totalPriceL6 += parseFloat(member.product.priceL6) * quantity;
                $scope.sum.totalPriceL7 += parseFloat(member.product.priceL7) * quantity;
                $scope.sum.totalPriceL8 += parseFloat(member.product.priceL8) * quantity;
                $scope.sum.totalPriceL9 += parseFloat(member.product.priceL9) * quantity;
                $scope.sum.totalPriceL10 += parseFloat(member.product.priceL10) * quantity;
            });
            $scope.product.priceL1 = $scope.sum.totalPriceL1;
            $scope.product.priceL2 = $scope.sum.totalPriceL2;
            $scope.product.priceL3 = $scope.sum.totalPriceL3;
            $scope.product.priceL4 = $scope.sum.totalPriceL4;
            $scope.product.priceL5 = $scope.sum.totalPriceL5;
            $scope.product.priceL6 = $scope.sum.totalPriceL6;
            $scope.product.priceL7 = $scope.sum.totalPriceL7;
            $scope.product.priceL8 = $scope.sum.totalPriceL8;
            $scope.product.priceL9 = $scope.sum.totalPriceL9;
            $scope.product.priceL10 = $scope.sum.totalPriceL10;
        }, true);
    }
])

.controller('ProductImageController', ['$scope', '$cookies', '$stateParams',
    function($scope, $cookies, $stateParams) {

        var $ = angular.element;
        var id = 0;
        if ($stateParams.id && $stateParams.id !== '') {
            id = $stateParams.id;
        } else {
            id = 0;
        }

        $scope.initImgFunction = function() {
            $('input[type="file"][name="image"]').on('change', function() {
                console.log(this.value.substring(this.value.lastIndexOf('\\') + 1));
                var imgUrl = this.value.substring(this.value.lastIndexOf('\\') + 1);
                if (id !== 0) {
                    imgUrl = id + '-' + imgUrl;
                }
                var field = this.id;

                $.ajax({
                    url: '/api/products/image/upload/' + id + '/' + field,
                    type: 'POST',
                    data: new FormData($('#' + this.id + 'Form')[0]),
                    enctype: 'multipart/form-data',
                    headers: {
                        'X-XSRF-TOKEN': $cookies.get('XSRF-TOKEN')
                    },
                    processData: false,
                    contentType: false,
                    cache: false,
                    success: function() {
                        $scope.$apply(function() {
                            $scope.product[field] = imgUrl;
                        });
                    },
                    error: function() {}
                });
            });
        };

    }
])

.controller('ProductMultiLanguageController', ['$scope', '$stateParams', 'ProductMultiLanguage',
    function($scope, $stateParams, ProductMultiLanguage) {

        var $ = angular.element;
        $scope.multiLanguage = {};

        $scope.saveLanguage = function(mlAddForm, multiLanguage) {
            console.clear();
            console.log('[' + $scope.action + '] saveLanguage:');
            console.log(multiLanguage);

            if ($scope.action === 'create') {
                console.log('[' + $scope.action + '] saveLanguage complete:');
                console.log(multiLanguage);
                $scope.product.multiLanguages.push(angular.copy(multiLanguage));
                mlAddForm.$setPristine();
                $scope.multiLanguage = {};
            } else if ($scope.action === 'update') {
                multiLanguage.productId = $stateParams.id;
                ProductMultiLanguage.save({}, multiLanguage, function(ml) {
                    console.log('[' + $scope.action + '] saveLanguage complete:');
                    console.log(ml);
                    $scope.product.multiLanguages.push(angular.copy(ml));
                    mlAddForm.$setPristine();
                    $scope.multiLanguage = {};
                });
            }
        };

        $scope.updateLanguage = function(ml) {
            console.clear();
            console.log('updateLanguage:');
            console.log(ml);
            ml.editable = true;
        };

        $scope.saveUpdateLanguage = function(ml, mlForm) {
            console.clear();
            console.log('[' + $scope.action + '] saveUpdateLanguage complete:');
            if ($scope.action === 'create') {
                console.log(ml);
                ml.editable = false;
                mlForm.$setPristine();
            } else if ($scope.action === 'update') {
                ProductMultiLanguage.save({}, ml, function() {
                    console.log(ml);
                    ml.editable = false;
                    mlForm.$setPristine();
                });
            }
        };

        $scope.removingLanguage = undefined;

        $scope.showRemoveLanguage = function(ml, $index) {
            console.clear();
            console.log('showRemoveLanguage $index: ' + $index);
            console.log(ml);

            $scope.removingLanguage = ml;
            $scope.removingLanguage.$index = $index;
            $('#mlDeleteModal').modal('show');
        };

        $scope.removeLanguage = function() {
            console.clear();
            console.log('removeLanguage:');
            console.log($scope.removingLanguage);

            if (angular.isDefined($scope.removingLanguage)) {
                if ($scope.action === 'create') {
                    $scope.product.multiLanguages.splice($scope.removingLanguage.$index, 1);
                    $scope.removingLanguage = undefined;
                    $('#mlDeleteModal').modal('hide');
                } else if ($scope.action === 'update') {
                    ProductMultiLanguage.remove({
                        id: $scope.removingLanguage.id
                    }, {}, function() {
                        $scope.product.multiLanguages.splice($scope.removingLanguage.$index, 1);
                        $scope.removingLanguage = undefined;
                        $('#mlDeleteModal').modal('hide');
                    });
                }
            }
        };

    }
])

.controller('ProductMultiCurrencyController', ['$scope', '$stateParams', 'ProductMultiCurrency',
    function($scope, $stateParams, ProductMultiCurrency) {

        var $ = angular.element;
        var defaultMultiCurrency = {
            priceL1: 0.00,
            priceL2: 0.00,
            priceL3: 0.00,
            priceL4: 0.00,
            priceL5: 0.00,
            priceL6: 0.00,
            priceL7: 0.00,
            priceL8: 0.00,
            priceL9: 0.00,
            priceL10: 0.00
        };
        $scope.multiCurrency = angular.copy(defaultMultiCurrency);

        $scope.saveCurrency = function(mcAddForm, multiCurrency) {
            console.clear();
            console.log('[' + $scope.action + '] saveCurrency:');
            console.log(multiCurrency);

            if ($scope.action === 'create') {
                console.log('[' + $scope.action + '] saveCurrency complete:');
                console.log(multiCurrency);
                $scope.product.multiCurrencies.push(angular.copy(multiCurrency));
                mcAddForm.$setPristine();
                $scope.multiCurrency = angular.copy(defaultMultiCurrency);
            } else if ($scope.action === 'update') {
                multiCurrency.productId = $stateParams.id;
                ProductMultiCurrency.save({}, multiCurrency, function(mc) {
                    console.log('[' + $scope.action + '] saveCurrency complete:');
                    console.log(mc);
                    $scope.product.multiCurrencies.push(angular.copy(mc));
                    mcAddForm.$setPristine();
                    $scope.multiCurrency = defaultMultiCurrency;
                });
            }
        };

        $scope.updateCurrency = function(mc) {
            console.clear();
            console.log('updateCurrency:');
            console.log(mc);
            mc.editable = true;
        };

        $scope.saveUpdateCurrency = function(mc) {
            console.clear();
            console.log('[' + $scope.action + '] updateCurrency complete:');
            if ($scope.action === 'create') {
                console.log(mc);
                mc.editable = false;
            } else if ($scope.action === 'update') {
                ProductMultiCurrency.save({}, mc, function() {
                    console.log(mc);
                    mc.editable = false;
                });
            }
        };

        $scope.removingCurrency = undefined;

        $scope.showRemoveCurrency = function(mc, $index) {
            console.clear();
            console.log('showRemoveCurrency $index: ' + $index);
            console.log(mc);

            $scope.removingCurrency = mc;
            $scope.removingCurrency.$index = $index;
            $('#mcDeleteModal').modal('show');
        };

        $scope.removeCurrency = function() {
            console.clear();
            console.log('removeCurrency:');
            console.log($scope.removingCurrency);

            if (angular.isDefined($scope.removingCurrency)) {
                if ($scope.action === 'create') {
                    $scope.product.multiCurrencies.splice($scope.removingCurrency.$index, 1);
                    $scope.removingCurrency = undefined;
                    $('#mcDeleteModal').modal('hide');
                } else if ($scope.action === 'update') {
                    ProductMultiCurrency.remove({
                        id: $scope.removingCurrency.id
                    }, {}, function() {
                        $scope.product.multiCurrencies.splice($scope.removingCurrency.$index, 1);
                        $scope.removingCurrency = undefined;
                        $('#mcDeleteModal').modal('hide');
                    });
                }
            }
        };

    }
]);

angular.module('ecommApp')

.controller('TagController', ['$rootScope', '$scope', 'Tag', 'Utils',
    function($rootScope, $scope, Tag, Utils) {

    	var $ = angular.element;
        
        $scope.template = {
            operator: {
                url: 'views/product/tag/tag.operator.html?' + (new Date())
            }
        };

    	$scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.tagSlideChecked = false;

        $scope.refresh = function() {
            Tag.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id,desc']
            }, function(page) {
                console.clear();
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.closeTagSlide();
            });
        };

        $scope.refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Tag.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id,desc']
                }, function(page) {
                    console.clear();
                    console.log('turnPage:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };
       
        $scope.updateTag = function(tag) {
            console.clear();
            console.log('updateTag:');
            console.log(tag);
            $scope.tag = tag;
            $scope.operateTag();
        };

        $scope.removingTag = undefined;

        $scope.showRemoveTag = function(tag, $index) {
            console.clear();
            console.log('showRemoveTag $index: ' + $index);
            console.log(tag);

            $scope.removingTag = tag;
            $('#tagDeleteModal').modal('show');
        };

        $scope.removeTag = function() {
            console.clear();
            console.log('removeTag:');
            console.log($scope.removingTag);

            if (angular.isDefined($scope.removingTag)) {
                Tag.remove({
                    id: $scope.removingTag.id
                }, {}, function() {
                    $scope.removingTag = undefined;
                    $('#tagDeleteModal').modal('hide');
                    $scope.refresh();
                });
            }
        };

        $scope.saveTag = function(tagForm, tag) {
            console.clear();
            console.log('saveTag:');
            console.log(tag);

            Tag.save({}, tag, function(tag) {
                console.log('saveTag complete:');
                console.log(tag);
                tagForm.$setPristine();
                $scope.tag = angular.copy($scope.defaultTag);
                $scope.refresh();
            });
        };

        // operator

        $scope.closeTagSlide = function() {
            $scope.tagSlideChecked = false;
        };

        $scope.operateTag = function(action) {
            if (action === 'create') {
                $scope.tag = {};
            }
            $scope.tagSlideChecked = true;
        };
    }
]);

angular.module('ecommApp')

.controller('RegisterController', function($scope, Auth) {

	$scope.user = {
		username: 'cook1fan',
		password: 'kuangde43',
		email: '32582048@qq.com'
	};

    $scope.register = function() {
    	console.log($scope.user);
        Auth.createAccount($scope.user).then(function() {
            $scope.success = 'OK';
        }).catch(function(response) {  
            console.log(response);
        });
    };
});

angular.module('ecommApp')

.controller('SupplierController', ['$rootScope', '$scope', 'Supplier', 'Utils',
    function($rootScope, $scope, Supplier, Utils) {

    	var $ = angular.element;
        
        $scope.template = {
            operator: {
                url: 'views/supplier/supplier.operator.html?' + (new Date())
            }
        };

    	$scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultSupplier = {
            deleted: false
        };
        $scope.supplier = angular.copy($scope.defaultSupplier);
        $scope.supplierSlideChecked = false;
        $scope.title = '';

        $scope.refresh = function() {
            Supplier.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id,desc'],
                deleted: false
            }, function(page) {
                console.clear();
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.closeSupplierSlide();
            });
        };

        $scope.refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Supplier.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id,desc'],
                    deleted: false
                }, function(page) {
                    console.clear();
                    console.log('turnPage:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };
       
        $scope.updateSupplier = function(supplier) {
            console.clear();
            console.log('updateSupplier:');
            console.log(supplier);
            $scope.supplier = supplier;
            $scope.operateSupplier();
        };

        $scope.removingSupplier = undefined;

        $scope.showRemoveSupplier = function(supplier, $index) {
            console.clear();
            console.log('showRemoveSupplier $index: ' + $index);
            console.log(supplier);

            $scope.removingSupplier = supplier;
            $('#supplierDeleteModal').modal('show');
        };

        $scope.removeSupplier = function() {
            console.clear();
            console.log('removeSupplier:');
            console.log($scope.removingSupplier);

            if (angular.isDefined($scope.removingSupplier)) {
                $scope.removingSupplier.deleted = true;
                Supplier.save({}, $scope.removingSupplier, function() {
                    $scope.removingSupplier = undefined;
                    $('#supplierDeleteModal').modal('hide');
                    $scope.refresh();
                });
            }
        };

        $scope.saveSupplier = function(supplierForm, supplier) {
            console.clear();
            console.log('saveSupplier:');
            console.log(supplier);

            Supplier.save({}, supplier, function(supplier) {
                console.log('saveSupplier complete:');
                console.log(supplier);
                supplierForm.$setPristine();
                $scope.supplier = angular.copy($scope.defaultSupplier);
                $scope.refresh();
            });
        };

        // operator

        $scope.closeSupplierSlide = function() {
            $scope.supplierSlideChecked = false;
        };

        $scope.operateSupplier = function(action) {
            $scope.title = '编辑';
            if (action === 'create') {
                $scope.title = '创建';
                $scope.supplier = angular.copy($scope.defaultSupplier);
            }
            $scope.supplierSlideChecked = true;
        };
    }
]);

angular.module('ecommApp')

.controller('RoleController', ['$rootScope', '$scope', 'Authority', 'Role',
    function($rootScope, $scope, Authority, Role) {

        $scope.reset = function() {
            $scope.authoritiesDoneWarning = false;
            $scope.role = {};
            if ($scope.userRoleForm) {
                $scope.userRoleForm.$setPristine();
            }
            angular.forEach($scope.authorities, function(authority) {
                authority.done = false;
            });
        };

        $scope.reset();

        $scope.roles = null;

        Role.query(function(roles) {
            console.log(roles);
            $scope.roles = roles;
            authoritiesToString($scope.roles);
        });

        $scope.authorities = [];

        Authority.query(function(authorities) {
            $scope.authorities = authorities;
        });

        $scope.save = function() {
            $scope.role.authorities = [];
            angular.forEach($scope.authorities, function(authority) {
                if (authority.done) {
                    $scope.role.authorities.push(authority);
                }
            });
            if ($scope.role.authorities.length === 0) {
                $scope.authoritiesDoneWarning = true;
                return false;
            } else {
                $scope.authoritiesDoneWarning = false;
            }

            Role.save({}, $scope.role, function(role) {
                authoritiesToString(role);
                refreshRoles(role);
                $scope.reset();
            }, function(err) {
                console.log(err);
            });
        };

        $scope.remove = function(id) {
            Role.remove({
                id: id
            }, {}, function() {
                var old = $scope.roles;
                $scope.roles = [];
                angular.forEach(old, function(role) {
                    if (role.id !== id) {
                        $scope.roles.push(role);
                    }
                });
                $scope.reset();
            }, function(err) {
                console.log(err);
            });
        };

        $scope.select = function(role) {
            $scope.role = role;
            angular.forEach($scope.authorities, function(authority) {
                angular.forEach($scope.role.authorities, function(roleAuhority) {
                    if (authority.id === roleAuhority.id) {
                        authority.done = true;
                        return;
                    }
                });
            });
        };

        function authoritiesToString(roles) {
            if (!angular.isArray(roles)) {
                roles = [roles];
            }
            angular.forEach(roles, function(role) {
                role.authorityString = '';
                var authorities = role.authorities;
                for (var i = 0, len = authorities.length - 1; i < len; i++) {
                    role.authorityString += authorities[i].name + ', ';
                }
                role.authorityString += authorities[authorities.length - 1].name;
            });
        }

        function refreshRoles(role) {
        	var isExist = false;
        	angular.forEach($scope.roles, function(r){
        		if (r.id === role.id) {
        			r.authorityString = role.authorityString;
        			console.log(r);
        			isExist = true;
        			return;
        		}
        	});
        	if (!isExist) {
                $scope.roles.push(role);
            }
        }

    }
]);

angular.module('ecommApp')

.controller('ShopController', ['$rootScope', '$scope', 'Shop', 'Utils', 'Process',
    function($rootScope, $scope, Shop, Utils, Process) {

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.processSlideChecked = false;
        $scope.processShop = undefined;
        $scope.action = undefined;

        $scope.template = {
            process: {
                url: 'views/system/shop/shop.process.html?' + (new Date())
            }
        };

        Shop.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['name']
        }).$promise.then(function(page) {
            console.clear();
            console.log('page:');
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        }).then(function() {
            Process.getAll({
                deleted: false,
                objectType: 1
            }).then(function(processes) {
                console.log('Process.getAll:');
                console.log(processes);
                $scope.processes = processes;
                //initStatus(processes);
            });
        });

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Shop.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['name']
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        // process

        $scope.colseProcessSlide = function() {
            $scope.processSlideChecked = false;
            if ($scope.processShop) {
                $scope.processShop.active = false;
            }
        };

        $scope.loadProcesses = function(shop, action) {
            console.clear();
            console.log('loadProcesses:');
            console.log(shop);
            $scope.action = action;
            $scope.processSlideChecked = true;
            $scope.processShop = shop;
            $scope.processShop.active = true;
        };

        $scope.applyState = function(step) {
            console.clear();
            console.log('applyState:[' + $scope.action + ']');
            console.log(step);
            if ($scope.action === 'init') {
                $scope.processShop.initStep = angular.copy(step);
            } else if ($scope.action === 'complete') {
                $scope.processShop.completeStep = angular.copy(step);
            } else if ($scope.action === 'error') {
                $scope.processShop.errorStep = angular.copy(step);
            }
            console.log($scope.processShop);
            Shop.save({}, $scope.processShop, function(shop){
                console.log('applyState complete:');
                console.log(shop);
                $scope.colseProcessSlide();
            });
        };
    }
])

.controller('ShopOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'User', 'Language', 'Currency', 'Shop', 'Supplier', 'Warehouse',
    function($rootScope, $scope, $state, $stateParams, User, Language, Currency, Shop, Supplier, Warehouse) {

        var $ = angular.element;
        $scope.users = [];
        $scope.languages = [];
        $scope.currencies = [];
        $scope.warehouses = [];
        $scope.suppliers = [];
        $scope.types = [{
            id: 0,
            label: '自营'
        }, {
            id: 1,
            label: '合作'
        }];
        $scope.statuses = [{
            id: 0,
            label: '禁用'
        }, {
            id: 1,
            label: '正常'
        }];
        $scope.lvls = [{
            id: 0,
            label: 'Level 0'
        }, {
            id: 1,
            label: 'Level 1'
        }, {
            id: 2,
            label: 'Level 2'
        }, {
            id: 3,
            label: 'Level 3'
        }, {
            id: 4,
            label: 'Level 4'
        }, {
            id: 5,
            label: 'Level 5'
        }, {
            id: 6,
            label: 'Level 6'
        }, {
            id: 7,
            label: 'Level 7'
        }, {
            id: 8,
            label: 'Level 8'
        }, {
            id: 9,
            label: 'Level 9'
        }, {
            id: 10,
            label: 'Level 10'
        }, ];
        $scope.shop = {
            type: 0,
            status: 1,
            apiCallLimit: -1,
            language: {
                id: 100
            },
            currency: {
                id: 101
            },
            priceLevel: 0,
            tunnels: []
        };
        $scope.tunnelTypes = [{
            label: '仓库配货方式(自营)',
            value: 1
        }, {
            label: '供应商配货方式(代发)',
            value: 2
        }];
        $scope.tunnelBehaviors = [{
            label: '包括',
            value: 1
        }, {
            label: '排除',
            value: 2
        }];
        $scope.tunnelDefaultOptions = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];
        $scope.tunnel = {};

        $scope.action = 'create';

        function initField(tunnel) {
            tunnel.type = $scope.tunnelTypes[tunnel.type - 1];
            tunnel.behavior = $scope.tunnelBehaviors[tunnel.behavior - 1];
            tunnel.defaultOption = $scope.tunnelDefaultOptions[tunnel.defaultOption - 1];
        }

        function refreshField(tunnel) {
            tunnel.type = tunnel.type.value;
            tunnel.behavior = tunnel.behavior.value;
            tunnel.defaultOption = tunnel.defaultOption.value;
        }

        console.clear();

        User.getAll().then(function(users) {
            console.log('users loaded');
            $scope.users = users;
        }).then(function() {
            return Language.getAll().then(function(languages) {
                console.log('languaegs loaded');
                $scope.languages = languages;
            });
        }).then(function() {
            return Currency.getAll().then(function(currencies) {
                console.log('currencies loaded');
                $scope.currencies = currencies;
            });
        }).then(function() {
            return Warehouse.getAll().then(function(warehouses) {
                console.log('warehouses loaded');
                $scope.warehouses = warehouses;
            });
        }).then(function() {
            return Supplier.getAll({
                deleted: false
            }).then(function(suppliers) {
                console.log('suppliers loaded');
                $scope.suppliers = suppliers;
            });
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                Shop.get({
                    id: $stateParams.id
                }, {}, function(shop) {
                    $scope.shop = shop;
                    angular.forEach($scope.shop.tunnels, function(tunnel) {
                        initField(tunnel);
                    });
                    console.log(shop);
                });
            }
        });

        $scope.saveShop = function(shop) {
            console.clear();
            console.log('saveShop:');

            angular.forEach(shop.tunnels, function(tunnel) {
                refreshField(tunnel);
            });

            console.log(shop);

            Shop.save({}, shop, function() {
                $state.go('shop');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.remove = function() {
            Shop.remove({
                id: $stateParams.id
            }, {}, function() {
                $state.go('shop');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.saveTunnel = function(tunnelAddForm, tunnel) {
            console.clear();
            console.log('[' + $scope.action + '] saveTunnel complete:');
            console.log(tunnel);

            $scope.shop.tunnels.push(angular.copy(tunnel));
            tunnelAddForm.$setPristine();
            $scope.tunnel = angular.copy($scope.defaultTunnel);
        };

        $scope.updateTunnel = function(tunnel) {
            //console.clear();
            console.log('updateTunnel:');
            console.log(tunnel);
            tunnel.editable = true;
        };

        $scope.saveUpdateTunnel = function(tunnel, tunnelForm) {
            //console.clear();
            if (tunnel.type.value === 1) {
                tunnel.suppliers.length = 0;
            } else if (tunnel.type.value === 2) {
                tunnel.warehouses.length = 0;
            }
            console.log('[' + $scope.action + '] saveUpdateTunnel complete:');
            console.log(tunnel);
            tunnel.editable = false;
            tunnelForm.$setPristine();

        };

        $scope.removingTunnel = undefined;

        $scope.showRemoveTunnel = function(tunnel, $index) {
            console.clear();
            console.log('showRemoveTunnel $index: ' + $index);
            console.log(tunnel);

            $scope.removingTunnel = tunnel;
            $scope.removingTunnel.$index = $index;
            $('#tunnelDeleteModal').modal('show');
        };

        $scope.removeTunnel = function() {
            console.clear();
            console.log('removeTunnel:');
            console.log($scope.removingTunnel);

            if (angular.isDefined($scope.removingTunnel)) {
                $scope.shop.tunnels.splice($scope.removingTunnel.$index, 1);
                $scope.removingTunnel = undefined;
                $('#tunnelDeleteModal').modal('hide');
            }
        };
    }
]);

angular.module('ecommApp')

.controller('UserController', ['$rootScope', '$scope', 'User',
    function($rootScope, $scope, User) {

        $scope.totalPagesList = [];

        User.get({
            page: 0,
            size: 20
        }, function(page) {
            console.log(page);
            $scope.page = page;
            rolesToString($scope.page.content);
            if (page.totalPages > 0) {
                for (var i = 0, len = page.totalPages; i < len; i++) {
                    $scope.totalPagesList.push(i);
                }
            }
        });

        function rolesToString(users) {
            if (!angular.isArray(users)) {
                users = [users];
            }
            angular.forEach(users, function(user) {
                user.roleString = '';
                var roles = user.roles;
                for (var i = 0, len = roles.length - 1; i < len; i++) {
                    user.roleString += roles[i].name + ', ';
                }
                user.roleString += roles[roles.length - 1].name;
            });
        }

        $scope.turnPage = function(number) {
            console.log(number);
            if (number > -1 && number < $scope.page.totalPages) {
                User.get({
                    page: number,
                    size: 5
                }, function(page) {
                    $scope.page = page;
                    rolesToString($scope.page.content);
                });
            }
        };
    }
])

.controller('UserOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', '$q', 'User', 'Role',
    function($rootScope, $scope, $state, $stateParams, $q, User, Role) {

        $scope.user = {};
        $scope.roles = {};
        $scope.action = 'create';
        var oldpassword = '';

        Role.query().$promise.then(function(roles) {
            $scope.roles = roles;
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                User.get({
                    id: $stateParams.id
                }, {}, function(user) {
                    oldpassword = user.password;
                    user.password = '********';
                    $scope.user = user;
                    angular.forEach($scope.roles, function(role) {
                        angular.forEach($scope.user.roles, function(userRole) {
                            if (role.id === userRole.id) {
                                role.done = true;
                                return;
                            }
                        });
                    });
                }, function(err) {
                    console.log(err);
                });
            }
        });

        $scope.save = function(user) {
            $scope.user.roles = [];
            angular.forEach($scope.roles, function(role) {
                if (role.done) {
                    $scope.user.roles.push(role);
                }
            });
            if ($scope.user.roles.length === 0) {
                $scope.rolesDoneWarning = true;
                return false;
            } else {
                $scope.rolesDoneWarning = false;
            }

            if ($scope.action === 'update' && $scope.user.password === '********') {
                $scope.user.password = oldpassword;
            }

            User.save({}, user, function() {
                $state.go('user');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.remove = function() {
            User.remove({
                id: $stateParams.id
            }, {}, function() {
                $state.go('user');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.clean = function() {
            if ($scope.action === 'update') {
                $scope.user.password = '';
            }
        };
    }
]);

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
