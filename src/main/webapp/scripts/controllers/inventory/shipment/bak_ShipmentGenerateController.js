//angular.module('ecommApp')
//
//.controller('bak_ShipmentGenerateController', ['$scope', '$rootScope', 'toastr', '$modal', 'filterFilter', 'Warehouse', 'Shop', 'orderService', 'courierService', 'Process', 'Utils', 'Inventory', 'OrderItem',
//    function($scope, $rootScope, toastr, $modal, filterFilter, Warehouse, Shop, orderService, courierService, Process, Utils, Inventory, OrderItem) {
//
//        $scope.template = {
//            shipmentGenerateOperationReview: {
//                url: 'views/inventory/shipment/shipment-generate-operation-review.html?' + (new Date())
//            }
//        };
//
//        $scope.defaultQuery = {
//            size: 20,
//            sort: ['internalCreateTime,desc'],
//            warehouse: undefined,
//            shop: undefined,
//            statuses: [],
//        };
//        $scope.query = angular.copy($scope.defaultQuery);
//        $scope.warehouses = [];
//        $scope.shops = [];
//        $scope.couriers = [];
//        $scope.courier = {};
//        $scope.processes = [];
//        $scope.inventory = {}; // 库存对象，里面每一个子属性都是一个仓库，仓库的值是一个归类好的产品数组
//
//        $scope.generateShipmentSheetCheckListSlideChecked = false;
//        $scope.couriers = [];
//
//        // 将所有店铺过滤，拿出所有配置了配送状态的店铺的ID
//        $scope.selectAllShops = function(shops) {
//            $.each(shops, function() {
//                var shop = this;
//                if (shop.deployStep) {
//                    var exist = false;
//                    $.each($scope.query.statuses, function() {
//                        if (this.id === shop.deployStep.id) {
//                            exist = true;
//                            return false;
//                        }
//                    });
//                    if (!exist) {
//                        $scope.query.statuses.push(shop.deployStep);
//                    }
//                }
//            });
//        };
//
//        Warehouse.getAll({ // 导入所有仓库
//            enabled: true,
//            sort: ['name']
//        }).then(function(warehouses) {
//            $scope.warehouses = warehouses;
//        }).then(function() { // 导入所有店铺
//            return courierService.getAll({
//                sort: ['name']
//            }).then(function(couriers) {
//                $scope.couriers = couriers;
//            });
//        }).then(function() { // 导入所有店铺
//            return Shop.getAll({
//                enabled: true,
//                sort: ['name']
//            }).then(function(shops) {
//                $scope.shops = shops;
//                $scope.selectAllShops(shops);
//                //console.log($scope.query.statuses);
//            });
//        }).then(function() {
//            $scope.search( $scope.query );
//        });
//
//        $scope.searchData = function(query, number)
//        {
//            orderService.getPagedOrdersForOrderDeploy({
//                page: number ? number : 0,
//                size: query.size,
//                sort: ['internalCreateTime,desc'],
//                warehouseId: query.warehouse ? query.warehouse.id : null,
//                shopId: query.shop ? query.shop.id : null,
//                deleted: false
//            }).then(function(page) {
//                console.log('page:');
//                console.log(page);
//                $scope.page = page;
//                $.each(page.content, function() {
//                    orderService.checkItemProductShopTunnel(this);
//                });
//                Utils.initList(page, $scope.query);
//                $scope.isCheckedAll = false;
//            });
//        };
//
//        $scope.turnPage = function(number)
//        {
//            if (number > -1 && number < $scope.page.totalPages) {
//                $scope.searchData( $scope.query, number );
//            }
//        };
//
//        $scope.search = function(query)
//        {
//            $scope.searchData(query);
//        };
//
//        $scope.reset = function()
//        {
//            $scope.query = angular.copy($scope.defaultQuery);
//            $scope.searchData($scope.query);
//        };
//
//        /* 选择仓库用 */
//        $scope.selectItemWarehouse = function(item, $item){
//            console.clear();
//            console.log('selectItemWarehouse');
//            console.log(item);
//            console.log($item);
//            item.warehouseId = $item.id;
//            OrderItem.save({}, item, function(item){
//                console.log('selectItemWarehouse complete');
//                console.log(item);
//            });
//        };
//
//        $scope.toggleShipmentSheetSlide = function(){
//            $scope.generateShipmentSheetCheckListSlideChecked = !$scope.generateShipmentSheetCheckListSlideChecked;
//
//            /* 如果打开弹出层，则显示弹出层滚动条并隐藏body滚动条，反之亦然 */
//            if( $scope.generateShipmentSheetCheckListSlideChecked )
//            {
//                $('body').css('overflow','hidden');
//                $('[ps-open="generateShipmentSheetCheckListSlideChecked"]').css('overflow','auto');
//            }
//            else
//            {
//                $('body').css('overflow','auto');
//                $('[ps-open="generateShipmentSheetCheckListSlideChecked"]').css('overflow','hidden');
//            }
//        };
//
//
//        // selected orders
//        $scope.isCheckedAll = false;
//        $scope.batchManipulationValue = 'batchManipulation';
//        $scope.selectedCourier = {};
//        $scope.startCourierNumber = '';
//
//        $scope.checkAllOrders = function()
//        {
//            for( var order in $scope.page.content )
//            {
//                $scope.page.content[order].isSelected = $scope.isCheckedAll;
//            }
//        };
//
//        /* 生成多个订单的货运单  */
//        $scope.batchManipulation = function()
//        {
//            var orders = $scope.page.content;
//            orderService.selectedOrders.length = 0;
//            $.each(orders, function(){
//                var order = this;
//                if (order.isSelected) {
//                    orderService.selectedOrders.push(angular.copy(order));
//                }
//            });
//            if (orderService.selectedOrders.length > 0)
//            {
//                if($scope.batchManipulationValue === 'generateShipment')
//                {
//                    $('#generateShipment').modal('show');
//                }
//            }
//            else
//            {
//                toastr.error('请选择一到多个订单来继续！');
//            }
//
//
//            $scope.batchManipulationValue = 'batchManipulation';
//        };
//
//
//        function executeShipmentOperationReview(action)
//        {
//            var reviewDTO = {
//                action                    : action,
//                orders                    : orderService.selectedOrders,
//                selectedCourier           : $scope.courier.selected,
//                dataMap : {
//                    operatorId                  : $rootScope.user().id,
//                    startShipNumber           : $scope.startShipNumber
//                },
//                ignoredMap : {
//                    differentWarehouseError : false,
//                    differentDeliveryMethodError : false,
//                    emptyCourierAndShipNumberError : false,
//                    warehouseExistOrderShipmentError : false,
//                    emptyReceiveAddressError : false
//                },
//                assignWarehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null
//            };
//            console.log('Before Operation Review:');
//            console.log(reviewDTO);
//            orderService.confirmOrderWhenGenerateShipment(reviewDTO).then(function(review){
//                console.log('After Operation Review:');
//                console.log(review);
//            });
//        }
//
//        /* 第一次进入生成发货单操作复核 */
//        $scope.checkGenerateShipmentModal = function()
//        {
//            /* 隐藏指定快递公司弹出层 */
//            $('#generateShipment').modal('hide');
//
//            executeShipmentOperationReview('VERIFY');
//
//            $scope.toggleShipmentSheetSlide();
//            /* 查看检查结果 */
//        };
//
//
//
//    }
//]);
