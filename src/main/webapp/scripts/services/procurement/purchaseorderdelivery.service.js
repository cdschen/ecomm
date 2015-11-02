angular.module('ecommApp')

.factory('purchaseOrderDeliveryService', ['$resource', '$http', function($resource, $http) {

    var operationReviewCompletePurchaseOrderDelivery;
    var purchaseOrderDelivery = $resource('/api/purchaseorderdeliveries/:id', {}, {});

    purchaseOrderDelivery.getAll = function(params) {
        return $http.get('/api/purchaseorderdeliveries/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    purchaseOrderDelivery.savePurchaseOrderDeliveries = function(shipment) {
        return $http.post('/api/saveDeliveries', shipment)
            .then(function(res) {
                return res.data;
            });
    };

    /* 批量选中 */
    purchaseOrderDelivery.selectedPurchaseOrderDeliveries = [];
    /* 单个选中 */
    purchaseOrderDelivery.selectedPurchaseOrderDelivery = {};

    purchaseOrderDelivery.confirmOperationReviewWhenCompletePurchaseOrderDelivery = function(reviewDTO) {
        return $http.post('/api/purchaseorderdeliveries/confirm/complete/operation-review', reviewDTO)
            .then(function(res) {
                return (operationReviewCompletePurchaseOrderDelivery = res.data);
            });
    };

    purchaseOrderDelivery.getOperationReviewCompletePurchaseOrderDelivery = function() {
        return operationReviewCompletePurchaseOrderDelivery;
    };

    purchaseOrderDelivery.getSelectedPurchaseOrderDelivery = function() {
        return purchaseOrderDelivery.selectedPurchaseOrderDelivery;
    };

    purchaseOrderDelivery.setSelectedPurchaseOrderDelivery = function(selectedPurchaseOrderDelivery) {
        purchaseOrderDelivery.selectedPurchaseDelivery = selectedPurchaseOrderDelivery;
    };

    return purchaseOrderDelivery;
}]);
