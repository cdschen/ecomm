
angular.module('ecommApp')

.factory('shipmentService', ['$resource', '$http', function($resource, $http) {

    var operationReviewCompleteShipment = undefined;
    var shipment = $resource('/api/shipments/:id', {}, {});

        shipment.getAll = function(params)
        {
            return $http.get('/api/shipments/get/all', {
                params: params
            }).then(function(res) {
                return res.data;
            });
        };

        shipment.saveShipments = function(shipment)
        {
            return $http.post('/api/saveShipments', shipment )
                .then(function(res) {
                    return res.data;
                });
        };

        /* 批量选中 */
        shipment.selectedShipments = [];
        /* 单个选中 */
        shipment.selectedShipment = {};

        shipment.confirmOperationReviewWhenCompleteShipment = function(reviewDTO)
        {
            return $http.post('/api/shipments/confirm/complete/operation-review', reviewDTO)
                .then(function(res) {
                    return (operationReviewCompleteShipment = res.data);
                });
        }

        shipment.getOperationReviewCompleteShipment = function()
        {
            return operationReviewCompleteShipment;
        };

        shipment.getSelectedShipment = function()
        {
            return shipment.selectedShipment;
        };

        shipment.setSelectedShipment = function( selectedShipment )
        {
            shipment.selectedShipment = selectedShipment;
        };

    return shipment;
}]);