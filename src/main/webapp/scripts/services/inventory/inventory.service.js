angular.module('ecommApp')

.factory('Inventory', ['$resource', '$http', function($resource, $http) {

    var $ = angular.element;
    var inventory = $resource('/api/inventories/:id');

    inventory.getAll = function(params) {
        return $http.get('/api/inventories/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    inventory.refresh = function(inventories) {
        var products = [];
        $.each(inventories, function() {
            var inventory = this,
                existInventory = false;
            $.each(products, function() {
                var product = this;
                if (product.sku === inventory.product.sku) {
                    if (inventory.position) {
                        var existPosition = false;
                        $.each(product.positions, function() {
                            var position = this;
                            if (position.id === inventory.position.id) {
                                var existPositionBatch = false;
                                $.each(position.batches, function() {
                                    var batch = this;
                                    if (batch.id === inventory.inventoryBatchId) {
                                        batch.total += inventory.quantity;
                                        existPositionBatch = true;
                                        return false;
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
                                return false;
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
                    }
                    var detail = {
                        position: inventory.position,
                        quantity: inventory.quantity,
                        expireDate: inventory.expireDate,
                        batchId: inventory.inventoryBatchId
                    };
                    product.total += inventory.quantity;
                    var existBatch = false;
                    $.each(product.batches, function() {
                        var batch = this;
                        if (batch.id === inventory.inventoryBatchId) {
                            batch.total += inventory.quantity;
                            existBatch = true;
                            return false;
                        }
                    });
                    if (!existBatch) {
                        product.batches.push({
                            id: inventory.inventoryBatchId,
                            total: inventory.quantity
                        });
                    }
                    product.details.push(detail);
                    existInventory = true;
                    return false;
                }
            });
            if (!existInventory) {
                inventory.product.positions = [];
                if (inventory.position) {
                    inventory.position.total = inventory.quantity;
                    inventory.position.batches = [{
                        id: inventory.inventoryBatchId,
                        total: inventory.quantity
                    }];
                    inventory.product.positions.push(inventory.position);
                    inventory.product.existPosition = true;
                }
                inventory.product.details = [{
                    position: inventory.position,
                    quantity: inventory.quantity,
                    expireDate: inventory.expireDate,
                    batchId: inventory.inventoryBatchId
                }];
                inventory.product.total = inventory.quantity;
                inventory.product.batches = [{
                    id: inventory.inventoryBatchId,
                    total: inventory.quantity
                }];
                products.push(inventory.product);
            }
        });
        return products;
    };

    function calculateSelectedBatch(object, item, action) {
        $.each(object.batches, function() {
            var batch = this;
            if (batch.id === item.outBatch.id) {
                if (action === 'add') {
                    batch.total -= item.changedQuantity;
                    object.total -= item.changedQuantity;
                } else if (action === 'remove') {
                    batch.total += item.changedQuantity;
                    object.total += item.changedQuantity;
                }
                return false;
            }
        });
    }

    function calcualteNoBatchAdd(object, item) {
        var temp = item.changedQuantity;
        object.total -= item.changedQuantity;
        $.each(object.batches, function() {
            var batch = this;
            if (batch.total - temp < 0) {
                batch[item.$field] = batch.total;
                temp = temp - batch.total;
                batch.total = 0;
            } else {
                batch[item.$field] = temp;
                batch.total -= temp;
                return false;
            }
        });
    }

    function calcualteNoBatchRemove(object, item) {
        console.log(object.batches);
        console.log(item);
        object.total += item.changedQuantity;
        $.each(object.batches, function() {
            var batch = this;
            batch.total += batch[item.$field] !== undefined && batch[item.$field];
        });
    }

    function selectedPositionAndBatch(product, position, item, action) {
        // calculate product.total, product.selectedBatch.total
        calculateSelectedBatch(product, item, action);
        // calculate position.total, position.selectedBatch.total
        calculateSelectedBatch(position, item, action);
    }

    function selectedPositionNoBatch(product, position, item, action) {
        if (action === 'add') {
            // calculate product.total, product.batches[x].total
            calcualteNoBatchAdd(product, item);
            // calculate position.total, position.batches[x].total
            calcualteNoBatchAdd(position, item);
        } else if (action === 'remove') {
            // calculate product.total, product.batches[x].total
            calcualteNoBatchRemove(product, item);
            // calculate position.total, position.batches[x].total
            calcualteNoBatchRemove(position, item);
        }
    }

    function noPositionSelectedBatch(product, item, action) {
        // calculate product.total, product.selectedBatch.total
        calculateSelectedBatch(product, item, action);
        // calculate position.total, position.selectedBatch.total
        if (action === 'add') {
            var temp = item.changedQuantity;
            var exitPositionEach = false;
            $.each(product.positions, function() {
                var position = this;
                $.each(position.batches, function() {
                    var batch = this;
                    if (batch.id === item.outBatch.id) {
                        if (batch.total - temp < 0) {
                            batch[item.$field] = batch.total;
                            temp = temp - batch.total;
                            batch.total = 0;
                            position[item.$field] = batch.total;
                            position.total -= batch.total;
                        } else {
                            batch[item.$field] = temp;
                            batch.total -= temp;
                            position[item.$field] = temp;
                            position.total -= temp;
                            exitPositionEach = true;
                        }
                        return false;
                    }
                });
                if (exitPositionEach) {
                    return false;
                }
            });
        } else if (action === 'remove') {
            $.each(product.positions, function() {
                var position = this;
                position.total += position[item.$field] !== undefined && position[item.$field];
                $.each(position.batches, function() {
                    var batch = this;
                    batch.total += batch[item.$field] !== undefined && batch[item.$field];
                });
            });
        }

    }

    function noPositionAndBatch() {}

    inventory.refrechProducts = function(products, item, action) {
        // selected position
        if (item.position) {
            $.each(products, function() {
                var product = this;
                if (product.sku === item.product.sku) {
                    $.each(product.positions, function() {
                        var position = this;
                        if (position.id === item.position.id) {
                            // selected batch
                            if (item.outBatch) {
                                selectedPositionAndBatch(product, position, item, action);
                                // no batch
                            } else {
                                selectedPositionNoBatch(product, position, item, action);
                            }
                            return false;
                        }
                    });
                    return false;
                }
            });
            // no position
        } else {
            // selected batch
            if (item.outBatch) {
                $.each(products, function() {
                    var product = this;
                    if (product.sku === item.product.sku) {
                        noPositionSelectedBatch(product, item, action);
                        return false;
                    }
                });
                // no batch
            } else {

            }
        }
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
