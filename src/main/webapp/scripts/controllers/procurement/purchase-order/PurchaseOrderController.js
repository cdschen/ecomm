angular.module('ecommApp')

.controller('PurchaseOrderController', ['$scope', '$rootScope', 'toastr', 'Warehouse', 'Supplier', 'User', 'Utils', 'purchaseOrderService',
    function($scope, $rootScope, toastr, Warehouse, Supplier, User, Utils, purchaseOrderService) {

        $scope.template = {
            //shipmentComplete: {
            //    url: 'views/inventory/shipment/shipment-complete.html?' + (new Date())
            //}
        };

        /* Activate Date Picker */
        $('input[ng-model="query.queryCreateTimeStart"], input[ng-model="query.queryCreateTimeEnd"]').datepicker({
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
        $scope.purchaseOrders = [];
        $scope.purchaseOrderStatus = [
            {
                id : 1, name : '待收货'
            },
            {
                id : 2, name : '已收货'
            },
            {
                id : 3, name : '作废'
            }
        ];

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
                queryCreateTimeEnd: $scope.query.queryCreateTimeEnd ? $scope.query.queryCreateTimeEnd : null,
                lastUpdateStart: $scope.query.lastUpdateStart ? $scope.query.lastUpdateStart : null,
                lastUpdateEnd: $scope.query.lastUpdateEnd ? $scope.query.lastUpdateEnd : null,
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
                $scope.isCheckedAll = false;
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.query = angular.copy($scope.defaultQuery);
            console.log($scope.query);
            purchaseOrderService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };


        $scope.isCheckedAll = false;
        $scope.batchManipulationValue = 'batchManipulation';

        $scope.checkAllOrders = function()
        {
            for( var purchaseOrder in $scope.page.content )
            {
                $scope.page.content[purchaseOrder].isSelected = $scope.isCheckedAll;
            }
        };

        ///* 批量操作 */
        $scope.batchManipulation = function()
        {
            var purchaseOrders = $scope.page.content;
            var ids = [];
            purchaseOrderService.selectedPurchaseOrders.length = 0;
            $.each(purchaseOrders, function()
            {
                var purchaseOrder = this;
                if (purchaseOrder.isSelected) {
                    purchaseOrderService.selectedPurchaseOrders.push(angular.copy( purchaseOrder ));
                    ids.push( purchaseOrder.id );
                }
            });
            if (purchaseOrderService.selectedPurchaseOrders.length > 0)
            {
                if($scope.batchManipulationValue === 'purchaseOrderExport')
                {
                    window.location.href = '/api/purchase-order/export?ids=' + ids;
                    toastr.info('批量导出采购单');
                }
                else if($scope.batchManipulationValue === 'purchaseOrderPrint')
                {
                    toastr.info('采购单打印！');
                }
                else if($scope.batchManipulationValue === 'purchaseOrderObsolete')
                {
                    $('#obsoletePurchaseOrders').modal('show');
                }
            }
            else
            {
                toastr.error('请选择一到多个采购单来继续！');
            }

            $scope.batchManipulationValue = 'batchManipulation';
        };

        $scope.showObsoletePurchaseOrderModal = function( purchaseOrder )
        {
            $scope.selectedPurchaseOrder = purchaseOrder;

            $('#obsoletePurchaseOrder').modal('show');
        };

        /* 将选中采购单作废 */
        $scope.obsoletePurchaseOrder = function()
        {
            /* 作废状态 */
            $scope.selectedPurchaseOrder.status = 3;

            purchaseOrderService.save({
                action: 'update'
            }, $scope.selectedPurchaseOrder, function( purchaseOrder ) {

                console.log( purchaseOrder );

                purchaseOrderService.get( getQueryParamJSON(), function(page) {
                    console.log('page:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            });

            $('#obsoletePurchaseOrder').modal('hide');

            toastr.success('作废选中的采购单！');
        };

        /* 将批量选中的采购单作废 */
        $scope.obsoletePurchaseOrders = function()
        {
            for( var purchaseOrderIndex in purchaseOrderService.selectedPurchaseOrders )
            {
                /* 作废状态 */
                purchaseOrderService.selectedPurchaseOrders[ purchaseOrderIndex ].status = 3;
            }

            var purchaseOrder = {};
            purchaseOrder.purchaseOrders = purchaseOrderService.selectedPurchaseOrders;

            console.log('obsoletePurchaseOrders');
            console.log( purchaseOrder );

            purchaseOrderService.savePurchaseOrders( purchaseOrder )
                .then(function( purchaseOrders ) {

                    console.log( purchaseOrders );

                    purchaseOrderService.get( getQueryParamJSON(), function(page) {
                        console.log('page:');
                        console.log(page);
                        $scope.page = page;
                        $scope.totalPagesList = Utils.setTotalPagesList(page);
                    });
                });

            $('#obsoletePurchaseOrders').modal('hide');
            toastr.error('作废批量选中的发货单！');
        };

    }
]);
