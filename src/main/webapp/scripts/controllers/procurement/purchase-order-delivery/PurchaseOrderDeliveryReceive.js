angular.module('ecommApp')

.controller('PurchaseOrderDeliveryReceive', ['$scope', '$rootScope', '$state', '$stateParams', '$interval', '$timeout', 'toastr', 'purchaseOrderService', 'purchaseOrderDeliveryService',
    function($scope, $rootScope, $state, $stateParams, $interval, $timeout, toastr, purchaseOrderService, purchaseOrderDeliveryService) {

        $scope.purchaseOrderDelivery =
        {
            purchaseOrderId : null,
            receiveUser : $rootScope.user()

        };
        $scope.purchaseOrderDeliveryItem =
        {
            realPurchaseUnitPrice   :   0,
            receiveQty  :   1
        };

        function updateReceiveTime() {
            $scope.receiveTime = new Date();
        }

        var receiveTime = $interval(updateReceiveTime, 500);

        $scope.items = [];

        $('.sandbox-container input').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            todayHighlight: true,
            autoclose: true
        });


        if ($stateParams.id && $stateParams.id !== '')
        {
            purchaseOrderService.get({
                id: $stateParams.id
            }, {}, function(purchaseOrder)
            {
                $.each(purchaseOrder.items, function()
                {
                    var purchaseOrderDeliveryItem =
                    {
                        purchaseOrderItemId : this.id,
                        supplierProduct : this.supplierProduct,
                        realPurchaseUnitPrice : this.estimatePurchaseUnitPrice,
                        receiveQty : this.purchaseQty,
                        creditQty : 0
                    };

                    $scope.items.push( angular.copy(purchaseOrderDeliveryItem) );
                });

                $scope.purchaseOrderDelivery.purchaseOrderId = purchaseOrder.id;
            });
        }


        $scope.saveItem = function(item, itemAddForm)
        {
            var isQualified = true;

            if( ! item.supplierProduct.supplierProductName )
            {
                toastr.warning('［名称］不能为空');
                isQualified = false;
            }
            if( ! item.receiveQty )
            {
                toastr.warning('［数量］不能为空');
                isQualified = false;
            }

            if( isQualified )
            {
                item.creditQty = 0;
                $scope.items.push(angular.copy(item));

                itemAddForm.$setPristine();

                item = null;
            }
        };

        $scope.selectedRemoveItem = null;
        $scope.showRemoveItemModal = function(selectedRemoveItem)
        {
            $scope.selectedRemoveItem = selectedRemoveItem;
            $('#removeItem').modal('show');
        };

        $scope.removeItem = function()
        {
            $.each($scope.items, function(index)
            {
                if (this === $scope.selectedRemoveItem)
                {
                    $scope.items.splice(index, 1);
                    return false;
                }
            });
            $('#removeItem').modal('hide');
        };

        $scope.copyItem = function( item )
        {
            $scope.items.push( angular.copy( item ) );
            console.log( item );
        };

        $scope.showConfirmReceiveModal = function()
        {
            $('#confirmReceive').modal('show');
        };
        $scope.confirmReceive = function()
        {
            /* 停止收货时间的实时交互 */
            $interval.cancel( receiveTime );
            $('#confirmReceive').modal('hide');

            $scope.purchaseOrderDelivery.items = $scope.items;

            purchaseOrderDeliveryService.save({
                action: 'create'
            }, $scope.purchaseOrderDelivery, function()
            {
            });

            $timeout(function(){
                $state.go('purchaseOrderDelivery.purchaseOrderDeliveryGenerate');
            }, 200);
        };



        /* 重新构建收货数量 */
        $scope.rebuildQtyNumeric = function( item )
        {
            item.receiveQty = Number( item.receiveQty );
            if( $.isNumeric( item.receiveQty ) )
            {
                item.receiveQty = Number( Math.floor( item.receiveQty ) );

                item.receiveQty = item.receiveQty > 1 ? item.receiveQty : 1;
            }
            else
            {
                item.receiveQty = 1;
            }
        };
        /* 重新构建收货单价 */
        $scope.rebuildPriceNumeric = function( item )
        {
            if( $.isNumeric( item.realPurchaseUnitPrice ) )
            {
                item.realPurchaseUnitPrice = item.realPurchaseUnitPrice > 0 ? item.realPurchaseUnitPrice : 0;
            }
            else
            {
                item.realPurchaseUnitPrice = 0;
            }
        };
    }
]);