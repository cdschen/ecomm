app.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider

            //订单模块路由
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
        //.state('product.operator', {
        //    url: '/product/:id',
        //    views: {
        //        'content@': {
        //            templateUrl: 'views/product/product.operator.html?' + (new Date()),
        //            controller: 'ProductOperatorController'
        //        }
        //    },
        //    data: {
        //        roles: [ROLES.sysadmin],
        //        authorities: []
        //    }
        //})
        ;
    }
]);
