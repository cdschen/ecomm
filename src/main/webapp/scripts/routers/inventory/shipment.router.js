angular.module('ecommApp')

.config(['$stateProvider', 'ROLES', function($stateProvider, ROLES) {

    var t = $.now();

    $stateProvider
        .state('shipment', {
            parent: 'site',
            url: '/shipments',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/shipment/shipment.html?' + t,
                    controller: 'ShipmentController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SHIPMENT_ADMIN]
            }
        })
        .state('shipment.shipmentGenerate', {
            url: '/shipment-generate',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/shipment/shipment-generate.html?' + t,
                    controller: 'ShipmentGenerateController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SHIPMENT_ADMIN]
            }
        })
        .state('shipment.shipmentImport', {
            url: '/shipment-import',
            views: {
                'content@': {
                    templateUrl: 'views/inventory/shipment/shipment-import.html?' + t,
                    controller: 'ShipmentImportController'
                }
            },
            data: {
                roles: [ROLES.SYSTEM_ADMIN, ROLES.SHIPMENT_ADMIN]
            }
        });
}]);