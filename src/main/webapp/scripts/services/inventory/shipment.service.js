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

        shipment.changeShipmentStatus = function( shipment )
        {
            return $http.post('/api/shipment/change-status', shipment )
                .then(function(res)
                {
                    return res.data;
                });
        };

        shipment.changeShipmentsStatus = function( shipment )
        {
            return $http.post('/api/shipments/change-status', shipment )
                .then(function(res)
                {
                    return res.data;
                });
        };

        shipment.saveShipmentsChanges = function( shipment )
        {
            return $http.post('/api/shipments/save-changes', shipment )
                .then(function(res)
                {
                    return res.data;
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


        /** 发货快递单，供拉出层打印用
         */
        shipment.shipmentCouriers = [];

        shipment.getShipmentCouriers = function()
        {
            return shipment.shipmentCouriers;
        };

        shipment.setShipmentCouriers = function( shipmentCouriers )
        {
            shipment.shipmentCouriers = shipmentCouriers;
        };

    return shipment;
}]);