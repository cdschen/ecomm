var PurchaseOrderOperatorInformationController = function($scope, $state, $stateParams, toastr) {


    $scope.defaultSupplierProduct =
    {
        supplierProductName : '',
        supplierProductCode : '',
        purchaseQty : 1,
        defaultPurchasePrice : '0.00'
    };
    $scope.newSupplierProduct = angular.copy( $scope.defaultSupplierProduct );



    /**
     * 功能函数：起始
     */



    /*
        添加新的［供应商产品］至 $scope.purchaseOrder.items
     */
    $scope.addNewSupplierProductToPurchaseOrderItem = function()
    {
        if( $scope.isSupplierSelected() )
        {
            var isQualified = true;

            if( ! $scope.newSupplierProduct.supplierProductName )
            {
                toastr.warning('请填写［供应商产品名称］');
                isQualified = false;
            }
            if( ! $scope.newSupplierProduct.supplierProductCode )
            {
                toastr.warning('请填写［供应商产品编号］');
                isQualified = false;
            }

            if( ! isNewSupplierProductCodeExistedInBothPlace( $scope.newSupplierProduct ) )
            {
                if( isQualified )
                {
                    var item =
                    {
                        supplierProduct : angular.copy( $scope.newSupplierProduct ),
                        purchaseQty : $scope.newSupplierProduct.purchaseQty,
                        estimatePurchaseUnitPrice : $scope.newSupplierProduct.defaultPurchasePrice
                    };

                    $scope.purchaseOrder.items.push( item );

                    toastr.success('成功添加：［' + item.supplierProduct.supplierProductName + '］ 至［采购产品］列表中');
                }
                $scope.newSupplierProduct = angular.copy( $scope.defaultSupplierProduct );
            }
        }
    };

    /*
         移除 $scope.purchaseOrder.items 里的［item］
     */
    $scope.removeItemFromPurchaseOrderItems = function( item )
    {
        $.each($scope.purchaseOrder.items, function(index)
        {
            if( this === item )
            {
                $scope.purchaseOrder.items.splice(index, 1);
                return false;
            }
        });

    };



    /**
     * 辅助函数：起始
     */

    /*
        检查该［当前供应商新品］是否存在于：
            1. ［当前供应商产品］列表
            2. ［采购产品］列表
     */
    function isNewSupplierProductCodeExistedInBothPlace( newSupplierProduct )
    {
        var isPass = false;
        if( $scope.supplierProducts.length > 0 )
        {
            $.each( $scope.supplierProducts, function()
            {
                /* 检查［当前供应商产品］列表 */
                if( this.supplierProductCode === newSupplierProduct.supplierProductCode )
                {
                    toastr.warning('该新品的供应商产品编号已存在于［当前供应商产品］列表中，请用其他编号');
                    isPass = true;
                }
            });
        }

        if( $scope.purchaseOrder.items.length > 0 )
        {
            $.each( $scope.purchaseOrder.items, function()
            {
                /* 检查［当前供应商产品］列表 */
                if( this.supplierProduct.supplierProductCode === newSupplierProduct.supplierProductCode )
                {
                    toastr.warning('该新品的供应商产品编号已存在于［采购产品］列表中，请用其他编号');
                    isPass = true;
                }
            });
        }
        return isPass;
    }


    /* 重新构建［供应商产品］采购数量 */
    $scope.rebuildPurchaseQtyNumeric = function( supplierProduct )
    {
        if( $.isNumeric( supplierProduct.purchaseQty ) )
        {
            supplierProduct.purchaseQty = Number( Math.floor( supplierProduct.purchaseQty ) ).toFixed( 0 );

            supplierProduct.purchaseQty = supplierProduct.purchaseQty > 1 ? supplierProduct.purchaseQty : 1;
        }
        else
        {
            supplierProduct.purchaseQty = 1;
        }
    };
    /* 重新构建［采购价格］ */
    $scope.rebuildPurchasePriceNumeric = function( product, field )
    {
        product[ field ] = Number( product[ field ] );
        if( $.isNumeric( product[ field ] ) )
        {
            product[ field ] = product[ field ] > 0 ? product[ field ] : 0.00;

            product[ field ] = Number( product[ field ] ).toFixed( 2 );
        }
        else
        {
            product[ field ] = 0.00;
        }
    };



};

PurchaseOrderOperatorInformationController.$inject = ['$scope', '$state', '$stateParams', 'toastr', '$timeout', 'supplierProductService'];

angular.module('ecommApp').controller('PurchaseOrderOperatorInformationController', PurchaseOrderOperatorInformationController);