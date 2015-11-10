angular.module('ecommApp')

.controller('PurchaseOrderDeliveryGenerateController', ['$scope', '$rootScope', 'toastr', '$modal', 'filterFilter', 'Warehouse', 'Utils', 'purchaseOrderService', 'purchaseOrderDeliveryService',
    function($scope, $rootScope, toastr, $modal, filterFilter, Warehouse, Utils, purchaseOrderService, purchaseOrderDeliveryService) {

        var t = $.now();

        $scope.template = {
            purchaseOrderDeliveryGenerateOperationReview: {
                url: 'views/procurement/purchase-order-delivery/purchase-order-delivery-generate-operation-review.html?' + t
            }
        };

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultQuery = {
            warehouse: undefined
        };
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.warehouses = [];

        $scope.generatePurchaseOrderDeliverySheetCheckListSlideChecked = false;

        /* 查询采购单分页数据所需查询参数 */
        function getQueryParamJSON()
        {
            return {
                page: 0,
                size: $scope.pageSize,
                sort: ['createTime,desc'],
                queryWarehouseId: $scope.query.queryWarehouse ? $scope.query.queryWarehouse.id : null,
                queryPurchaseOrderId: $scope.query.queryPurchaseOrderId ? $scope.query.queryPurchaseOrderId : null,
                queryCreatorId: $scope.query.queryCreator ? $scope.query.queryCreator.id : null,
                querySupplierId: $scope.query.querySupplier ? $scope.query.querySupplier.id : null,
                //receiveStatus: $scope.query.receiveStatus ? $scope.query.receiveStatus : null,
                //paymentStatus: $scope.query.paymentStatus ? $scope.query.paymentStatus : null,
                queryCreateTimeStart: $scope.query.queryCreateTimeStart ? $scope.query.queryCreateTimeStart : null,
                queryCreateTimeEnd: $scope.query.queryCreateTimeEnd ? $scope.query.queryCreateTimeEnd : null
            };
        }

        Warehouse.getAll({ // 导入所有仓库
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() {
            purchaseOrderService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        });

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.query);
            purchaseOrderService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.query = angular.copy($scope.defaultQuery);
            $scope.selectAllShops($scope.shops);
            console.log($scope.query);
            purchaseOrderService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };


        $scope.togglePurchaseOrderDeliverySheetSlide = function(){
            $scope.generatePurchaseOrderDeliverySheetCheckListSlideChecked = !$scope.generatePurchaseOrderDeliverySheetCheckListSlideChecked;
        };

        // selected orders
        $scope.isCheckedAll = false;
        $scope.batchManipulationValue = 'batchManipulation';

        $scope.checkAllPurchaseOrders = function()
        {
            for( var purchaseOrder in $scope.page.content )
            {
                $scope.page.content[purchaseOrder].isSelected = $scope.isCheckedAll;
            }
        };

        /* 生成多个采购单的收货单  */
        $scope.batchManipulation = function()
        {
            var purchaseOrders = $scope.page.content;
            purchaseOrderService.selectedPurchaseOrders.length = 0;
            $.each(purchaseOrders, function(){
                var purchaseOrder = this;
                if (purchaseOrder.isSelected) {
                    purchaseOrderService.selectedPurchaseOrders.push(angular.copy(purchaseOrder));
                }
            });
            if (purchaseOrderService.selectedPurchaseOrders.length > 0)
            {
                if($scope.batchManipulationValue === 'generatePurchaseOrderDelivery')
                {
                    //$('#generatePurchaseOrderDelivery').modal('show');
                    $scope.checkGeneratePurchaseOrderDeliverySlide();
                }
            }
            else
            {
                toastr.error('请选择一到多个采购单来继续！');
            }


            $scope.batchManipulationValue = 'batchManipulation';
        };


        function executePurchaseOrderDeliveryOperationReview(action)
        {
            var reviewDTO = {
                action                    : action,
                purchaseOrders            : purchaseOrderService.selectedPurchaseOrders,
                dataMap : {
                    receiveUserId            : $rootScope.user().id
                },
                ignoredMap : {
                    emptyPurchaseUnitPriceError : false,
                    emptyReceiveQtyError : false,
                    differentReceiveQtyError : false,
                    isStatusObsoleteError : false
                }
            };
            console.log('Before Operation Review:');
            console.log(reviewDTO);
            purchaseOrderDeliveryService.confirmOperationReviewWhenCompletePurchaseOrderDelivery(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        }

        /* 第一次进入生成收货单操作复核 */
        $scope.checkGeneratePurchaseOrderDeliverySlide = function()
        {
            /* 隐藏指定快递公司弹出层 */
            //$('#generatePurchaseOrderDelivery').modal('hide');

            executePurchaseOrderDeliveryOperationReview('VERIFY');

            $scope.togglePurchaseOrderDeliverySheetSlide();
            /* 查看检查结果 */
        };
    


    }
]);
