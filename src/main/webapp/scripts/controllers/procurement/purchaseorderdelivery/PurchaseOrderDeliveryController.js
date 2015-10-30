angular.module('ecommApp')

.controller('PurchaseOrderDeliveryController', ['$scope', '$rootScope', 'toastr', 'Warehouse', 'Supplier', 'User', 'Utils', 'purchaseOrderDeliveryService',
    function($scope, $rootScope, toastr, Warehouse, Supplier, User, Utils, purchaseOrderDeliveryService) {

        $scope.template = {
            //shipmentComplete: {
            //    url: 'views/inventory/shipment/shipment-complete.html?' + (new Date())
            //}
        };

        var $ = angular.element;

        /* Activate Date Picker */
        $('input[ng-model="query.queryReceiveTimeStart"], input[ng-model="query.queryReceiveTimeEnd"]').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'top left',
            todayHighlight: true
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
        function getQueryParamJSON()
        {
            return {
                page: 0,
                size: $scope.pageSize,
                sort: ['receiveTime,desc'],
                queryWarehouseId: $scope.query.queryWarehouse ? $scope.query.queryWarehouse.id : null,
                queryPurchaseOrderDeliveryId: $scope.query.queryPurchaseOrderDeliveryId ? $scope.query.queryPurchaseOrderDeliveryId : null,
                queryCreatorId: $scope.query.queryCreator ? $scope.query.queryCreator.id : null,
                querySupplierId: $scope.query.querySupplier ? $scope.query.querySupplier.id : null,
                //receiveStatus: $scope.query.receiveStatus ? $scope.query.receiveStatus : null,
                //paymentStatus: $scope.query.paymentStatus ? $scope.query.paymentStatus : null,
                queryReceiveTimeStart: $scope.query.queryReceiveTimeStart ? $scope.query.queryReceiveTimeStart : null,
                queryReceiveTimeEnd: $scope.query.queryReceiveTimeEnd ? $scope.query.queryReceiveTimeEnd : null
            };
        }

        Warehouse.getAll({ // 导入所有仓库
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有供应商
            return Supplier.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(suppliers) {
                $scope.suppliers = suppliers;
            });
        }).then(function() { // 导入所有用户
            return User.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(users) {
                $scope.users = users;
            });
        }).then(function() {
            purchaseOrderDeliveryService.get( getQueryParamJSON(), function(page) {
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
            purchaseOrderDeliveryService.get( getQueryParamJSON(), function(page) {
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
            purchaseOrderDeliveryService.get( getQueryParamJSON(), function(page) {
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

        $scope.checkAllPurchaseOrderDeliveries = function()
        {
            for( var purchaseOrderDeliveryIndex in $scope.page.content )
            {
                $scope.page.content[purchaseOrderDeliveryIndex].isSelected = ! $scope.page.content[purchaseOrderDeliveryIndex].isSelected;
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
