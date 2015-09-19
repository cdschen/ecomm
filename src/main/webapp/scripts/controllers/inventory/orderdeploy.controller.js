angular.module('ecommApp')

.controller('OrderDeployController', ['$scope', '$rootScope', 'toastr', '$modal', 'filterFilter', 'Warehouse', 'Shop', 'orderService', 'courierService', 'Process', 'Utils', 'Inventory',
    function($scope, $rootScope, toastr, $modal, filterFilter, Warehouse, Shop, orderService, courierService, Process, Utils, Inventory) {

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
            });
        };

        // Generate Shipment
        $scope.closeGenerateShipmentSlide = function() {
            $scope.generateShipmentCheckListSlideChecked = false;
        };

        $scope.loadGenerateShipment = function() {
            $scope.generateShipmentCheckListSlideChecked = true;
        };

        $scope.toggleOutInventorySheetSlide = function(){
           $scope.generateOutInventorySheetCheckListSlideChecked = !$scope.generateOutInventorySheetCheckListSlideChecked;
        };

        $scope.toggleShipmentSheetSlide = function(){
            $scope.generateShipmentSheetCheckListSlideChecked = !$scope.generateShipmentSheetCheckListSlideChecked;
        };

        /* 开始：检查表存放的数据 */
        /**
         *
         * checkList = {
         *      isAllPassLooseCheck : 是否通过宽松检查［ true：当 messageList 所有 type 等于 danger 的 message 的 isPass 为 true 时 ］，［ false：当 messageList 某个 type 等于 danger 的 message 的 isPass 为 false 时 ］,
         *      isAllPassRestrictCheck : 是否通过约束检查［ true：当 messageList 所有 message 的 isPass 为 true 时 ］，［ false：当 messageList 某个 message 的 isPass 为 false 时 ］,
         *      successMessageTextColorClass : 检查通过信息的颜色,
         *      errorMessageTextColorClass : { 检查未通过信息的颜色，根据 message 的 type 来获取颜色
         *          danger : message 的 type 等于 danger 时提示信息的文字颜色,
         *          warning : message 的 type 等于 warning 时提示信息的文字颜色
         *      },
         *      successMessageIcon : 检查通过信息的图标,
         *      errorMessageIcon : { 检查未通过信息的图标，根据 message 的 type 来获取图标字体
         *          danger : message 的 type 等于 danger 时提示信息的图标,
         *          warning : message 的 type 等于 warning 时提示信息的图标
         *      },
         *      totalMessage : 总检查项,
         *      passMessage : 通过项,
         *      warningMessage : 警告项,
         *      dangerMessage : 失败项,
         *      messageList : [
         *          {
         *              name: 检查项名称,
         *              successMsg: 成功提示信息,
         *              errorMsg: 错误提示信息,
         *              triggerScopeMethod: 检查不通过时可以调用的方法，会赋给某个按钮的 ng-click 上,
         *              actionBtnText: 检查不通过时显示的操作按钮的文字,
         *              isActionBtnShow: ［ true：显示可以点击执行 triggerScopeMethod 方法的按钮 ］，［ false：隐藏可以点击执行 triggerScopeMethod 方法的按钮 ］,
         *              type: 错误类型，［ danger：危险，会发生严重些问题 ］，［ warning：警告，不会发生严重问题 ］,
         *              isPass: 是否通过，［ true：对应检查项通过 ］，［ false：对应检察项不通过 ］
         *          }
         *      ]
         * }
         */
        $scope.checkList = {
            isAllPassLooseCheck : false,
            isAllPassRestrictCheck : false,
            successMessageTextColorClass : 'text-success',
            errorMessageTextColorClass : {
                danger : 'text-danger',
                warning : 'text-warning'
            },
            successMessageIcon : 'glyphicon glyphicon-ok',
            errorMessageIcon : {
                danger : 'glyphicon glyphicon-remove-circle',
                warning : 'glyphicon glyphicon-warning-sign'
            },
            successMessageNonActionBtnClass : 'btn btn-xs btn-success btn-block',
            errorMessageNonActionBtnClass : 'btn btn-xs btn-danger btn-block',
            errorMessageBtnClass : {
                danger : 'btn btn-xs btn-danger btn-block',
                warning : 'btn btn-xs btn-warning btn-block'
            },
            totalMessage : 5,
            passMessage : 0,
            warningMessage : 3,
            dangerMessage : 0,
            messageList : [
                {
                    name : 'check_is_same_warehouse',
                    successMsg : '订单商品处于同一仓库', errorMsg : '必须同一个仓库',
                    isActionBtnShow : true, triggerScopeMethod : 'leaveSameWarehouseProductOrder()', actionBtnText : '修复',
                    type:'danger', isPass: false
                },
                {
                    name : 'check_is_assigned_courier_company',
                    successMsg : '快递公司已指定', errorMsg : '必须有快递公司，请通过点击［快递公司］下拉菜单来指定一个',
                    isActionBtnShow : false,
                    type : 'danger', isPass : false
                },
                {
                    name : 'check_is_duplicate_order_same_warehouse_shipment',
                    successMsg : '订单在同一仓库没有发货单', errorMsg : '订单已经有同一仓库下的发货单',
                    isActionBtnShow : true, triggerScopeMethod : 'leaveDifferentWarehouseShipmentOrder()', actionBtnText : '修复',
                    type : 'warning', isPass : false
                },
                {
                    name : 'check_is_same_order_delivery_method',
                    successMsg : '订单发货方式相同', errorMsg : '订单的发货方式不同',
                    isActionBtnShow : true, triggerScopeMethod : 'leaveSameDeliveryMethodOrder()', actionBtnText : '修复',
                    type : 'warning', isPass : false
                },
                {
                    name : 'check_is_not_empty_order_receive_address',
                    successMsg: ' 订单收货地址不为空', errorMsg : '订单的收货地址为空',
                    isActionBtnShow : true, triggerScopeMethod : 'leaveNoEmptyReceiveAddressOrder()', actionBtnText : '修复',
                    type : 'warning', isPass : false
                }
            ]
        };
        /* 结束：检查表存放的数据 */


        // selected orders
        $scope.batch_manipulation_value = 'batch_manipulation';
        $scope.is_checked_all = false;
        $scope.isAnyError = false;
        $scope.shopNeedToAssignTunnel = [];
        $scope.productNeedToInSameTunnel = [];
        $scope.selectedCourier = {};
        $scope.startCourierNumber = '';

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

        $scope.checkAllOrder = function()
        {
            for(var order in $scope.page.content)
            {
                $scope.page.content[order].isSelected = $scope.is_checked_all;
                //if( $scope.is_checked_all )
                //{
                //    var currentOrder = $scope.page.content[order];
                //    $scope.finalMultipleShipment.push(setOrderAndItemsToShipmentAndItemsThenReturn(currentOrder));
                //}
                //else
                //{
                //    $scope.finalMultipleShipment.length = 0;
                //}
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

        /* 将选中订单及其商品数据转成发货单及发货商品数据后返回 */
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
                    if (orderService.selectedOrders.length > 0) {
                        $scope.toggleOutInventorySheetSlide();
                        var reviewDTO = {
                            orders: orderService.selectedOrders
                        };
                        console.log('reviewDTO:');
                        console.log(reviewDTO);
                        orderService.confirmOrderWhenGenerateOutInventory(reviewDTO).then(function(review){
                            console.log('review:');
                            console.log(review);
                        });
                    } else {
                        toastr.error('请选择至少一个订单!');
                    }
                }
            }
            else
            {
                toastr.error('请选择一到多个订单来继续！');
            }


            /* 重置错误提示信息 */
            $scope.batch_manipulation_value = 'batch_manipulation';

            /* 重置错误提示信息 */
            //$scope.isAnyError = false;
        };

        /* 生成单个发货单 */
        $scope.openGenerateSingleShipmentModal = function(order)
        {
            $('#generateShipment').modal('show');
        };
        /* 生成多个发货单 */
        $scope.openGenerateMultipleShipmentModal = function()
        {
            $('#generateShipment').modal('show');
        };


        //{
        //    name : 'check_is_same_warehouse',
        //        successMsg : '订单商品处于同一仓库', errorMsg : '必须同一个仓库',
        //    isActionBtnShow : true, triggerScopeMethod : 'leaveSameWarehouseProductOrder()', actionBtnText : '修复',
        //    type:'danger', isPass: false
        //},
        //{
        //    name : 'check_is_assigned_courier_company',
        //        successMsg : '快递公司已指定', errorMsg : '必须有快递公司，请通过点击［快递公司］下拉菜单来指定一个',
        //    isActionBtnShow : false,
        //    type : 'danger', isPass : false
        //},
        //{
        //    name : 'check_is_duplicate_order_same_warehouse_shipment',
        //        successMsg : '订单在同一仓库没有发货单', errorMsg : '订单已经有同一仓库下的发货单',
        //    isActionBtnShow : true, triggerScopeMethod : 'leaveDifferentWarehouseShipmentOrder()', actionBtnText : '修复',
        //    type : 'warning', isPass : false
        //},
        //{
        //    name : 'check_is_same_order_delivery_method',
        //        successMsg : '订单发货方式相同', errorMsg : '订单的发货方式不同',
        //    isActionBtnShow : true, triggerScopeMethod : 'leaveSameDeliveryMethodOrder()', actionBtnText : '修复',
        //    type : 'warning', isPass : false
        //},
        //{
        //    name : 'check_is_not_empty_order_receive_address',
        //        successMsg: ' 订单收货地址不为空', errorMsg : '订单的收货地址为空',
        //    isActionBtnShow : true, triggerScopeMethod : 'leaveNoEmptyReceiveAddressOrder()', actionBtnText : '修复',
        //    type : 'warning', isPass : false
        //}

        function executeShipmentOperationReview(action)
        {
            var reviewDTO = {
                "action"                    : action,
                "orders"                    : orderService.selectedOrders,
                "selectedCourier"           : $scope.courier.selected,
                "dataMap" : {
                    "startShipNumber"           : $scope.startShipNumber
                }
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
