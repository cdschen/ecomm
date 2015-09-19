angular.module('ecommApp')

.controller('ConfirmShipmentSheetController', ['$scope', 'orderService',
    function($scope, orderService) {

        $scope.operateDate = Date.now();
        $scope.operationReview = orderService.getOperationReview;
        $scope.defaultHeight = function(){
            return {
                height: $(window).height() / 2
            };
        };

        /* 点击将某个订单的 ignoreCheck 标为 true，在进行符合验证时不再对该订单进行验证 */
        $scope.ignoreCheckOrderFalse = function(order)
        {
            order.ignoreCheck = false;
            var reviewDTO = {
                'orders' : orderService.getOperationReview().orders
            };
            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };
        $scope.ignoreCheckOrderTrue = function(order)
        {
            order.ignoreCheck = true;
            var reviewDTO = {
                'orders' : orderService.getOperationReview().orders
            };
            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

    }
]);