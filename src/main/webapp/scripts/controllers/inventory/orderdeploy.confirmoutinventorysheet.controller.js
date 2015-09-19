angular.module('ecommApp')

.controller('ConfirmOutInventorySheetController', ['$scope', 'orderService',
    function($scope, orderService) {

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
            var reviewDTO = {
                orders: orderService.getOperationReview().orders,
                assginWarehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null
            };
            console.log('reviewDTO:');
            console.log(reviewDTO);
            orderService.confirmOrderWhenGenerateOutInventory(reviewDTO).then(function(review) {
                console.log('review:');
                console.log(review);
            });
        };

        $scope.recover = function(order){
            order.ignoreCheck = false;
            var reviewDTO = {
                orders: orderService.getOperationReview().orders,
                assginWarehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null
            };
            console.log('reviewDTO:');
            console.log(reviewDTO);
            orderService.confirmOrderWhenGenerateOutInventory(reviewDTO).then(function(review) {
                console.log('review:');
                console.log(review);
            });
        }

    }
]);
