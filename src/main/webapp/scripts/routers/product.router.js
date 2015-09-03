app.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider

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
            });
    }
]);
