angular.module('ecommApp')

.controller('ShipmentGenerateOperationReviewController', ['$scope', '$location', '$interval', 'toastr', 'orderService', 'Shop', 'Utils',
    function($scope, $location, $interval, toastr, orderService, Shop, Utils) {

        $scope.operateDate = Date.now();
        $scope.operationReview = orderService.getOperationReview;
        $scope.defaultHeight = function(){
            return {
                height: $(window).height() / 2.3
            };
        };

        function updateCreateTime() {
            $scope.operateDate = new Date();
        }

        var createTime = $interval(updateCreateTime, 500);

        /* 点击将某个订单的 ignoreCheck 标为  ! ignoreCheck，在进行复核验证时不再对该订单进行验证 */
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
                'selectedCourier' : operationReview.selectedCourier,
                'assignWarehouseId' : operationReview.assignWarehouseId
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
                'selectedCourier' : operationReview.selectedCourier,
                'assignWarehouseId' : operationReview.assignWarehouseId
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
                'selectedCourier' : operationReview.selectedCourier,
                'assignWarehouseId' : operationReview.assignWarehouseId
            };
            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){

                if( review.confirmable )
                {
                    /* 如果没有最终订单  */
                    if( review.resultMap.isEmptyFinalOrders )
                    {
                        toastr.warning('抱歉，没有可以生成发货单的订单！');
                    }
                    else
                    {
                        toastr.success('成功开出 ' + review.resultMap.generatedShipmentCount + ' 张发货单。');
                        $scope.$parent.toggleShipmentSheetSlide();

                        orderService.getPagedOrdersForOrderDeploy({
                            page: 0,
                            size: $scope.$parent.pageSize,
                            sort: ['internalCreateTime,desc'],
                            warehouseId: $scope.$parent.query.warehouse ? $scope.$parent.query.warehouse.id : null,
                            shopId: $scope.$parent.query.shop ? $scope.$parent.query.shop.id : null,
                            deleted: false
                        }).then(function(page) {
                            console.log('page:');
                            console.log(page);
                            $.each(page.content, function() {
                                Shop.initShopDefaultTunnel(this.shop);
                                orderService.checkItemProductShopTunnel(this);
                            });
                            $scope.$parent.page = page;
                            $scope.$parent.totalPagesList = Utils.setTotalPagesList(page);
                        });

                        $location.url('/shipment');
                    }
                }

                console.log('After Operation Review:');
                console.log(review);
                console.log('验证是否全部通过 ：' + review.confirmable);
                $interval.cancel(createTime);
            });
        };

    }
]);