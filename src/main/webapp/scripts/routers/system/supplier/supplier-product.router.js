angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('supplierProduct', {
            parent: 'site',
            url: '/supplier-products',
            views: {
                'content@': {
                    templateUrl: 'views/system/supplier/supplier-product/supplier-product.html?' + t,
                    controller: 'SupplierProductController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SUPPLIER_ADMIN]
            }
        })
        .state('supplierProduct.operator', {
            url: '/supplier-product/:id',
            views: {
                'content@': {
                    templateUrl: 'views/system/supplier/supplier-product/supplier-product.operator.html?' + t,
                    controller: 'SupplierProductOperatorController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SUPPLIER_ADMIN]
            }
        });
}]);
