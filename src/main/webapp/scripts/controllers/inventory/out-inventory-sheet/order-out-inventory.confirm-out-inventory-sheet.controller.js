angular.module('ecommApp')

.controller('ConfirmOutInventorySheetController', ['$rootScope', '$scope', '$state', 'toastr', 'orderService', 'Inventory',
    function($rootScope, $scope, $state, toastr, orderService, Inventory) {

        var $ = angular.element;
        $scope.operateDate = Date.now();
        $scope.operationReview = orderService.getOperationReview;
        $scope.defaultHeight = function() {
            return {
                height: $(window).height() / 2
            };
        };

        $scope.moveOut = function(order) {
            order.ignoreCheck = true;
            console.log('orderService.getOperationReview():');
            console.log(orderService.getOperationReview());
            orderService.confirmOrderWhenGenerateOutInventory(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
            });
        };

        $scope.recover = function(order) {
            order.ignoreCheck = false;
            console.log('orderService.getOperationReview():');
            console.log(orderService.getOperationReview());
            orderService.confirmOrderWhenGenerateOutInventory(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
            });
        };

        $scope.ignoredName = '';

        $scope.cancelConfirm = function(name) {
            $scope.ignoredName = name;
            $('#confirmOutInventorySheetWaringModal').modal('show');
            if (name === 'productInventoryNotEnough') {
                $scope.warning = {
                    content: '你可以取消此项验证，则在生成出库单时将不再验证，这样的出库单可能无法正常出库'
                };
            } else if (name === 'orderExistOutInventorySheet') {
                $scope.warning = {
                    content: '你可以取消此项验证, 则本次生成的出库单可能会重复出库某些商品，你确定要继续?'
                };
            }
        };

        $scope.doCancelConfirm = function() {
            orderService.getOperationReview().ignoredMap[$scope.ignoredName] = true;
            console.log('doCancelConfirm');
            console.log(orderService.getOperationReview());
            orderService.confirmOrderWhenGenerateOutInventory(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
                $('#confirmOutInventorySheetWaringModal').modal('hide');
            });
        };

        $scope.recoverConfirm = function(name) {
            orderService.getOperationReview().ignoredMap[name] = false;
            console.log('cancelConfirm');
            console.log(orderService.getOperationReview());
            orderService.confirmOrderWhenGenerateOutInventory(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
            });
        };

        $scope.createOutInventorySheet = function() {
            console.clear();
            console.log('createOutInventorySheet');
            orderService.getOperationReview().dataMap.userId = $rootScope.user().id;
            Inventory.createOutInventorySheet(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
                orderService.setOperationReview(review);

                if (review.checkMap.differentWarehouseError === true || (review.checkMap.productInventoryNotEnoughError === true && !review.ignoredMap.productInventoryNotEnough) || (review.checkMap.orderExistOutInventorySheetError === true && !review.ignoredMap.orderExistOutInventorySheet)) {

                    if (review.checkMap.differentWarehouseError === true) {
                        toastr.error('必须将"异常"订单从出库订单中删除才能继续');
                    }
                    if (review.checkMap.productInventoryNotEnoughError === true && !review.ignoredMap.productInventoryNotEnough) {
                        toastr.warning('将一些订单从出库订单中移出，保证全部有货，才可继续');
                    }
                    if (review.checkMap.orderExistOutInventorySheetError === true && !review.ignoredMap.orderExistOutInventorySheet) {
                        toastr.warning('以下订单商品已经存在有出库单，可以从列表中移出后继续');
                    }
                } else {
                    $state.go('outInventorySheet');
                }
            });
        };

    }
]);
