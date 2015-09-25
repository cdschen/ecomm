angular.module('ecommApp')

.config(['$stateProvider', 'ROLES',
    function($stateProvider, ROLES) {

        $stateProvider
            .state('shipment', {
                parent: 'site',
                url: '/shipments',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/shipment/shipment.html?' + (new Date()),
                        controller: 'ShipmentController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            })
            .state('shipment.shipmentGenerate', {
                url: '/shipment-generate',
                views: {
                    'content@': {
                        templateUrl: 'views/inventory/shipment/shipment-generate.html?' + (new Date()),
                        controller: 'ShipmentGenerateController'
                    }
                },
                data: {
                    roles: [ROLES.sysadmin],
                    authorities: []
                }
            });
    }
]);
