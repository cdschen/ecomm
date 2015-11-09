angular.module('ecommApp')

.controller('EnterInventoryOperatorController', ['$scope', '$rootScope', '$state', '$stateParams', 'toastr', 'Warehouse', 'purchaseOrderDeliveryService', 'InventoryBatch',
    function($scope, $rootScope, $state, $stateParams, toastr, Warehouse, purchaseOrderDeliveryService, InventoryBatch) {

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
                console.log(receive)
                $scope.receive = receive;
                $.each(receive.items, function() {
                    this.addedQty = 0;
                });
            });
        }

        $scope.selectAll = function(receive, checkedAll) {
            $.each(receive.items, function() {
                this.checked = checkedAll;
            });
        };

        $scope.defaultAdd = function(item, itemAddForm) {

            item.warehouse = angular.copy($scope.batch.warehouse);

            if (!item.position) {
                item.position = item.warehouse.positions[0];
            }

            $.each($scope.receive.items, function() {
                if (this.checked) {
                    if (this.receiveQty - this.addedQty > 0) {
                        item.product = this.product;
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
                if (this.checked) {
                    if (this.receiveQty - this.addedQty > 0) {
                        item.product = this.product;
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
            $.each($scope.items, function(index){
                if (this === item) {
                    $scope.items.splice(index, 1);
                    return false;
                }
            });
            $.each($scope.receive.items, function() {
                if (this.product.id === item.product.id) {
                    this.addedQty -= item.changedQuantity;
                }
            });
        };

        $scope.enterInventory = function() {

            if ($scope.items.length > 0) {
                $.each($scope.items, function() {
                    var item = this;
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
