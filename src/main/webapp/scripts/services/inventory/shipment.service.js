angular.module('ecommApp')

.factory('shipmentService', ['$resource', '$http', function($resource, $http) {

    var operationReviewCompleteShipment, operationReviewCompleteShipments, operationReviewImportShipments;
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

        shipment.confirmOperationReviewWhenImportShipment = function( reviewDTO )
        {
            return $http.post('/api/shipments/confirm/import/operation-review', reviewDTO )
                .then(function(res) {
                    return (operationReviewImportShipments = res.data);
                });
        };

        /* 批量选中 */
        shipment.selectedShipments = [];
        /* 单个选中 */
        shipment.selectedShipment = {};

        shipment.confirmOperationReviewWhenCompleteShipment = function(reviewDTO)
        {
            return $http.post('/api/shipment/confirm/complete/operation-review', reviewDTO)
                .then(function(res) {
                    return (operationReviewCompleteShipment = res.data);
                });
        };

        shipment.confirmOperationReviewWhenCompleteShipments = function(reviewDTO)
        {
            return $http.post('/api/shipments/confirm/complete/operation-review', reviewDTO)
                .then(function(res) {
                    return (operationReviewCompleteShipments = res.data);
                });
        };

        shipment.getOperationReviewCompleteShipment = function()
        {
            return operationReviewCompleteShipment;
        };

        shipment.getOperationReviewCompleteShipments = function()
        {
            return operationReviewCompleteShipments;
        };

        shipment.getOperationReviewImportShipments = function()
        {
            return operationReviewImportShipments;
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