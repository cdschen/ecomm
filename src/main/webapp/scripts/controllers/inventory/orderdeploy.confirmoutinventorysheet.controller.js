angular.module('ecommApp')

.controller('ConfirmOutInventorySheetController', ['$rootScope', '$scope', 'orderService', 'Inventory',
    function($rootScope, $scope, orderService, Inventory) {

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

        $scope.cancelConfirm = function(name) {
            orderService.getOperationReview().ignoredMap[name] = true;
            console.log('cancelConfirm');
            console.log(orderService.getOperationReview());
            orderService.confirmOrderWhenGenerateOutInventory(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
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
        }

        $scope.createOutInventorySheet = function() {
            console.clear();
            console.log('createOutInventorySheet');
            orderService.getOperationReview().dataMap.userId = $rootScope.user().id;
            Inventory.createOutInventorySheet(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
                orderService.setOperationReview(review);
            })
        };

    }
]);
