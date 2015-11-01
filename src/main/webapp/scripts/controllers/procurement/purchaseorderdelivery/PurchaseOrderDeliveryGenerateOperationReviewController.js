angular.module('ecommApp')

.controller('PurchaseOrderDeliveryGenerateOperationReviewController', ['$scope', '$location', '$interval', 'toastr', 'purchaseOrderDeliveryService', 'Utils',
    function($scope, $location, $interval, toastr, purchaseOrderDeliveryService, Utils) {

        var $ = angular.element;
        $scope.operateDate = Date.now();
        $scope.operationReview = purchaseOrderDeliveryService.getOperationReviewCompletePurchaseOrderDelivery;
        $scope.defaultHeight = function(){
            return {
                height: $(window).height() / 2.2
            };
        };

        function updateCreateTime() {
            $scope.operateDate = new Date();
        }

        var createTime = $interval(updateCreateTime, 500);

        /* 点击将某个采购单或采购单详情的 ignoreCheck 标为  ! ignoreCheck，在进行复核验证时不再对该采购单或采购单详情进行验证 */
        $scope.ignoreOrNotCheckPurchaseOrderOrItem = function( orderOrItem, isOrder )
        {
            if( isOrder )
            {
                for( var purchaseOrderItemIndex in orderOrItem.items )
                {
                    orderOrItem.items[purchaseOrderItemIndex].ignoreCheck = ! orderOrItem.ignoreCheck;
                }
            }
            orderOrItem.ignoreCheck = ! orderOrItem.ignoreCheck;
            var operationReview = purchaseOrderDeliveryService.getOperationReviewCompletePurchaseOrderDelivery();
            var reviewDTO = {
                'action' : 'VERIFY',
                'purchaseOrders' : operationReview.purchaseOrders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            purchaseOrderDeliveryService.confirmOperationReviewWhenCompletePurchaseOrderDelivery(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

        $scope.selectedRemainingQtyToCreditOrBackOrderPurchaseOrderItem = {};

        /* ［实际收货数量］输入框失去焦点时调用 */
        $scope.updateReceivedQtyConfirm = function( purchaseOrderItem )
        {
            if( purchaseOrderItem.realReceivedQty < 0 )
            {
                purchaseOrderItem.realReceivedQty = 0;
            }

            /* 如果［实际收货数量］与［待收货数量］不相等 */
            if ( purchaseOrderItem.realReceivedQty !== purchaseOrderItem.pendingQty )
            {
                /* 当用户手动调整成小于“待收货”的数量时， 跳出选择框让用户选择   有N件未到货的商品 “缺货转厂家Credit" 还是“继续等back order到货”。 */
                if ( purchaseOrderItem.realReceivedQty < purchaseOrderItem.pendingQty )
                {
                    $scope.warning = {
                        content: '有 ' + ( purchaseOrderItem.pendingQty - purchaseOrderItem.realReceivedQty ) + ' 件未到货的商品'
                    };
                    $scope.isReceivedQtyGreaterThanPendingQty = false;
                    $scope.selectedRemainingQtyToCreditOrBackOrderPurchaseOrderItem = purchaseOrderItem;
                }
                /* 当用户手动调整成大于”待收货“的数量时，跳出选择框让用户选择， 有N件超量到货的商品， “修改采购数量”， 还是“就这样，我自己处理”。 */
                else
                {
                    $scope.warning = {
                        content: '有 ' + ( purchaseOrderItem.realReceivedQty - purchaseOrderItem.pendingQty ) + ' 件超量到货的商品'
                    };
                    $scope.isReceivedQtyGreaterThanPendingQty = true;
                }
                $('#confirmOperationReviewWhenCompletePurchaseOrderDeliveryWaringModal').modal('show');
            }
            else
            {
                $scope.refreshOperationReview();
            }
        };

        /* ［实际采购单价］输入框失去焦点时调用 */
        $scope.updateRealPurchaseUnitPriceConfirm = function( purchaseOrderItem )
        {
            if( purchaseOrderItem.realPurchaseUnitPrice < 0 )
            {
                purchaseOrderItem.realPurchaseUnitPrice = 0;
            }
            $scope.refreshOperationReview();
        };


        /* 设置收货数量不匹配错误是否［取消验证］ */
        $scope.setIgnoreDifferentReceiveQtyError = function( isIgnored )
        {
            $scope.ignoreOrNotChecker($scope.operationReview().ignoredMap, 'differentReceiveQtyError', isIgnored);
            $('#confirmOperationReviewWhenCompletePurchaseOrderDeliveryWaringModal').modal('hide');
            $scope.refreshOperationReview();
        };

        /* 设置剩余数量至［Credit数量］或者［Back Order数量］ */
        $scope.setRemainingQtyToCreditOrBackOrder = function( isCredit )
        {
            var purchaseOrderItem = $scope.selectedRemainingQtyToCreditOrBackOrderPurchaseOrderItem;
            purchaseOrderItem.creditQty = 0;
            purchaseOrderItem.backOrderQty = 0;
            /* isCredit 为 true 则缺货转厂家Credit */
            if( isCredit )
            {
                purchaseOrderItem.creditQty = purchaseOrderItem.pendingQty - purchaseOrderItem.realReceivedQty;
            }
            /* isCredit 为 false 则继续等back order到货 */
            else
            {
                purchaseOrderItem.backOrderQty = purchaseOrderItem.pendingQty - purchaseOrderItem.realReceivedQty;
            }
            $('#confirmOperationReviewWhenCompletePurchaseOrderDeliveryWaringModal').modal('hide');
            $scope.refreshOperationReview();
        };

        $scope.refreshOperationReview = function()
        {
            var operationReview = purchaseOrderDeliveryService.getOperationReviewCompletePurchaseOrderDelivery();
            var reviewDTO = {
                'action' : 'VERIFY',
                'purchaseOrders' : operationReview.purchaseOrders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            purchaseOrderDeliveryService.confirmOperationReviewWhenCompletePurchaseOrderDelivery(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

        /* 点击将［取消］或［恢复］某项验证 */
        $scope.ignoreOrNotChecker = function(ignoredMap, checker, isIgnored)
        {
            ignoredMap[checker] = isIgnored;

            var operationReview = purchaseOrderDeliveryService.getOperationReviewCompletePurchaseOrderDelivery();
            for( var purchaseOrderItemIndex in operationReview.purchaseOrders.items )
            {
                operationReview.purchaseOrders.items[purchaseOrderItemIndex].ignoredMap[checker] = isIgnored;
            }
            var reviewDTO = {
                'action' : 'VERIFY',
                'purchaseOrders' : operationReview.purchaseOrders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            purchaseOrderDeliveryService.confirmOperationReviewWhenCompletePurchaseOrderDelivery(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

        /* 确认生成收货单，如果再次验证通过则可以生成，否则保持在该页 */
        $scope.confirmPurchaseOrderDeliveryGeneration = function()
        {
            var operationReview = purchaseOrderDeliveryService.getOperationReviewCompletePurchaseOrderDelivery();
            var reviewDTO = {
                'action' : 'CONFIRM',
                'purchaseOrders' : operationReview.purchaseOrders,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            purchaseOrderDeliveryService.confirmOperationReviewWhenCompletePurchaseOrderDelivery(reviewDTO).then(function(review){

                if( review.confirmable )
                {
                    /* 如果没有最终采购单  */
                    if( review.resultMap.isEmptyFinalPurchaseOrders )
                    {
                        toastr.warning('抱歉，没有可以生成收货单的采购单！');
                    }
                    else
                    {
                        if( review.resultMap.generatedPurchaseOrderDeliveryCount > 0 )
                        {
                            toastr.success('成功开出 ' + review.resultMap.generatedPurchaseOrderDeliveryCount + ' 张收货单。');
                        }
                        $scope.$parent.togglePurchaseOrderDeliverySheetSlide();

                        purchaseOrderDeliveryService.get({
                            page: 0,
                            size: $scope.$parent.pageSize,
                            sort: ['receiveTime,desc']
                        }).then(function(page) {
                            console.log('page:');
                            console.log(page);
                            $scope.$parent.page = page;
                            $scope.$parent.totalPagesList = Utils.setTotalPagesList(page);
                        });

                        $location.url('/purchaseorderdeliveries');
                    }
                }
                else
                {
                    toastr.warning('确认之前请确保验证全部通过，或者您可以选择点击［取消验证］之后再确认！');
                }

                console.log('After Operation Review:');
                console.log(review);
                console.log('验证是否全部通过 ：' + review.confirmable);
                $interval.cancel(createTime);
            });
        };

    }
]);