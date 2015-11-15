
angular.module('ecommApp')

.factory('purchaseOrderService', ['$resource', '$http', function($resource, $http) {

    //var operationReviewCompletePurchaseOrderDelivery;
    var purchaseOrder = $resource('/api/purchaseorders/:id', {}, {});

        purchaseOrder.getAll = function(params)
        {
            return $http.get('/api/purchaseorders/get/all', {
                params: params
            }).then(function(res) {
                return res.data;
            });
        };

        purchaseOrder.savePurchaseOrders = function( PurchaseOrder )
        {
            return $http.post('/api/savePurchaseOrders', PurchaseOrder )
                .then(function(res) {
                    return res.data;
                });
        };

        /* 批量选中 */
        purchaseOrder.selectedPurchaseOrders = [];
        /* 单个选中 */
        purchaseOrder.selectedPurchaseOrder = {};

        purchaseOrder.getSelectedPurchaseOrder = function()
        {
            return purchaseOrder.selectedPurchaseOrder;
        };

        purchaseOrder.setSelectedPurchaseOrder = function( selectedPurchase )
        {
            purchaseOrder.selectedPurchase = selectedPurchase;
        };

    return purchaseOrder;
}]);