angular.module('ecommApp')

.factory('InventoryBatch', ['$resource', '$http', function($resource, $http) {

    var batch = $resource('/api/inventory-batches/:id');

    batch.getAll = function(params) {
        return $http.get('/api/inventory-batches/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    batch.saveList = function(params) {
        return $http.post('/api/inventory-batches/save/list', params).then(function(res) {
            return res.data;
        });
    };

    batch.trash = function(params) {
        return $http.post('/api/inventory-batches/trash', params).then(function(res) {
            return res.data;
        });
    };

    batch.refreshBatchItems = function(batch) {
        var products = [],
            batchItems = batch.items;
        $.each(batchItems, function() {
            var batchItem = this,
                existProduct = false;
            batch.total += batchItem.changedQuantity;
            $.each(products, function() {
                var product = this;
                if (product.id === batchItem.product.id) {
                	existProduct = true;
                    var existPosition = false;
                    $.each(product.positions, function() {
                        var position = this;
                        if (position.id === batchItem.position.id) {
                            position.total += batchItem.changedQuantity;
                            position.actualTotal += Math.abs(batchItem.changedQuantity);
                            existPosition = true;
                            var existPositionOutBatch = false;
                            $.each(position.outBatches, function() {
                                var outBatch = this;
                                if (outBatch.id === batchItem.outBatchId) {
                                    outBatch.total += batchItem.changedQuantity;
                                    outBatch.actualTotal += Math.abs(batchItem.changedQuantity);
                                    existPositionOutBatch = true;
                                    return false;
                                }
                            });
                            if (!existPositionOutBatch) {
                                position.outBatches.push({
                                    id: batchItem.outBatchId,
                                    total: batchItem.changedQuantity,
                                    actualTotal: Math.abs(batchItem.changedQuantity)
                                });
                            }
                            return false;
                        }
                    });
                    if (!existPosition) {
                        batchItem.position.total = batchItem.changedQuantity;
                        batchItem.position.actualTotal = Math.abs(batchItem.changedQuantity);
                        product.positions.push(batchItem.position);
                        batchItem.position.outBatches = [{
                            id: batchItem.outBatchId,
                            total: batchItem.changedQuantity,
                            actualTotal: Math.abs(batchItem.changedQuantity)
                        }];
                    }
                    product.total += batchItem.changedQuantity;
                    product.actualTotal += Math.abs(batchItem.changedQuantity);
                    var existProductOutBatch = false;
                    $.each(product.outBatches, function() {
                        var outBatch = this;
                        if (outBatch.id === batchItem.outBatchId) {
                            outBatch.total += batchItem.changedQuantity;
                            outBatch.actualTotal += Math.abs(batchItem.changedQuantity);
                            existProductOutBatch = true;
                            return false;
                        }
                    });
                    if (!existProductOutBatch) {
                        product.outBatches.push({
                            id: batchItem.outBatchId,
                            total: batchItem.changedQuantity,
                            actualTotal: Math.abs(batchItem.changedQuantity)
                        });
                    }
                    return false;
                }
            });
            if (!existProduct) {
                batchItem.position.total = batchItem.changedQuantity;
                batchItem.position.actualTotal = Math.abs(batchItem.changedQuantity);
                batchItem.position.outBatches = [{
                    id: batchItem.outBatchId,
                    total: batchItem.changedQuantity,
                    actualTotal: Math.abs(batchItem.changedQuantity)
                }];
                batchItem.product.positions.push(batchItem.position);
                batchItem.product.total = batchItem.changedQuantity;
                batchItem.product.actualTotal = Math.abs(batchItem.changedQuantity);
                batchItem.product.outBatches = [{
                    id: batchItem.outBatchId,
                    total: batchItem.changedQuantity,
                    actualTotal: Math.abs(batchItem.changedQuantity)
                }];
                products.push(batchItem.product);
            }
        });
        return products;
    };

    return batch;

}]);
