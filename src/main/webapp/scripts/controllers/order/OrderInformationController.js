
var OrderInformationController = function($scope, $state, $stateParams) {

    $scope.prices = [
        { name:'价格1', value:'priceL1' },
        { name:'价格2', value:'priceL2' },
        { name:'价格3', value:'priceL3' },
        { name:'价格4', value:'priceL4' },
        { name:'价格5', value:'priceL5' },
        { name:'价格6', value:'priceL6' },
        { name:'价格7', value:'priceL7' },
        { name:'价格8', value:'priceL8' },
        { name:'价格9', value:'priceL9' },
        { name:'价格10', value:'priceL10' }
    ];

    if ($stateParams.id && $stateParams.id !== '') {
        $scope.id = $stateParams.id;
    }
    else
    {
        $scope.order = {};
        $scope.order.items = [];
    }

    // Product.getAll().then(function(products) {
    //     $scope.products = products;
    // });

    $scope.changeShop = function(shop)
    {
        $scope.order.currency = angular.copy(shop.currency);
    };

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
        var selectedPrice = selectedProduct[selectedProduct.selectedPrice.value];

        for(var existedItem in $scope.order.items)
        {
            console.log(selectedProduct);
            console.log($scope.order.items[existedItem]);
            if(selectedProduct.sku === $scope.order.items[existedItem].sku &&
                selectedPrice === $scope.order.items[existedItem].unitPrice)
            {
                $scope.order.items[existedItem].qtyOrdered += selectedProduct.qtyOrdered;
                isRepeated = true;
            }
        }

        if( ! isRepeated )
        {
            var item = {
                product: {
                    id: selectedProduct.id
                },
                'externalSku': selectedProduct.sku,
                'sku': selectedProduct.sku,
                'external_name': selectedProduct.name,
                'name': selectedProduct.name,
                'unitWeight': selectedProduct.weight,
                'qtyOrdered': selectedProduct.qtyOrdered,
                'unitPrice': selectedPrice,
                'unitGst': selectedPrice * 3 / 23
            };

            $scope.order.items.push(item);
        }
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
            $scope.order.items.splice($scope.removingItem.$index, 1);
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

OrderInformationController.$inject = ['$scope', '$state', '$stateParams'];

angular.module('ecommApp').controller('OrderInformationController', OrderInformationController);