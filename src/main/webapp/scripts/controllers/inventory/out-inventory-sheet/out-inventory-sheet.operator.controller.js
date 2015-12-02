angular.module('ecommApp')

.controller('OutInventorySheetOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'Inventory', 'InventoryBatch', 'orderService', 'toastr',
    function($rootScope, $scope, $state, $stateParams, Inventory, InventoryBatch, orderService, toastr) {

        var t = $.now();

        $scope.template = {
            details: {
                url: 'views/inventory/out-inventory-sheet/out-inventory-sheet.operator.order-detail-slide.html?' + t
            },
            purchaseDetails: {
                url: 'views/inventory/out-inventory-sheet/out-inventory-sheet.operator.purchase-detail-slide.html?' + t
            }
        };

        $scope.Math = Math;

        $scope.batch = undefined;

        $scope.products = [];

        InventoryBatch.get({
            id: $stateParams.id
        }, function(batch) {
            console.log('InventoryBatch complete:');
            console.log(batch);
            $scope.batch = batch;
            $scope.products = InventoryBatch.refreshBatchItems(batch);
            console.log('products');
            console.log($scope.products);
        });

        $scope.outInventory = function() {

            // 判断一张临时采购性质的出库单是否有关联入库批次

            var noOutBatch = false;
            $.each($scope.batch.items, function() {
                if (!this.outBatchId) {
                    noOutBatch = true;
                    return false;
                }
            });
            if (noOutBatch) {
                toastr.warning('需要临时采购的出库单没有关联入库批次，不得入库。');
                return false;
            }

            $scope.batch.executeOperator = $rootScope.user();
            $scope.batch.type = 2;

            // 复杂的对出库item的实出数量的拆分
            $.each($scope.batch.items, function() {
                var item = this;
                $.each($scope.products, function() {
                    var product = this;
                    if (item.product.id === product.id) {
                        $.each(product.positions, function() {
                            var position = this;
                            if (item.position.id === position.id) {
                                console.log('item, id: ' + item.id + ', product: ' + item.product.name + ', position:' + item.position.name + ', 应出: ' + item.changedQuantity + ', 库位应出: ' + position.total + ', 库位实出: ' + position.actualTotal);

                                if (item.changedQuantity === -position.actualTotal && item.changedQuantity === position.total) {
                                    item.actualQuantity = item.changedQuantity;
                                } else {
                                    if (item.changedQuantity + position.actualTotal >= 0) {
                                        item.actualQuantity = item.changedQuantity;
                                        position.actualTotal += item.changedQuantity;
                                    } else {
                                        item.actualQuantity = -position.actualTotal;
                                        position.actualTotal = 0;
                                    }
                                }
                            }
                        });
                    }
                });

            });

            console.log('-------------------------------------');
            $.each($scope.batch.items, function() {
                var item = this;
                console.log('item, id: ' + item.id + ', product: ' + item.product.name + ', position:' + item.position.name + ', 应出: ' + item.changedQuantity + ', 实出: ' + item.actualQuantity + ', 对应的入库单号: ' + item.outBatchId);
            });

            // console.clear();
            // console.log('outInventory:batch');
            // console.log($scope.batch);

            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log('complete outInventory:');
                console.log(batch);
                $state.go('outInventorySheet');
            });
        };

        /*
         * 订单详情
         */

        $scope.detailsSlideChecked = false;

        $scope.toggleDetailsSlide = function(batch) {
            console.log('toggleDetailsSlide():');
            console.log(batch);
            $scope.detailsSlideChecked = !$scope.detailsSlideChecked;
            $('body').css('overflow', 'auto');
            $('div[ps-open="detailsSlideChecked"]').css('overflow', 'hidden');
            if ($scope.detailsSlideChecked) {
                $('body').css('overflow', 'hidden');
                $('div[ps-open="detailsSlideChecked"]').css('overflow', 'auto');
                var orderIds = [];
                $.each(batch.orderBatches, function() {
                    orderIds.push(this.orderId);
                });

                orderService.getAll({
                    orderIds: orderIds
                }).then(function(orders) {
                    $scope.orders = orders;
                    console.log(orders);
                });
            }
        };

        /*
         *   临时采购
         */

        $scope.purchaseDetailsSlideChecked = false;

        $scope.togglePurchaseDetailsSlide = function() {
            console.log('togglePurchaseDetailsSlide():');
            $scope.purchaseDetailsSlideChecked = !$scope.purchaseDetailsSlideChecked;
            $('body').css('overflow', 'auto');
            $('div[ps-open="purchaseDetailsSlideChecked"]').css('overflow', 'hidden');
            if ($scope.purchaseDetailsSlideChecked) {
                $.each($scope.products, function() {
                    var product = this;
                    $.each(product.positions, function() {
                        product.purchaseQty = Math.abs(this.total);
                    });
                });
                $('body').css('overflow', 'hidden');
                $('div[ps-open="purchaseDetailsSlideChecked"]').css('overflow', 'auto');
            }
        };

        $scope.doPurchase = function() {
            console.log('doPurchase()');
            console.log($scope.products);
            $('body').css('overflow', 'auto');
            $('div[ps-open="purchaseDetailsSlideChecked"]').css('overflow', 'hidden');
            var purchasedProducts = '';
            $.each($scope.products, function() {
                // var purchasedProduct = {
                //     id: this.id,
                //     sku: this.sku,
                //     barcode: this.sku,
                //     name: this.name,
                //     purchaseQty: this.purchaseQty
                // };
                // purchasedProducts.push(purchasedProduct);
                purchasedProducts += this.sku + ',' + this.purchaseQty + ';';
            });
            console.log('purchasedProducts:');
            console.log(purchasedProducts);



            $state.go('purchaseOrder.operator', {
                purchasedProducts: purchasedProducts
            });//purchasedProducts: purchasedProducts
        };

    }
]);
