'use strict';

// LocalStorageModule 提供 web存储功能
// ui.router 提供 路由服务
// ngResource Angular官方提供， 与服务器交互的模块
angular.module('ecommApp', ['toastr', 'ngTinyScrollbar', 'LocalStorageModule', 'ui.router', 'ngResource', 'ngMessages', 'ngCookies', 'ui.select', 'ngSanitize', 'ngAnimate', 'ngDragDrop', 'pageslide-directive', 'ui.bootstrap'])

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
}])

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
            })

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
            })

        //商品模块路由
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
                        templateUrl: 'views/product/brand.html?' + (new Date()),
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
            })


        // 库存模块路由
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
            })

        //流程模块路由
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
        })

        //供应商模块路由
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
