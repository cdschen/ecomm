angular.module('ecommApp')

.controller('OrderDeployController', ['$scope', '$rootScope', 'toastr', 'filterFilter', 'Warehouse', 'Shop', 'orderService', 'Process', 'Utils', 'Inventory',
    function($scope, $rootScope, toastr, filterFilter, Warehouse, Shop, orderService, Process, Utils, Inventory) {

        $scope.template = {
            status: {
                url: 'views/inventory/orderdeploy/order-deploy.status.html?' + (new Date())
            },
            generateShipment: {
                url: 'views/inventory/orderdeploy/order-deploy.shipment-generate.html?' + (new Date())
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
        $scope.processes = [];
        $scope.inventory = {}; // 库存对象，里面每一个子属性都是一个仓库，仓库的值是一个归类好的产品数组
        $scope.statusSlideChecked = false;

        $scope.generateShipmentSlideChecked = false;
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

        /*.then(function() { // 导入所有流程
            return Process.getAll({ 
                deleted: false,
                objectType: 1
            }).then(function(processes) {
                $scope.processes = processes;
                Process.initStatus(processes);
            });
        })*/

        Warehouse.getAll({ // 导入所有仓库
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        }).then(function() { // 导入所有店铺
            return Shop.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(shops) {
                $scope.shops = shops;
                $scope.selectAllShops(shops);
                console.log($scope.query.statuses);
            });
        }).then(function() { // 导入所有库存, 按仓库分组
            return Inventory.getAll({
                sort: ['productId', 'inventoryBatchId']
            }).then(function(inventories) {
                $scope.inventory = Inventory.refreshByWarehouse($scope.warehouses, inventories);
                $.each($scope.inventory, function(key, value) {
                    var warehouse = value;
                    warehouse.products = Inventory.refresh(warehouse.inventories);
                });
                console.log('inventory:');
                console.log($scope.inventory);
            });
        }).then(function() {
            orderService.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                statusIds: Process.refreshStatus($scope.query.statuses),
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
            });
        });

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.query);
            orderService.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['internalCreateTime,desc'],
                warehouseId: $scope.query.warehouse ? $scope.query.warehouse.id : null,
                shopId: $scope.query.shop ? $scope.query.shop.id : null,
                statusIds: Process.refreshStatus($scope.query.statuses),
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
                statusIds: Process.refreshStatus($scope.query.statuses),
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
            });
        };

        // Generate Shipment
        $scope.closeGenerateShipmentSlide = function() {
            $scope.generateShipmentSlideChecked = false;
        };

        $scope.loadGenerateShipment = function() {
            $scope.generateShipmentSlideChecked = true;
        };

        // selected orders
        $scope.batch_manipulation_value = 'batch_manipulation';
        $scope.is_checked_all = false;
        $scope.shipment_generate_type = '';
        $scope.finalSingleShipment = [];
        $scope.finalMultipleShipment = [];
        $scope.isAnyError = false;
        $scope.shopNeedToAssignTunnel = [];
        $scope.productNeedToInSameTunnel = [];

        $scope.checkAnyAvailable = function(order)
        {
            var isAssignTunnel = true;
            var isAllMatched = true;
            var comparedWarehouseId = 0;
            for(var item in order.items)
            {
                if( ! order.items[item].assignTunnel)
                {
                    isAssignTunnel = false;
                    break;
                }
                if(item == 0)
                {
                    comparedWarehouseId = order.items[item].assignTunnel.defaultWarehouse.id;
                }
                else
                {
                    if(comparedWarehouseId != order.items[item].assignTunnel.defaultWarehouse.id)
                    {
                        isAllMatched = false;
                        break;
                    }
                }
            }

            if( ! isAssignTunnel )
            {
                $scope.isAnyError = true;
                $scope.shopNeedToAssignTunnel.push(order.shop.name);
            }
            else
            {
                if( ! isAllMatched )
                {
                    $scope.isAnyError = true;
                    $scope.productNeedToInSameTunnel.push(order.id);
                }
            }
        };

        /* 如果有错则显示错误提示信息 */
        $scope.isAnyErrorShowMsg = function()
        {
            if( $scope.isAnyError )
            {
                for(var shopName in $scope.shopNeedToAssignTunnel)
                {
                    toastr.error('请为［' + $scope.shopNeedToAssignTunnel[shopName] + '］店铺分配一个默认仓库！');
                }
                for(var orderId in $scope.productNeedToInSameTunnel)
                {
                    toastr.error('［' + $scope.productNeedToInSameTunnel[orderId] + '］订单的商品必须处于同一个仓库！');
                }
            }

            /* 重置错误提示信息 */
            $scope.shopNeedToAssignTunnel.length = 0;
            $scope.productNeedToInSameTunnel.length = 0;

            return $scope.isAnyError;
        };

        /* 生成单个快递单号 */
        $scope.generateShipment = function(order)
        {
            $scope.finalSingleShipment.length = 0;

            /* 检查订单的状态是否允许出运单号 */
            $scope.checkAnyAvailable(order);

            /* 判断是否有错误提示信息，如果没有则可以生成运单号 */
            if( ! $scope.isAnyErrorShowMsg() )
            {
                $scope.shipment_generate_type = 'single';
                $scope.generateShipmentSlideChecked = true;
                $scope.finalSingleShipment.push(setOrderAndItemsToShipmentAndItemsThenReturn(order));
            }
            /* 重置错误提示信息 */
            $scope.isAnyError = false;
        };

        $scope.checkAllOrder = function()
        {
            for(var order in $scope.page.content)
            {
                $scope.page.content[order].isSelected = $scope.is_checked_all;
                if( $scope.is_checked_all )
                {
                    var currentOrder = $scope.page.content[order];
                    $scope.finalMultipleShipment.push(setOrderAndItemsToShipmentAndItemsThenReturn(currentOrder));
                }
                else
                {
                    $scope.finalMultipleShipment.length = 0;
                }
            }
        };

        /* 生成多个yundanhao  */
        $scope.batch_manipulation = function()
        {
            $scope.finalMultipleShipment.length = 0;

            if($scope.batch_manipulation_value == 'generate_shipment')
            {
                for(var order in $scope.page.content)
                {
                    var currentOrder = $scope.page.content[order];
                    if( currentOrder.isSelected )
                    {
                        $scope.finalMultipleShipment.push(setOrderAndItemsToShipmentAndItemsThenReturn(currentOrder));
                        $scope.checkAnyAvailable(currentOrder);
                    }
                }
                /* 判断是否有选中订单，没有则显示错误提示信息 */
                if($scope.finalMultipleShipment && $scope.finalMultipleShipment.length > 0)
                {
                    /* 判断是否有错误提示信息，如果没有则可以生成快递单 */
                    if( ! $scope.isAnyErrorShowMsg() )
                    {
                        for(var order in $scope.page.content)
                        {
                            var currentOrder = $scope.page.content[order];
                            if( currentOrder.isSelected )
                            {
                                $scope.finalMultipleShipment.push(setOrderAndItemsToShipmentAndItemsThenReturn(currentOrder));
                            }
                        }
                        $scope.shipment_generate_type = 'multiple';
                        $scope.generateShipmentSlideChecked = true;
                    }
                }
                else
                {
                    toastr.error('请选择一到多个订单来继续！');
                }

            }

            /* 重置错误提示信息 */
            $scope.isAnyError = false;
            $scope.batch_manipulation_value = 'batch_manipulation';
        };

        function setOrderAndItemsToShipmentAndItemsThenReturn(order)
        {
            var user = $rootScope.user();

            var shipment = {
                'order_id' : order.id,
                'operator_id' : user.id,
                'ship_status' : '待取件',
                'qty_total_item_shipped' : order.qtyTotalItemOrdered,
                'total_weight' : order.weight,
                'sender_name' : order.senderName,
                'sender_address' : order.senderAddress,
                'sender_phone' : order.senderPhone,
                'sender_email' : order.senderEmail,
                'sender_post' : order.senderPost,
                'receive_name' : order.receiveName,
                'receive_phone' : order.receivePhone,
                'receive_email' : order.receiveEmail,
                'receive_country' : order.receiveCountry,
                'receive_province' : order.receiveProvince,
                'receive_city' : order.receiveCity,
                'receive_address' : order.receiveAddress,
                'receive_post' : order.receivePost,

                'memo' : order.memo
            };
            shipment.shipmentItems = [];
            if( order.items )
            {
                for(var item in order.items)
                {
                    var currentOrderItem = order.items[item];
                    var shipmentItem = {
                        'order_item_id' : currentOrderItem.id,
                        'qty_shipped' : currentOrderItem.qtyOrdered
                    };
                    shipment.shipmentItems.push(shipmentItem);
                }
            }
            return shipment;
        }

        $scope.generateFinalShipment = function()
        {
            console.log('$scope.shipment_generate_type: ' + $scope.shipment_generate_type);
            console.log('$scope.finalSingleShipment: ');
            console.log($scope.finalSingleShipment);
            console.log('$scope.finalMultipleShipment: ');
            console.log($scope.finalMultipleShipment);
        };

        /* 发货时要改变的字段 */
        //`create_time` datetime NOT NULL,     #创建时间
        //`courier_id` varchar(255) NOT NULL,             #快递公司id
        //`ship_number` varchar(255),          #快递单号
        //`qty_total_item_shipped` int NOT NULL DEFAULT 0, #已发货商品总件数
        //`shipping_fee` decimal(14,2) NOT NULL,  #运费金额
        //`grand_total` decimal(14,2) NOT NULL,  #订单总金额 = 商品金额(subtotal) + 运费(shipping_fee)
        // `shipping_description` varchar(255),      #店铺要求的发货方式描述， 可以为空， 也未必真的通过这种方式发货
        // `delivery_method` tinyint,                #发货方式， 1=快递， 2=自提， 3=送货上门
        //`pickup_time` datetime,              #快递取件时间
        //`shipfee_cost` decimal(14,2)  NOT NULL,  #运费成本

        //`last_update` datetime NOT NULL,              #运单最近更新时间
        //`last_update_time` datetime NOT NULL,   #订单最近更新时间


        /* 收货时要改变的字段 */
        //`ship_status` tinyint NOT NULL,      #发货单状态： 1: 待取件， 2: 已发出， 3： 已签收， 4： 派送异常
        //`signup_time` datetime,              #签收时间
        //`last_update` datetime NOT NULL,              #运单最近更新时间
        //`last_update_time` datetime NOT NULL,   #订单最近更新时间



    }
]);