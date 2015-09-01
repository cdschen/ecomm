'use strict';

angular.module('ecommApp')

.factory('Inventory', ['$resource', '$http', function($resource, $http) {

    var inventory = $resource('/api/inventories/:id');

    inventory.getAll = function() {
        return $http.get('/api/inventories/get/all').then(function(res) {
            return res.data;
        });
    };

    inventory.getAllByWarehouseId = function(warehouseId) {
        return $http.get('/api/inventories/get/all/' + warehouseId).then(function(res) {
            return res.data;
        });
    };

    inventory.refresh = function(inventories) {
        var products = [];
        angular.forEach(inventories, function(inventory) {
            var exist = false;
            angular.forEach(products, function(product) {
                if (product.sku == inventory.product.sku) {
                    if (inventory.position) {
                        var existPosition = false;
                        angular.forEach(product.positions, function(position) {
                            if (position.id == inventory.position.id) {
                                var existPositionBatch = false;
                                angular.forEach(position.batches, function(batch) {
                                    if (batch.id == inventory.inventoryBatchId) {
                                        batch.total += inventory.quantity;
                                        existPositionBatch = true;
                                        return;
                                    }
                                });
                                if (!existPositionBatch) {
                                    position.batches.push({
                                        id: inventory.inventoryBatchId,
                                        total: inventory.quantity
                                    });
                                }
                                position.total += inventory.quantity;
                                existPosition = true;
                                return;
                            }
                        });
                        if (!existPosition) {
                            inventory.position.total = inventory.quantity;
                            inventory.position.batches = [{
                                id: inventory.inventoryBatchId,
                                total: inventory.quantity
                            }];
                            product.positions.push(inventory.position);
                        }
                        product.existPosition = true;
                    } else {
                        var existBatch = false;
                        angular.forEach(product.batches, function(batch) {
                            if (batch.id == inventory.inventoryBatchId) {
                                batch.total += inventory.quantity;
                                existBatch = true;
                                return;
                            }
                        });
                        if (!existBatch) {
                            product.batches.push({
                                id: inventory.inventoryBatchId,
                                total: inventory.quantity
                            });
                        }
                    }
                    var detail = {
                        position: inventory.position,
                        quantity: inventory.quantity,
                        expireDate: inventory.expireDate,
                        batchId: inventory.inventoryBatchId
                    };
                    product.total += inventory.quantity;
                    product.details.push(detail);
                    exist = true;
                    return;
                }
            });

            if (!exist) {
                inventory.product.positions = [];
                if (inventory.position) {
                    inventory.position.total = inventory.quantity;
                    inventory.position.batches = [{
                        id: inventory.inventoryBatchId,
                        total: inventory.quantity
                    }];
                    inventory.product.positions.push(inventory.position);
                    inventory.product.existPosition = true;
                } else {
                    inventory.product.batches = [{
                        id: inventory.inventoryBatchId,
                        total: inventory.quantity
                    }];
                }
                inventory.product.details = [{
                    position: inventory.position,
                    quantity: inventory.quantity,
                    expireDate: inventory.expireDate,
                    batchId: inventory.inventoryBatchId
                }];
                inventory.product.total = inventory.quantity;
                products.push(inventory.product);
            }
        });
        return products;
    };

    var itemHasBatch = function(parent, item, action) {
        for (var i = 0, len = parent.batches.length; i < len; i++) {
            var batch = parent.batches[i];
            if (batch.id == item.outBatch.id) {
                if (action == 'add') {
                    batch.total -= item.changedQuantity;
                    parent.total -= item.changedQuantity;
                } else if (action == 'remove') {
                    batch.total += item.changedQuantity;
                    parent.total += item.changedQuantity;
                }
                break;
            }
        }
        return parent;
    };

    var itemNoBatch = function(parent, item, action) {
        if (action == 'add') {
            parent.total -= item.changedQuantity;
            var temp = item.changedQuantity;
            for (var i = 0, len = parent.batches.length; i < len; i++) {
                var batch = parent.batches[i];
                if (batch.total - temp < 0) {
                	batch[item.$index] = batch.total;
                    temp = temp - batch.total;
                    batch.total = 0;
                } else {
                	batch[item.$index] = temp;
                    batch.total -= temp;
                    break;
                }
            }
        } else if (action == 'remove') {
            parent.total += item.changedQuantity;
            for (var i = 0, len = parent.batches.length; i < len; i++) {
                var batch = parent.batches[i];
                batch.total += batch[item.$index] !== undefined && batch[item.$index];
            }
        }
        return parent;
    };

    var itemHasPosition = function(parent, item, action) {
        for (var i = 0, len = parent.positions.length; i < len; i++) {
            if (parent.positions[i].id == item.position.id) {
                if (item.outBatch) {
                    console.log('item有批次');
                    parent.positions[i] = itemHasBatch(parent.positions[i], item, action);

                } else {
                    console.log('item没批次');
                    parent.positions[i] = itemNoBatch(parent.positions[i], item, action);
                }
                console.log(parent.positions[i]);
                break;
            }
        }
        return parent;
    };

    inventory.refrechProducts = function(products, item, action) {
        for (var i = 0, len = products.length; i < len; i++) {
            if (products[i].sku == item.product.sku) {
                console.log('匹配到商品');
                if (item.position) {
                    console.log('item有库位');
                    products[i] = itemHasPosition(products[i], item, action);
                    console.log(products[i]);
                } else {
                    console.log('item没库位');
                    if (item.outBatch) {
                        console.log('item有批次');
                        products[i] = itemHasBatch(products[i], item, action);
                    } else {
                        console.log('item没批次');
                        products[i] = itemNoBatch(products[i], item, action);
                    }
                }
                break;
            }
        }
        return products;
    };

    return inventory;
}])

.factory('InventoryBatch', ['$resource', '$http', function($resource, $http) {

    var batch = $resource('/api/inventorybatches/:id');

    batch.getAll = function() {
        return $http.get('/api/inventorybatches/get/all').then(function(res) {
            return res.data;
        });
    };

    return batch;
}]);
