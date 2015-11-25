angular.module('ecommApp')

.controller('OutInventorySheetOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'Inventory', 'InventoryBatch', 'orderService',
    function($rootScope, $scope, $state, $stateParams, Inventory, InventoryBatch, orderService) {

        var t = $.now();

        $scope.template = {
            details: {
                url: 'views/inventory/out-inventory-sheet/out-inventory-sheet.operator.order-detail-slide.html?' + t
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
                $.each(batch.orderBatches, function(){
                    orderIds.push(this.orderId);
                });
                orderService.getAll({
                    orderIds: orderIds
                }).then(function(orders){
                    $scope.orders = orders;
                    console.log(orders);
                });
            }
        };

    }
]);
