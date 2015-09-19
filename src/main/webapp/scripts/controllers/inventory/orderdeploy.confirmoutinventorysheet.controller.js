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

        $scope.recoverConfirm = function (name){
            orderService.getOperationReview().ignoredMap[name] = false;
            console.log('cancelConfirm');
            console.log(orderService.getOperationReview());
            orderService.confirmOrderWhenGenerateOutInventory(orderService.getOperationReview()).then(function(review) {
                console.log('review:');
                console.log(review);
            });
        }

        // $scope.existIgnoreItem = function(name) {
        //     var exist = false;
        //     if (orderService.getOperationReview() && orderService.getOperationReview().ignoredCheckers) {
        //         $.each(orderService.getOperationReview().ignoredCheckers, function() {
        //             var itemName = this;
        //             if (itemName === name) {
        //                 exist = true;
        //                 return false;
        //             }
        //         });
        //     }

        //     return exist;
        // };

    }
]);
