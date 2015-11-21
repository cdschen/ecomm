angular.module('ecommApp')

.controller('PurchaseOrderDeliveryController', ['$scope', '$rootScope', 'toastr', 'Warehouse', 'Supplier', 'User', 'Utils', 'purchaseOrderDeliveryService',
    function($scope, $rootScope, toastr, Warehouse, Supplier, User, Utils, purchaseOrderDeliveryService) {

        var t = $.now();

        $scope.template = {
            enterInventorySheet: {
                url: 'views/procurement/purchase-order-delivery/purchase-order-delivery.enter-inventory.html?' + t
            }
        };

        /* Activate Date Picker */
        $('input[ng-model="query.queryReceiveTimeStart"], input[ng-model="query.queryReceiveTimeEnd"]').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'bottom left',
            todayHighlight: true,
            autoclose: true
        });

        $scope.selectedPurchaseOrder = {};

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultQuery = {};
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.warehouses = [];
        $scope.suppliers = [];
        $scope.users = [];
        $scope.purchaseOrderDeliverys = [];

        //$scope.shipmentCompleteSlideChecked = false;

        /* 查询收货单分页数据所需查询参数 */
        function getQueryParamJSON() {
            return {
                page: 0,
                size: $scope.pageSize,
                sort: ['receiveTime,desc'],
                queryWarehouseId: $scope.query.queryWarehouse ? $scope.query.queryWarehouse.id : null,
                queryPurchaseOrderId: $scope.query.queryPurchaseOrderId ? $scope.query.queryPurchaseOrderId : null,
                queryPurchaseOrderDeliveryId: $scope.query.queryPurchaseOrderDeliveryId ? $scope.query.queryPurchaseOrderDeliveryId : null,
                queryReceiveTimeStart: $scope.query.queryReceiveTimeStart ? $scope.query.queryReceiveTimeStart : null,
                queryReceiveTimeEnd: $scope.query.queryReceiveTimeEnd ? $scope.query.queryReceiveTimeEnd : null
            };
        }

        Warehouse.getAll({ // 导入所有仓库
            enabled: true,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有供应商
            return Supplier.getAll({
                enabled: true,
                sort: ['name']
            }).then(function(suppliers) {
                $scope.suppliers = suppliers;
            });
        }).then(function() {
            purchaseOrderDeliveryService.get(getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $.each(page.content, function(){
                    var receive = this;
                    var batches = this.batches;
                    receive.enterableQty = 0;
                    receive.enteredQty = 0;
                    $.each(receive.items, function(){
                        receive.enterableQty += this.receiveQty;
                    });
                    $.each(batches, function(){
                        var batch = this;
                        $.each(batch.items, function(){
                            console.log(this.changedQuantity);
                            if (this.changedQuantity) {
                                receive.enteredQty += this.changedQuantity;
                            }
                        });
                    });
                }); 
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        });

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.query);
            purchaseOrderDeliveryService.get(getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.query = angular.copy($scope.defaultQuery);
            console.log($scope.query);
            purchaseOrderDeliveryService.get(getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };

        //$scope.toggleShipmentCompleteSlide = function(){
        //   $scope.shipmentCompleteSlideChecked = !$scope.shipmentCompleteSlideChecked;
        //};


        $scope.batchManipulationValue = 'batchManipulation';

        $scope.checkAllPurchaseOrderDeliveries = function() {
            for (var purchaseOrderDeliveryIndex in $scope.page.content) {
                $scope.page.content[purchaseOrderDeliveryIndex].isSelected = !$scope.page.content[purchaseOrderDeliveryIndex].isSelected;
            }
        };

        ///* 批量操作 */
        //$scope.batchManipulation = function()
        //{
        //    var shipments = $scope.page.content;
        //    shipmentService.selectedPurchaseOrders.length = 0;
        //    $.each(shipments, function(){
        //        var shipment = this;
        //        if (shipment.isSelected) {
        //            shipmentService.selectedPurchaseOrders.push(angular.copy(shipment));
        //        }
        //    });
        //    if (shipmentService.selectedPurchaseOrders.length > 0)
        //    {
        //        if($scope.batchManipulationValue === 'shipmentExport')
        //        {
        //            toastr.info('发货单导出！');
        //        }
        //        else if($scope.batchManipulationValue === 'shipmentPrint')
        //        {
        //            toastr.info('发货单打印！');
        //        }
        //        else if($scope.batchManipulationValue === 'shipmentObsolete')
        //        {
        //            $('#obsoleteShipments').modal('show');
        //        }
        //    }
        //    else
        //    {
        //        toastr.error('请选择一到多个发货单来继续！');
        //    }
        //
        //
        //    $scope.batchManipulationValue = 'batchManipulation';
        //};

        //$scope.showObsoleteShipmentModal = function( shipment )
        //{
        //    $scope.selectedPurchaseOrder = shipment;
        //
        //    $('#obsoleteShipment').modal('show');
        //};

        /* 将选中发货单作废 */
        //$scope.obsoleteShipment = function()
        //{
        //    /* 作废状态 */
        //    $scope.selectedPurchaseOrder.shipStatus = 5;
        //
        //    shipmentService.save({
        //        action: 'update'
        //    }, $scope.selectedPurchaseOrder, function( shipment ) {
        //
        //            console.log( shipment );
        //
        //            shipmentService.get( getQueryParamJSON(), function(page) {
        //                console.log('page:');
        //                console.log(page);
        //                $scope.page = page;
        //                $scope.totalPagesList = Utils.setTotalPagesList(page);
        //            });
        //        });
        //
        //    $('#obsoleteShipment').modal('hide');
        //
        //    toastr.error('作废选中发的货单！');
        //};

        /* 将批量选中的货单作废 */
        //$scope.obsoleteShipments = function()
        //{
        //    for( var shipmentIndex in shipmentService.selectedPurchaseOrders )
        //    {
        //        /* 作废状态 */
        //        shipmentService.selectedPurchaseOrders[shipmentIndex].shipStatus = 5;
        //    }
        //
        //    var shipment = {};
        //    shipment.shipments = shipmentService.selectedPurchaseOrders;
        //
        //    shipmentService.saveShipments( shipment )
        //        .then(function(shipments) {
        //
        //            console.log( shipments );
        //
        //            shipmentService.get( getQueryParamJSON(), function(page) {
        //                console.log('page:');
        //                console.log(page);
        //                $scope.page = page;
        //                $scope.totalPagesList = Utils.setTotalPagesList(page);
        //            });
        //        });
        //
        //    $('#obsoleteShipments').modal('hide');
        //    toastr.error('作废批量选中的发货单！');
        //};


        //$scope.showShipmentCompleteSlide = function( shipment )
        //{
        //    shipmentService.setselectedPurchaseOrder( angular.copy( shipment ) );
        //
        //    executeOperationReviewCompleteShipment('VERIFY');
        //
        //    $scope.toggleShipmentCompleteSlide();
        //};


        //function executeOperationReviewCompleteShipment(action)
        //{
        //    var reviewDTO = {
        //        action                      : action,
        //        shipment                    : shipmentService.getselectedPurchaseOrder(),
        //        dataMap : {
        //            executeOperatorId                : $rootScope.user().id
        //        },
        //        ignoredMap : {
        //            emptyCourierError : false,
        //            emptyShipNumberError : false,
        //            emptyReceiveNameError : false,
        //            emptyReceivePhoneError : false,
        //            emptyReceiveAddressError : false
        //        }
        //    };
        //    console.log('Before Operation Review:');
        //    console.log(reviewDTO);
        //    shipmentService.confirmOperationReviewWhenCompleteShipment(reviewDTO).then(function(review){
        //        console.log('After Operation Review Complete Shipment:');
        //        console.log(review);
        //    });
        //}

        

    }
]);
