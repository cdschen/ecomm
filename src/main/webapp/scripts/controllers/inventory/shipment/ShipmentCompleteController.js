angular.module('ecommApp')

.controller('ShipmentCompleteController', ['$scope', '$location', '$interval', 'toastr', 'shipmentService', 'courierService', 'Utils',
    function($scope, $location, $interval, toastr, shipmentService, courierService, Utils) {

        $scope.operatePickupTime = Date.now();
        $scope.operationReviewCompleteShipment = shipmentService.getOperationReviewCompleteShipment;


        courierService.getAll().then(function(coureirs) {
            $scope.coureirs = coureirs;
        });

        function updatePickupTime() {
            $scope.operatePickupTime = new Date();
        }

        var pickupTime = $interval(updatePickupTime, 500);

        /* 点击将［取消］或［恢复］某项验证 */
        $scope.ignoreOrNotChecker = function(ignoredMap, checker, isIgnored)
        {
            ignoredMap[checker] = isIgnored;

            var operationReviewCompleteShipment = shipmentService.getOperationReviewCompleteShipment();
            var reviewDTO = {
                'action' : 'VERIFY',
                'shipment' : operationReviewCompleteShipment.shipment,
                'orders' : operationReviewCompleteShipment.orders,
                'checkMap' : operationReviewCompleteShipment.checkMap,
                'dataMap' : operationReviewCompleteShipment.dataMap,
                'ignoredMap' : operationReviewCompleteShipment.ignoredMap
            };
            shipmentService.confirmOperationReviewWhenCompleteShipment(reviewDTO).then(function(review){
                console.log('After Operation Review Complete Shipment:');
                console.log(review);
            });
        };

        /* 确认完成发货单，如果再次验证通过则可以生成，否则保持在该页 */
        $scope.confirmShipmentComplete = function()
        {
            var operationReviewCompleteShipment = shipmentService.getOperationReviewCompleteShipment();
            var reviewDTO = {
                'action' : 'CONFIRM',
                'shipment' : operationReviewCompleteShipment.shipment,
                'orders' : operationReviewCompleteShipment.orders,
                'checkMap' : operationReviewCompleteShipment.checkMap,
                'dataMap' : operationReviewCompleteShipment.dataMap,
                'ignoredMap' : operationReviewCompleteShipment.ignoredMap
            };
            shipmentService.confirmOperationReviewWhenCompleteShipment(reviewDTO).then(function(review){

                if( review.confirmable )
                {
                    /* 如果没有最终发货单  */
                    if( review.resultMap.isEmptyFinalShipment )
                    {
                        toastr.warning('抱歉，无法完成发货单操作！');
                    }
                    else
                    {
                        toastr.success('成功完成 ' + review.resultMap.generatedShipmentCount + ' 张发货单。');
                        $scope.$parent.toggleShipmentCompleteSlide();

                        shipmentService.get({
                            page: 0,
                            size: $scope.$parent.pageSize,
                            sort: ['createTime,desc']
                        }, function(page) {
                            console.log('page:');
                            console.log(page);
                            $scope.$parent.page = page;
                            $scope.$parent.totalPagesList = Utils.setTotalPagesList(page);
                        });

                        //$location.url('/shipment');
                    }
                }
                else
                {
                    toastr.warning('确认之前请确保验证全部通过，或者您可以选择点击［取消验证］之后再确认！');
                }

                console.log('After Operation Review Complete Shipment:');
                console.log(review);
                console.log('验证是否全部通过 ：' + review.confirmable);
                $interval.cancel(pickupTime);
            });
        };

        /* 更新操作复核里的发货单的数据 */
        $scope.updateOperationReviewCompleteShipmentData = function()
        {
            var operationReviewCompleteShipment = shipmentService.getOperationReviewCompleteShipment();
            var reviewDTO = {
                'action' : 'VERIFY',
                'shipment' : operationReviewCompleteShipment.shipment,
                'orders' : operationReviewCompleteShipment.orders,
                'checkMap' : operationReviewCompleteShipment.checkMap,
                'dataMap' : operationReviewCompleteShipment.dataMap,
                'ignoredMap' : operationReviewCompleteShipment.ignoredMap
            };
            shipmentService.confirmOperationReviewWhenCompleteShipment(reviewDTO).then(function(review){
                console.log('After Operation Review Complete Shipment:');
                console.log(review);
            });
        };

        /* 更换快递公司，并刷新 */
        $scope.changeCourier = function( selectedCourier )
        {
            var operationReviewCompleteShipment = shipmentService.getOperationReviewCompleteShipment();
            operationReviewCompleteShipment.shipment.courierId = selectedCourier.id;

            $scope.updateOperationReviewCompleteShipmentData();

            console.log( 'operationReviewCompleteShipment.shipment.courierId: ' + operationReviewCompleteShipment.shipment.courierId );
        };

        /* 清除选中快递公司，并刷新 */
        $scope.emptySelectedCourier = function()
        {
            var operationReviewCompleteShipment = shipmentService.getOperationReviewCompleteShipment();
            operationReviewCompleteShipment.shipment.courier = undefined;
            operationReviewCompleteShipment.shipment.courierId = undefined;

            $scope.updateOperationReviewCompleteShipmentData();

            console.log( 'operationReviewCompleteShipment.shipment.courierId: ' + operationReviewCompleteShipment.shipment.courierId );
        };

    }
]);