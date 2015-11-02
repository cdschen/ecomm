angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('dashboard', {
            parent: 'site',
            url: '/dashboard',
            views: {
                'content@': {
                    templateUrl: 'views/dashboard.html?' + t,
                }
            },
            data: {
                roles: [
                    ROLES.SYSTEM_ADMIN, ROLES.TASK_ADMIN, ROLES.ORDER_ADMIN, ROLES.PRODUCT_ADMIN,
                    ROLES.BRAND_ADMIN, ROLES.SOURCE_ADMIN, ROLES.TAG_ADMIN, ROLES.INVENTORY_ADMIN,
                    ROLES.INVENTORY_SNAPSHOT_ADMIN, ROLES.OUT_INVENTORY_SHEET_ADMIN, ROLES.SHIPMENT_ADMIN, ROLES.PURCHASE_ADMIN,
                    ROLES.CUSTOMER_ADMIN, ROLES.USER_ADMIN, ROLES.SHOP_ADMIN, ROLES.WAREHOUSE_ADMIN,
                    ROLES.PROCESS_ADMIN, ROLES.SUPPLIER_ADMIN, ROLES.RECEIVE_ADMIN
                ]
            }
        })
        .state('accessDenied', {
            parent: 'site',
            url: '/access-denied',
            views: {
                'content@': {
                    templateUrl: 'views/access-denied.html?' + t,
                }
            },
            data: {
                roles: []
            }
        });
}]);
