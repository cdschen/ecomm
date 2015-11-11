var PurchaseOrderOperatorInformationController = function($scope, $state, $stateParams, purchaseOrderService, Product) {

    if ($stateParams.id && $stateParams.id !== '') {
        $scope.id = $stateParams.id;
    }
    else
    {
        $scope.purchaseOrder = {
            items:[]
        };
    }

    Product.getAll().then(function(products) {
        $scope.products = products;
    });

    /**
     * BEGIN Items Area
     */
    $scope.pushItem = function(itemAddForm, selectedProduct) {
        //console.clear();
        console.log('[' + $scope.action + '] saveItem complete:');
        //console.log('selectedProduct: ');
        //console.log(selectedProduct);
        //console.log('selectedProduct.selectedPrice: ');
        //console.log(selectedProduct.selectedPrice);

        var isRepeated = false;
        var selectedPrice = selectedProduct.selectedPrice;

        console.log('selectedProduct:');
        console.log(selectedProduct);

        for(var existedItem in $scope.purchaseOrder.items)
        {
            //console.log('selectedProduct.id: ' + selectedProduct.id);
            //console.log('$scope.purchaseOrder.items[existedItem].productId: ' + $scope.purchaseOrder.items[existedItem].product.id);
            //console.log('selectedPrice: ' + selectedPrice);
            //console.log('$scope.purchaseOrder.items[existedItem].estimatePurchaseUnitPrice: ' + $scope.purchaseOrder.items[existedItem].estimatePurchaseUnitPrice);

            //console.log(selectedProduct);
            //console.log($scope.purchaseOrder.items[existedItem]);
            if(selectedProduct.id === $scope.purchaseOrder.items[existedItem].product.id &&
                selectedPrice === $scope.purchaseOrder.items[existedItem].estimatePurchaseUnitPrice)
            {

                console.log('$scope.purchaseOrder.items[existedItem]');
                console.log($scope.purchaseOrder.items[existedItem]);
                $scope.purchaseOrder.items[existedItem].purchaseQty += selectedProduct.purchaseQty;
                $scope.purchaseOrder.items[existedItem].supplierProductCodeMap = {
                    supplierProductCode : $scope.purchaseOrder.supplierProductCode
                };
                isRepeated = true;
            }
        }

        if( ! isRepeated )
        {
            var item = {
                product: {
                    id: selectedProduct.id,
                    externalSku: selectedProduct.externalSku,
                    sku: selectedProduct.sku,
                    externalName: selectedProduct.externalName,
                    name: selectedProduct.name,
                    unitWeight: selectedProduct.unitWeight,
                    unitCost: selectedProduct.unitCost,
                    unitGst: selectedProduct.unitGst
                },
                supplierProductCodeMap: {
                    supplierProductCode : $scope.purchaseOrder.supplierProductCode
                },
                purchaseQty: selectedProduct.purchaseQty,
                estimatePurchaseUnitPrice: selectedProduct.selectedPrice
            };

            console.log('item');
            console.log(item);

            //console.log(item);
            //
            //console.log($scope.purchaseOrder);

            $scope.purchaseOrder.items.push(item);
        }
    };

    /* 监听 $scope.purchaseOrder 对象的任何一个属性的改动 */
    $scope.$watch('purchaseOrder', function()
    {
        if( $scope.purchaseOrder )
        {
            var items = $scope.purchaseOrder.items;
            var totalPurchasedQty = 0;
            var totalEstimatePurchasedAmount = 0;
            if( items )
            {
                for( var itemIndex in items )
                {
                    totalPurchasedQty += items[itemIndex].purchaseQty;
                    totalEstimatePurchasedAmount += ( items[itemIndex].purchaseQty * items[itemIndex].estimatePurchaseUnitPrice );
                }
                $scope.purchaseOrder.totalPurchasedQty = totalPurchasedQty;
                $scope.purchaseOrder.totalEstimatePurchasedAmount = totalEstimatePurchasedAmount;
            }
        }
    }, true);

    $scope.checkSupplierProductCode = function()
    {
        if( $scope.purchaseOrder.supplierProductCode )
        {
            purchaseOrderService.getSupplierProductCodeMapBySupplierProductCode( $scope.purchaseOrder.supplierProductCode ).then(function(supplierProductCodeMap) {
                //console.log('supplierProductCodeMap');
                //console.log(supplierProductCodeMap);
                if( supplierProductCodeMap )
                {
                    $scope.purchaseOrder.selectedProduct = supplierProductCodeMap.product;
                    $scope.purchaseOrder.selectedProduct.selectedPrice = supplierProductCodeMap.defaultPurchasePrice;
                    $scope.purchaseOrder.supplierProductCode = supplierProductCodeMap.supplierProductCode;
                }
            });
        }
    };

    $scope.checkSelectedProductFromSupplierProductCode = function()
    {
        purchaseOrderService.getSelectedProductFromSupplierProductCode( $scope.purchaseOrder.selectedProduct.id ).then(function(supplierProductCodeMap) {
            //console.log('supplierProductCodeMap');
            //console.log(supplierProductCodeMap);
            if( supplierProductCodeMap )
            {
                $scope.purchaseOrder.selectedProduct = supplierProductCodeMap.product;
                $scope.purchaseOrder.selectedProduct.selectedPrice = supplierProductCodeMap.defaultPurchasePrice;
                $scope.purchaseOrder.supplierProductCode = supplierProductCodeMap.supplierProductCode;
            }
        });
    };

    $scope.removingItem = undefined;

    $scope.showRemoveItem = function(item, $index) {
        console.clear();
        console.log('showRemoveItem $index: ' + $index);
        console.log(item);

        $scope.removingItem = item;
        $scope.removingItem.$index = $index;
        $('#itemDeleteModal').modal('show');
    };

    $scope.removeItem = function() {
        console.clear();
        console.log('removeItem:');

        if (angular.isDefined($scope.removingItem)) {
            $scope.purchaseOrder.items.splice($scope.removingItem.$index, 1);
            $('#itemDeleteModal').modal('hide');
        }
    };


    $scope.updateItem = function(item) {
        //console.clear();
        console.log('updateItem:');
        console.log(item);
        item.editable = true;
    };

    $scope.saveUpdateItem = function(item, itemForm) {
        //console.clear();
        console.log('[' + $scope.action + '] saveUpdateItem complete:');
        console.log(item);
        item.editable = false;
        itemForm.$setPristine();

    };
    /**
     * BEGIN Items Area
     */

};

PurchaseOrderOperatorInformationController.$inject = ['$scope', '$state', '$stateParams', 'purchaseOrderService', 'Product'];

angular.module('ecommApp').controller('PurchaseOrderOperatorInformationController', PurchaseOrderOperatorInformationController);