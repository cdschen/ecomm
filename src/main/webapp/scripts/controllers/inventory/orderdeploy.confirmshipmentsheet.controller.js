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

        /* 点击将某个订单的 ignoreCheck 标为  ! ignoreCheck，在进行符合验证时不再对该订单进行验证 */
        $scope.ignoreOrNotCheckOrder = function(order)
        {
            order.ignoreCheck = ! order.ignoreCheck;
            var operationReview = orderService.getOperationReview();
            var reviewDTO = {
                'action' : 'VERIFY',
                'orders' : operationReview.orders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap,
                'selectedCourier' : operationReview.selectedCourier
            };
            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

        /* 点击将［取消］或［恢复］某项验证 */
        $scope.ignoreOrNotChecker = function(ignoredMap, checker, isIgnored)
        {
            ignoredMap[checker] = isIgnored;

            var operationReview = orderService.getOperationReview();
            var reviewDTO = {
                'action' : 'VERIFY',
                'orders' : operationReview.orders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap,
                'selectedCourier' : operationReview.selectedCourier
            };
            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

        /* 确认生成发货单，如果再次验证通过则可以生成，否则保持在该页 */
        $scope.confirmShipmentGeneration = function()
        {
            var operationReview = orderService.getOperationReview();
            var reviewDTO = {
                'action' : 'CONFIRM',
                'orders' : operationReview.orders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap,
                'selectedCourier' : operationReview.selectedCourier
            };
            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
                console.log('验证是否全部通过 ：' + review.reviewPass);
            });
        };

    }
]);