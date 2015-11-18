angular.module('ecommApp')

.factory('Inventory', ['$resource', '$http', function($resource, $http) {

    var inventory = $resource('/api/inventories/:id');

    inventory.getAll = function(params) {
        return $http.get('/api/inventories/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    inventory.createOutInventorySheet = function(reviewDTO) {
        return $http.post('/api/inventories/outinventorysheet/create', reviewDTO)
            .then(function(res) {
                return res.data;
            });
    };

    inventory.refreshByWarehouse = function(warehouses, inventories) {
        var inventory = {};
        $.each(warehouses, function() {
            var warehouse = this;
            warehouse.inventories = [];
            inventory[warehouse.id] = warehouse;
            $.each(inventories, function() {
                if (warehouse.id === this.warehouseId) {
                    warehouse.inventories.push(this);
                }
            });

        });
        return inventory;
    };

    function printProducts(products) {
        console.log('==============================');
        $.each(products, function() {
            var product = this;
            var str = 'Product: (' + product.name + ', ' + product.total + ')';
            console.log(str);
            if (product.positions) {
                str = 'Product Positions: ';
                $.each(product.positions, function() {
                    var position = this;
                    str += '(' + position.name + ', ' + position.total + ')';
                    str += '[';
                    if (position.batches) {
                        $.each(position.batches, function() {
                            var batch = this;
                            str += '(' + batch.id + ', ' + batch.total + ')';
                        });
                    }
                    str += ']';
                });
                console.log(str);
            }
            str = 'Product Batches: ';
            if (product.batches) {
                $.each(product.batches, function() {
                    var batch = this;
                    str += '(' + batch.id + ', ' + batch.total + ')';
                });
            }
            console.log(str);
        });
        console.log('==============================');
    }

    inventory.refresh = function(inventories) {
        console.log('inventory.refresh():');
        console.log(inventories);
        var products = [];
        $.each(inventories, function() {
            var inventory = this,
                existProduct = false;
            $.each(products, function() {
                var product = this;
                if (product.id === inventory.product.id) {
                    product.existPosition = true;
                    var existPosition = false;
                    $.each(product.positions, function() {
                        var position = this;
                        if (position.id === inventory.position.id) {
                            existPosition = true;
                            var existPositionBatch = false;
                            $.each(position.batches, function() {
                                var batch = this;
                                if (batch.id === inventory.inventoryBatchId) {
                                    existPositionBatch = true;
                                    batch.total += inventory.quantity;
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
                    
                    var detail = {
                        position: inventory.position,
                        quantity: inventory.quantity,
                        expireDate: inventory.expireDate,
                        batchId: inventory.inventoryBatchId,
                        purchaseOrderId: inventory.batch.purchaseOrderId,
                        receiveId: inventory.batch.receiveId
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
                    existProduct = true;
                    return false;
                }
            });
            if (!existProduct) {
                inventory.position.total = inventory.quantity;
                inventory.position.batches = [{
                    id: inventory.inventoryBatchId,
                    total: inventory.quantity
                }];
                inventory.product.positions.push(inventory.position);
                inventory.product.existPosition = true;
                inventory.product.details = [{
                    position: inventory.position,
                    quantity: inventory.quantity,
                    expireDate: inventory.expireDate,
                    batchId: inventory.inventoryBatchId,
                    purchaseOrderId: inventory.batch.purchaseOrderId,
                    receiveId: inventory.batch.receiveId
                }];
                inventory.product.total = inventory.quantity;
                inventory.product.batches = [{
                    id: inventory.inventoryBatchId,
                    total: inventory.quantity
                }];
                products.push(inventory.product);
            }
        });
        printProducts(products);
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
        object.total += item.changedQuantity;
        $.each(object.batches, function() {
            var batch = this;
            batch.total += batch[item.$field] !== undefined && batch[item.$field];
        });
    }

    function calculateNoPositionAndBatchRemove(object, item) {
        $.each(object.positions, function() {
            var position = this;
            position.total += position[item.$field] !== undefined && position[item.$field];
            $.each(position.batches, function() {
                var batch = this;
                batch.total += batch[item.$field] !== undefined && batch[item.$field];
            });
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
                            position[item.$field] = batch.total;
                            position.total -= batch.total;
                            batch[item.$field] = batch.total;
                            temp = temp - batch.total;
                            batch.total = 0;
                        } else {
                            position[item.$field] = temp;
                            position.total -= temp;
                            batch[item.$field] = temp;
                            batch.total -= temp;
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
            calculateNoPositionAndBatchRemove(product, item);
        }
    }

    function noPositionAndBatch(product, item, action) {
        if (action === 'add') {
            // calculate product.total, product.batches[x].total
            calcualteNoBatchAdd(product, item);
            // calculate position.total, position.batches[x].total
            var temp = item.changedQuantity;
            var done = false;
            $.each(product.batches, function() {
                var outBatch = this;
                var exitPositionEach = false;
                console.log('outBatch:' + outBatch.id);
                $.each(product.positions, function() {
                    var position = this;
                    console.log('position:' + position.name + ', batches:');
                    console.log(position.batches);
                    $.each(position.batches, function() {
                        var batch = this;
                        if (batch.id === outBatch.id) {
                            if (batch.total - temp < 0) {
                                position[item.$field] = batch.total;
                                position.total -= batch.total;
                                batch[item.$field] = batch.total;
                                temp = temp - batch.total;
                                batch.total = 0;
                            } else {
                                batch[item.$field] = temp;
                                batch.total -= temp;
                                position[item.$field] = temp;
                                position.total -= temp;
                                exitPositionEach = true;
                                done = true;
                            }
                            return false;
                        }
                    });
                    if (exitPositionEach) {
                        return false;
                    }
                });
                if (done) {
                    return false;
                }
            });
        } else if (action === 'remove') {
            // calculate product.total, product.batches[x].total
            calcualteNoBatchRemove(product, item);
            // calculate position.total, position.batches[x].total
            calculateNoPositionAndBatchRemove(product, item);
        }
    }

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
                                console.log('selectedPositionAndBatch:');
                                selectedPositionAndBatch(product, position, item, action);
                                // no batch
                            } else {
                                console.log('selectedPositionNoBatch:');
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
                    if (product.id === item.product.id) {
                        console.log('noPositionSelectedBatch:');
                        noPositionSelectedBatch(product, item, action);
                        return false;
                    }
                });
                // no batch
            } else {
                $.each(products, function() {
                    var product = this;
                    if (product.id === item.product.id) {
                        console.log('noPositionAndBatch:');
                        noPositionAndBatch(product, item, action);
                        return false;
                    }
                });
            }
        }
    };

    return inventory;
}])

.factory('InventoryBatchItem', ['$resource', '$http', function($resource, $http) {

    //var $ = angular.element;
    var batchItem = $resource('/api/inventory-batch-items/:id');

    batchItem.getAll = function(params) {
        return $http.get('/api/inventory-batch-items/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    return batchItem;
}]);
