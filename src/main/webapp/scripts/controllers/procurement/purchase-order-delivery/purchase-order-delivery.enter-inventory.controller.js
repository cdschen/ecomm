angular.module('ecommApp')

.controller('EnterInventoryOperatorController', ['$scope', '$rootScope', '$state', '$stateParams', 'toastr', 'Warehouse', 'purchaseOrderDeliveryService', 'InventoryBatch', '$filter',
    function($scope, $rootScope, $state, $stateParams, toastr, Warehouse, purchaseOrderDeliveryService, InventoryBatch, $filter) {

        $scope.defaultBatch = {
            operate: 1,
            operateTime: null,
            memo: '',
            warehouse: null,
            user: $rootScope.user(),
            executeOperator: null,
            receiveId: null,
            items: []
        };

        $scope.defaultItem = {
            product: null,
            warehouse: null,
            position: null,
            user: $rootScope.user(),
            executeOperator: null,
            changedQuantity: 1,
            expireDate: null,
            outBatch: null
        };

        $scope.batch = angular.copy($scope.defaultBatch);
        $scope.item = angular.copy($scope.defaultItem);
        $scope.batches = [];
        $scope.items = [];

        $('#sandbox-container input').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            todayHighlight: true,
            autoclose: true
        });

        Warehouse.getAll({
            enabled: true
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        });

        if ($stateParams.id && $stateParams.id !== '') {
            purchaseOrderDeliveryService.get({
                id: $stateParams.id
            }, {}, function(receive) {
                console.log(receive);
                $scope.receive = receive;
                $.each(receive.batches, function() {
                    var batch = this;
                    $.each(batch.items, function() {
                        console.log(this.changedQuantity);
                        if (this.changedQuantity) {
                            receive.enteredQty += this.changedQuantity;
                        }
                    });
                });
                $.each(receive.items, function() {
                    var item = this;
                    item.addedQty = 0;
                    if (item.supplierProduct.product) {
                        $.each(receive.batches, function() {
                            var batch = this;
                            $.each(batch.items, function() {
                                if (this.product.id === item.supplierProduct.product.id) {
                                    item.receiveQty -= this.changedQuantity;
                                }
                            });
                        });
                    }

                });
            });
        }

        $scope.selectAll = function(receive, checkedAll) {
            $.each(receive.items, function() {
                if (this.supplierProduct.product) {
                    this.checked = checkedAll;
                }
            });
        };

        $scope.defaultAdd = function(item, itemAddForm) {

            item.warehouse = angular.copy($scope.batch.warehouse);

            if (!item.position) {
                item.position = item.warehouse.positions[0];
            }

            $.each($scope.receive.items, function() {
                console.log(this.supplierProduct.supplierProductName + ':' + this.checked);
                if (this.checked) {
                    if (this.receiveQty - this.addedQty > 0) {
                        item.product = this.supplierProduct.product;
                        item.expireDate = $filter('date')(this.expireDate, 'yyyy-MM-dd');
                        item.changedQuantity = this.receiveQty - this.addedQty;
                        $scope.items.push(angular.copy(item));
                        this.addedQty += this.receiveQty - this.addedQty;
                    }

                }
            });

            $scope.batch = angular.copy($scope.defaultBatch);
            $scope.item = angular.copy($scope.defaultItem);
            itemAddForm.$setPristine();

        };

        $scope.saveItem = function(item, itemAddForm) {

            item.warehouse = angular.copy($scope.batch.warehouse);

            if (!item.position) {
                item.position = item.warehouse.positions[0];
            }

            $.each($scope.receive.items, function() {
                console.log(this.supplierProduct.supplierProductName + ':' + this.checked);
                if (this.checked) {
                    if (this.receiveQty - this.addedQty > 0) {
                        item.product = this.supplierProduct.product;
                        if (this.receiveQty - this.addedQty - item.changedQuantity < 0) {
                            item.changedQuantity = this.receiveQty - this.addedQty;
                        }
                        $scope.items.push(angular.copy(item));
                        this.addedQty += item.changedQuantity;
                    }

                }
            });

            $scope.batch = angular.copy($scope.defaultBatch);
            $scope.item = angular.copy($scope.defaultItem);
            itemAddForm.$setPristine();

        };

        $scope.removeItem = function(item) {
            $.each($scope.items, function(index) {
                if (this === item) {
                    $scope.items.splice(index, 1);
                    return false;
                }
            });
            $.each($scope.receive.items, function() {
                if (this.supplierProduct.product && this.supplierProduct.product.id === item.product.id) {
                    this.addedQty -= item.changedQuantity;
                }
            });
        };

        $scope.enterInventory = function() {

            if ($scope.items.length > 0) {
                $.each($scope.items, function() {
                    var item = this;
                    if (!item.position) {
                        item.position = item.warehouse.positions[0];
                    }
                    var existItem = false;
                    $.each($scope.batches, function() {
                        var batch = this;
                        if (batch.warehouse.id === item.warehouse.id) {
                            batch.items.push(item);
                            existItem = true;
                            return false;
                        }
                    });
                    if (!existItem) {
                        $scope.batch = angular.copy($scope.defaultBatch);
                        $scope.batch.receiveId = $scope.receive.id;
                        $scope.batch.purchaseOrderId = $scope.receive.purchaseOrderId;
                        $scope.batch.warehouse = angular.copy(item.warehouse);
                        $scope.batch.items.push(item);

                        $scope.batch.purchaseBatchId = $scope.purchaseBatchId;
                        $scope.batches.push(angular.copy($scope.batch));
                    }
                });

                console.log($scope.batches);

                InventoryBatch.saveList($scope.batches).then(function() {
                    $state.go('purchaseOrderDelivery');
                });
            } else {
                toastr.warning('请添加要入库的商品!');
            }
        };
    }
]);
