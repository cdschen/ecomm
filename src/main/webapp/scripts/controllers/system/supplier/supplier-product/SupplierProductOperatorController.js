var SupplierProductOperatorController = function($scope, $rootScope, $state, $stateParams, $filter, toastr, supplierProductService, Supplier, Product) {

    var t = $.now();

    $scope.template = {
        info: {
            url: 'views/system/supplier/supplier-product/supplier-product.operator.info.html?' + t
        }
    };

    $scope.supplierProduct = {
        creator : $rootScope.user()
    };

    $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';


    $scope.getProduct = function(val) {
        return Product.get({
            page: 0,
            size: 30,
            enabled: true,
            nameOrSku: val
        }).$promise.then(function(page) {
                return page.content;
            });
    };
    Product.getAll().then(function(products) {
        $scope.products = products;
    });

    Supplier.getAll({
        deleted : 0
    }).then(function(suppliers) {
        $scope.suppliers = suppliers;
    });

    $scope.save = function( supplierProduct , formValid)
    {
        var isQualified = true;

        if( ! formValid )
        {
            if( !supplierProduct.supplierProductCode )
            {
                toastr.warning('请填写［供应商产品编号］');
            }
            if( !supplierProduct.supplierProductName )
            {
                toastr.warning('请填写［供应商产品名称］');
            }
            if( !supplierProduct.supplier )
            {
                toastr.warning('请选择一个［供应商］');
            }
            isQualified = false;
        }

        if( isQualified )
        {

            console.log(supplierProduct);
            supplierProductService.save({
                action: $scope.action
            }, supplierProduct, function( supplierProduct ) {
                console.log('[' + $scope.action + '] save supplierProduct complete:');
                console.log( supplierProduct );
                $state.go('supplierProduct');
            });
        }
    };

    /* 重新构建［价格］ */
    $scope.rebuildPriceNumeric = function( product, field )
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

    $scope.action = 'create';

    if ($stateParams.id && $stateParams.id !== '') {
        $scope.action = 'update';
        supplierProductService.get({
            id: $stateParams.id
        }, {}).$promise
            .then(function( supplierProduct ) {
                console.log('[' + $scope.action + '] loading supplierProduct');
                console.log( supplierProduct );
                $scope.supplierProduct = supplierProduct;

                console.log( supplierProduct );

                return supplierProduct;
            });
    }

    $('#supplierProductTabs a').click(function(e) {
        e.preventDefault();
        $(this).tab('show');
    });
};

SupplierProductOperatorController.$inject = ['$scope', '$rootScope', '$state', '$stateParams', '$filter', 'toastr', 'supplierProductService', 'Supplier', 'Product'];

angular.module('ecommApp').controller('SupplierProductOperatorController', SupplierProductOperatorController);