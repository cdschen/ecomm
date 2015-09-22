angular.module('ecommApp')

.controller('OrderDeployController', ['$scope', '$rootScope', 'toastr', '$modal', 'filterFilter', 'Warehouse', 'Shop', 'orderService', 'courierService', 'Process', 'Utils', 'Inventory', 'OrderItem',
    function($scope, $rootScope, toastr, $modal, filterFilter, Warehouse, Shop, orderService, courierService, Process, Utils, Inventory, OrderItem) {

        $scope.template = {
            status: {
                url: 'views/inventory/orderdeploy/order-deploy.status.html?' + (new Date())
            },
            generateShipment: {
                url: 'views/inventory/orderdeploy/order-deploy.shipment-generate.html?' + (new Date())
            },
            generateOutInventorySheet: {
                url: 'views/inventory/orderdeploy/order-deploy.confirm-out-inventory-sheet.html?' + (new Date())
            },
            generateShipmentSheet: {
                url: 'views/inventory/orderdeploy/order-deploy.confirm-shipment-sheet.html?' + (new Date())
            }
        };

        var $ = angular.element;
        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultQuery = {
            warehouse: undefined,
            shop: undefined,
            statuses: [],
        };
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.warehouses = [];
        $scope.shops = [];
        $scope.couriers = [];
        $scope.courier = {};
        $scope.processes = [];
        $scope.inventory = {}; // 库存对象，里面每一个子属性都是一个仓库，仓库的值是一个归类好的产品数组
        $scope.statusSlideChecked = false;

        $scope.generateShipmentSheetCheckListSlideChecked = false;
        $scope.generateOutInventorySheetCheckListSlideChecked = false;
        $scope.couriers = [];

        // 将所有店铺过滤，拿出所有配置了配送状态的店铺的ID
        $scope.selectAllShops = function(shops) {
            $.each(shops, function() {
                var shop = this;
                if (shop.deployStep) {
                    var exist = false
                    $.each($scope.query.statuses, function() {
                        if (this.id === shop.deployStep.id) {
                            exist = true;
                            return false;
                        }
                    });
                    if (!exist) {
                        $scope.query.statuses.push(shop.deployStep);
                    }
                }
            });
        };

        Warehouse.getAll({ // 导入所有仓库
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有店铺
            return courierService.getAll({
                sort: ['name']
            }).then(function(couriers) {
                $scope.couriers = couriers;
            });
        }).then(function() { // 导入所有店铺
            return Shop.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(shops) {
                $scope.shops = shops;
                $scope.selectAllShops(shops);
                //console.log($scope.query.statuses);
            });
        }).then(function() {
            orderService.getPagedOrdersForOrderDeploy({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                deleted: false
            }).then(function(page) {
                console.log('page:');
                console.log(page);
                $.each(page.content, function() {
                    Shop.initShopDefaultTunnel(this.shop);
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        });

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.query);
            orderService.getPagedOrdersForOrderDeploy({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                deleted: false
            }).then(function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $.each(page.content, function() {
                    Shop.initShopDefaultTunnel(this.shop);
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.query = angular.copy($scope.defaultQuery);
            $scope.selectAllShops($scope.shops);
            console.log($scope.query);
            orderService.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                deleted: false
            }, function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $.each(page.content, function() {
                    Shop.initShopDefaultTunnel(this.shop);
                    orderService.checkItemProductShopTunnel(this);
                });
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };

        $scope.selectItemWarehouse = function(item, $item){
            console.clear();
            console.log('selectItemWarehouse');
            console.log(item);
            console.log($item);
            item.warehouseId = $item.id;
            OrderItem.save({}, item, function(item){
                console.log('selectItemWarehouse complete');
                console.log(item);
            });
        };

        $scope.toggleOutInventorySheetSlide = function(){
           $scope.generateOutInventorySheetCheckListSlideChecked = !$scope.generateOutInventorySheetCheckListSlideChecked;
        };

        $scope.toggleShipmentSheetSlide = function(){
            $scope.generateShipmentSheetCheckListSlideChecked = !$scope.generateShipmentSheetCheckListSlideChecked;
        };


        // selected orders
        $scope.isCheckedAll = false;
        $scope.batch_manipulation_value = 'batch_manipulation';
        $scope.selectedCourier = {};
        $scope.startCourierNumber = '';

        $scope.checkAllOrders = function()
        {
            for( var order in $scope.page.content )
            {
                $scope.page.content[order].isSelected = $scope.isCheckedAll;
            }
        };

        $scope.confirmSameWarehouseBySelectedOrders = function(orders) {
            var sameWarehouses = [];
            var differentWarehouseError = false;
            $.each(orders, function(){
                var order = this;
                console.log(order.id);
                $.each(order.items, function(){
                    var item = this;
                    if (item.assignTunnel) {
                        sameWarehouses.push(item.assignTunnel.defaultWarehouse.name);
                        $.each(sameWarehouses,function(){
                            if (this !== item.assignTunnel.defaultWarehouse.name) {
                                console.log(item.id +':'+item.assignTunnel.defaultWarehouse.name);
                                differentWarehouseError = true;
                                return false;
                            }
                        });
                    }
                });
            });

            if (differentWarehouseError) {
                toastr.error('选择的订单中的细目来自不同的仓库，请调整.');
            }
        };

        /* 生成多个订单的货运单  */
        $scope.batch_manipulation = function()
        {
            var orders = $scope.page.content;
            orderService.selectedOrders.length = 0;
            $.each(orders, function(){
                var order = this;
                if (order.isSelected) {
                    orderService.selectedOrders.push(angular.copy(order));
                }
            });
            if (orderService.selectedOrders.length > 0)
            {
                if($scope.batch_manipulation_value == 'generate_shipment')
                {
                    $('#generateShipment').modal('show');
                }

                if ($scope.batch_manipulation_value === 'generate_out_inventory_sheet') {
                    var orders = $scope.page.content;
                    orderService.selectedOrders.length = 0;
                    $.each(orders, function(){
                        var order = this;
                        if (order.isSelected) {
                            orderService.selectedOrders.push(angular.copy(order));
                        }
                    });

                    $scope.toggleOutInventorySheetSlide();
                    var reviewDTO = {
                        orders: orderService.selectedOrders,
                        assignWarehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                        ignoredMap: {
                            'productInventoryNotEnough': false,
                            'orderExistOutInventorySheet': false
                        }
                    };
                    console.log('reviewDTO:');
                    console.log(reviewDTO);
                    orderService.confirmOrderWhenGenerateOutInventory(reviewDTO).then(function(review){
                        console.log('review:');
                        console.log(review);
                    });
                }
            }
            else
            {
                toastr.error('请选择一到多个订单来继续！');
            }


            $scope.batch_manipulation_value = 'batch_manipulation';
        };


        function executeShipmentOperationReview(action)
        {
            var reviewDTO = {
                action                    : action,
                orders                    : orderService.selectedOrders,
                selectedCourier           : $scope.courier.selected,
                dataMap : {
                    operatorId                  : $rootScope.user().id,
                    startShipNumber           : $scope.startShipNumber
                },
                ignoredMap : {
                    differentWarehouseError : false,
                    differentDeliveryMethodError : false,
                    emptyCourierAndShipNumberError : false,
                    warehouseExistOrderShipmentError : false,
                    emptyReceiveAddressError : false
                },
                assignWarehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null
            };
            console.log('Before Operation Review:');
            console.log(reviewDTO);
            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        }

        /* 第一次进入生成发货单操作复核 */
        $scope.checkGenerateShipmentModal = function()
        {
            /* 隐藏指定快递公司弹出层 */
            $('#generateShipment').modal('hide');

            executeShipmentOperationReview('VERIFY');

            $scope.toggleShipmentSheetSlide();
            /* 查看检查结果 */
        };
    


    }
]);
