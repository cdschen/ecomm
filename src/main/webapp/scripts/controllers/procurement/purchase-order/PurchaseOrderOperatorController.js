var PurchaseOrderOperatorController = function($scope, $rootScope, $state, $stateParams, $filter, toastr, $timeout, $interval, purchaseOrderService, Supplier, Currency, supplierProductService) {

    var t = $.now();

    $scope.template = {
        info: {
            url: 'views/procurement/purchase-order/purchase-order.operator.info.html?' + t
        }
    };

    $scope.holdSelectedSupplier = {};
    $scope.supplierProducts = [];

    $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

    $scope.bookingTypes = [
        { name:'邮件', value:1 },
        { name:'电话', value:2 },
        { name:'Ecomm系统', value:3 },
        { name:'对方系统', value:4 }
    ];

    $scope.init = function()
    {
    };

    Currency.getAll().then(function(currencies) {
        $scope.currencies = currencies;
    });

    Supplier.getAll({
        enabled : true
    }).then(function(suppliers) {
        $scope.suppliers = suppliers;
    });


    function initField(purchaseOrder)
    {
        /* Activate Date Picker */
        if( purchaseOrder.estimateReceiveDate )
        {
            $('input[ng-model="purchaseOrder.estimateReceiveDate"]').datepicker({
                format: 'yyyy-mm-dd',
                clearBtn: true,
                language: 'zh-CN',
                orientation: 'bottom left',
                todayHighlight: true,
                autoclose: true
            }).datepicker('setDate', new Date(purchaseOrder.estimateReceiveDate));
        }

        if( purchaseOrder.bookingType )
        {
            purchaseOrder.bookingType = $scope.bookingTypes[purchaseOrder.bookingType - 1];
        }
        if( purchaseOrder.estimateReceiveDate )
        {
            purchaseOrder.estimateReceiveDate = $filter('date')( purchaseOrder.estimateReceiveDate, 'yyyy-MM-dd' );
        }
    }

    function refreshField(purchaseOrder) {
        purchaseOrder.bookingType = purchaseOrder.bookingType.value;
    }

    $scope.save = function(purchaseOrder, formValid) {

        console.log( purchaseOrder );

        var isQualified = true;

        /* 如果表单验证不通过 */
        if( !formValid )
        {
            //|| !purchaseOrder.supplier || !purchaseOrder.currency
            //|| !purchaseOrder.bookingType || !purchaseOrder.receiveName || !purchaseOrder.receivePhone
            if( !purchaseOrder.supplier )
            {
                toastr.warning('请选择一个［供应商］');
            }
            if( !purchaseOrder.currency )
            {
                toastr.warning('请选择一种［结算货币］');
            }
            if( !purchaseOrder.bookingType )
            {
                toastr.warning('请选择一种［订货方式］');
            }
            if( !purchaseOrder.receiveName )
            {
                toastr.warning('请填写［收货人姓名］');
            }
            if( !purchaseOrder.receivePhone )
            {
                toastr.warning('请填写［收货人电话］');
            }
            isQualified = false;
        }
        /* 如果没有添加采购详情 */
        if( !purchaseOrder.items || purchaseOrder.items.length < 1 )
        {
            toastr.warning('请添加至少一个［采购商品］');
            isQualified = false;
        }
        if( purchaseOrder.receiveEmail && ! echeck( purchaseOrder.receiveEmail ) )
        {
            toastr.warning('［收件人email］邮件格式不正确');
            $('[ng-model="purchaseOrder.receiveEmail"]').css('border', '3px solid red');
            isQualified = false;
        }
        else
        {
            $('[ng-model="purchaseOrder.receiveEmail"]').css('border', '1px solid #CCC');
        }

        /* 如果验证全部通过 */
        if( isQualified )
        {
            //console.clear();
            console.log('[' + $scope.action + '] save purchaseOrder');
            console.log(purchaseOrder);

            refreshField(purchaseOrder);

            purchaseOrder.creator = {
                id : $rootScope.user().id
            };

            purchaseOrder.totalCreditQty = purchaseOrder.totalCreditQty ? purchaseOrder.totalCreditQty : 0;
            purchaseOrder.totalDeliveredQty = purchaseOrder.totalDeliveredQty ? purchaseOrder.totalDeliveredQty : 0;
            purchaseOrder.totalPurcahsedQty = purchaseOrder.totalPurcahsedQty ? purchaseOrder.totalPurcahsedQty : 0;

            purchaseOrderService.save({
                action: $scope.action
            }, purchaseOrder, function(purchaseOrder) {
                console.log('[' + $scope.action + '] save purchaseOrder complete:');
                console.log(purchaseOrder);
                console.log('purchaseOrder.isSupplierProductCodeChanged: ' + purchaseOrder.isSupplierProductCodeChanged);
                if( purchaseOrder.isSupplierProductCodeChanged )
                {
                    toastr.success('供应商编号和采购单价已经自动保存， 下次采购时会自动帮您填');
                }

                toastr.success('成功' + ( $scope.id ? '编辑' : '保存' ) + '［采购单］及［采购单详情］');
                $state.go('purchaseOrder');
            });
        }
    };

    function echeck(str)
    {
        var at='@';
        var dot='.';
        var lat=str.indexOf(at);
        var lstr=str.length;
        console.log(str.indexOf(at)===-1);
        if (str.indexOf(at)===-1)
        {
            return false;
        }

        if (str.indexOf(at)===-1 || str.indexOf(at)===0 || str.indexOf(at)===lstr)
        {
            return false;
        }

        if (str.indexOf(dot)===-1 || str.indexOf(dot)===0 || str.indexOf(dot)===lstr)
        {
            return false;
        }

        if (str.indexOf(at,(lat+1))!==-1)
        {
            return false;
        }

        if (str.substring(lat-1,lat)===dot || str.substring(lat+1,lat+2)===dot)
        {
            return false;
        }

        if (str.indexOf(dot,(lat+2))===-1)
        {
            return false;
        }

        if (str.indexOf(' ')!==-1)
        {
            return false;
        }
        return true;
    }

    $scope.action = 'create';

    if ($stateParams.id && $stateParams.id !== '')
    {
        $scope.id = $stateParams.id;
        $scope.action = 'update';
        purchaseOrderService.get({
            id: $stateParams.id
        }, {}).$promise
            .then(function(purchaseOrder) {
                console.log('[' + $scope.action + '] loading purchaseOrder');
                console.log(purchaseOrder);

                $timeout(function(){
                    initField(purchaseOrder);
                }, 500);

                for( var itemIndex in purchaseOrder.items )
                {
                    purchaseOrder.items[ itemIndex].estimatePurchaseUnitPrice = Number( Math.floor( purchaseOrder.items[ itemIndex].estimatePurchaseUnitPrice ) ).toFixed( 2 );
                }

                $scope.purchaseOrder = purchaseOrder;
                $scope.filterSupplierProduct();
                $scope.holdSelectedSupplier = angular.copy( $scope.purchaseOrder.supplier );

                return purchaseOrder;
            });
    }
    else
    {
        $scope.purchaseOrder = {
            items               :   [],
            estimateReceiveDate :   '',
            companyName         :   'Magic Group',
            receiveName         :   'Candy ZHANG',
            receivePhone        :   '0800 - 999 899  or 09-9729611',
            receiveMobile       :   '027 652 8888',
            receiveEmail        :   'candy@mdd.co.nz',
            receiveAddress      :   'Magic Group Ltd (MDD).   Unit 1, 48 Ellice Road, Wairau Valley, Auckland',
            deliverAttention    :   'Please Deliver between 11am~7pm.   Thanks for your help  :)'
        };

        $timeout(function()
        {
            /* Activate Date Picker */
            $('input[ng-model="purchaseOrder.estimateReceiveDate"]').datepicker({
                format: 'yyyy-mm-dd',
                clearBtn: true,
                language: 'zh-CN',
                orientation: 'bottom left',
                todayHighlight: true,
                autoclose: true
            });
        });
    }

    /*
     切换供应商前准备工作：
     1.
     如果 $scope.purchaseOrder.items.length > 0 =>  则提醒用户［当前供应商采购的产品，确定继续切换供应商？］，如果用户点击［是］，则调用 $scope.filterSupplierProduct();
     否则 => 调用 $scope.filterSupplierProduct();
     */

    $scope.checkChangeSupplier = function()
    {
        if( $scope.purchaseOrder.items && $scope.purchaseOrder.items.length > 0 )
        {
            if( $scope.holdSelectedSupplier.id !== $scope.purchaseOrder.supplier.id )
            {
                $('#changeSupplierConfirmModal').modal('show').on('hidden.bs.modal', function()
                {
                    $timeout(function(){
                        $scope.purchaseOrder.supplier = angular.copy( $scope.holdSelectedSupplier );
                    });
                });
            }
        }
        else
        {
            $scope.changeSupplierConfirm();
        }
    };

    $scope.checkChangeSupplierConfirm = function()
    {
        $scope.changeSupplierConfirm();

        $('#changeSupplierConfirmModal').modal('hide');
    };

    /* 确认切换供应商 */
    $scope.changeSupplierConfirm = function()
    {
        $scope.holdSelectedSupplier = angular.copy( $scope.purchaseOrder.supplier );

        if( $scope.purchaseOrder.items )
        {
            $scope.purchaseOrder.items.length = 0;
        }

        $scope.filterSupplierProduct();
    };

    /** 如果临时采购产品不为空，则是从出库哪里跳转过来的
     */
    if( $stateParams.purchasedProducts && $stateParams.purchasedProducts.length > 0 )
    {
        console.log( '$stateParams.purchasedProducts: ' );
        console.log( $stateParams.purchasedProducts );
        var purchasedProductsStr = $stateParams.purchasedProducts.split(';');
        var finalPurchasedProducts = [];
        for( var purchasedProductsStrIndex in purchasedProductsStr )
        {
            var purchasedProductStr = purchasedProductsStr[ purchasedProductsStrIndex].split(',');
            var purchasedProduct = {
                sku             :    purchasedProductStr[ 0 ],
                purchaseQty     :    purchasedProductStr[ 1 ]
            };
            finalPurchasedProducts.push( purchasedProduct );
        }

        if( ! $scope.purchaseOrder.supplier )
        {
            $scope.purchaseOrder.supplier = {};
        }
        $scope.purchaseOrder.supplier.id = 76;

        $timeout(function()
        {
            $.each( $scope.suppliers, function()
            {
                if( this.id === $scope.purchaseOrder.supplier.id )
                {
                    $scope.purchaseOrder.supplier = angular.copy( this );
                }
            });
            $scope.changeSupplierConfirm();

            $timeout(function()
            {
                $.each( $scope.supplierProducts, function()
                {
                    var supplierProduct = this;
                    $.each( finalPurchasedProducts, function()
                    {
                        if( this.purchaseQty && supplierProduct.product && this.sku === supplierProduct.product.sku )
                        {
                            supplierProduct.purchaseQty = this.purchaseQty;
                            var item =
                            {
                                supplierProduct : supplierProduct,
                                purchaseQty : supplierProduct.purchaseQty,
                                estimatePurchaseUnitPrice : supplierProduct.defaultPurchasePrice
                            };
                            $scope.purchaseOrder.items.push( item );
                        }
                    });
                });
            }, 300);

            console.log( '$scope.purchaseOrder: ' );
            console.log( $scope.purchaseOrder );

        }, 200);
    }

    /* 模糊搜索：延迟 500 毫秒，相同模糊匹配关键词则不进行搜索 */
    $scope.queryPurchaseOrderItemFuzzySearchParamHold = '';
    $scope.delayFuzzySearch = function()
    {
        if( $scope.queryPurchaseOrderItemFuzzySearchParam !== $scope.queryPurchaseOrderItemFuzzySearchParamHold )
        {
            if( $scope.isSupplierSelected() )
            {
                $timeout.cancel( $scope.delayFuzzySearchTimeout );

                $scope.delayFuzzySearchTimeout = $timeout(function()
                {
                    $scope.filterSupplierProduct();
                }, 500);
            }
        }
        $scope.queryPurchaseOrderItemFuzzySearchParamHold = $scope.queryPurchaseOrderItemFuzzySearchParam;
    };

    $scope.filterSupplierProduct = function()
    {
        supplierProductService.get( getQueryParamJSON(), function(page)
        {
            $scope.supplierProducts = page.content;

            for( var supplierProductIndex in $scope.supplierProducts )
            {
                $scope.supplierProducts[supplierProductIndex].purchaseQty = 1;
                $scope.supplierProducts[supplierProductIndex].defaultPurchasePrice = Number( $scope.supplierProducts[supplierProductIndex].defaultPurchasePrice ).toFixed( 2 );
            }
        });
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

    /* 查询采购单分页数据所需查询参数 */
    function getQueryParamJSON()
    {
        return {
            page: 0,
            size: 100,
            sort: ['supplierProductName,desc'],
            queryPurchaseOrderItemFuzzySearchParam: $scope.queryPurchaseOrderItemFuzzySearchParam ? $scope.queryPurchaseOrderItemFuzzySearchParam : null,
            querySupplierId: $scope.purchaseOrder.supplier ? $scope.purchaseOrder.supplier.id : null
        };
    }
    /* 检查是否选择供应商 */
    $scope.isSupplierSelected = function()
    {
        var isPass = true;
        if( ! $scope.purchaseOrder.supplier )
        {
            toastr.warning('请选择一个［供应商］来继续');
            isPass = false;
        }
        return isPass;
    };


    /*
         添加［供应商产品］至 $scope.purchaseOrder.items
         如果［供应商产品］编号存在［采购单详情］供应商产品 ID 中：
     */

    $scope.addSupplierProductToPurchaseOrderItemMouseHeld = function( supplierProduct )
    {
        $scope.addSupplierProductToPurchaseOrderItemMouseHeldInterval = $interval(function(){
            $scope.addSupplierProductToPurchaseOrderItem( supplierProduct );
        }, 60);
    };
    $scope.addSupplierProductToPurchaseOrderItemMouseRelease = function()
    {
        $interval.cancel( $scope.addSupplierProductToPurchaseOrderItemMouseHeldInterval );
    };
    $scope.addSupplierProductToPurchaseOrderItem = function( supplierProduct )
    {
        var item =
        {
            supplierProduct : supplierProduct,
            purchaseQty : supplierProduct.purchaseQty,
            estimatePurchaseUnitPrice : supplierProduct.defaultPurchasePrice
        };

        var isExisted = false;

        if( $scope.purchaseOrder.items.length > 0 )
        {
            $.each($scope.purchaseOrder.items, function()
            {
                this.estimatePurchaseUnitPrice = Number( this.estimatePurchaseUnitPrice).toFixed( 2 );
                supplierProduct.defaultPurchasePrice = Number( supplierProduct.defaultPurchasePrice).toFixed( 2 );

                if
                (
                    this.supplierProduct.id === supplierProduct.id &&
                    this.estimatePurchaseUnitPrice === supplierProduct.defaultPurchasePrice
                )
                {
                    this.purchaseQty =  Number( this.purchaseQty ) + Number( supplierProduct.purchaseQty );
                    isExisted = true;
                }
                else if( this.supplierProduct.supplierProductCode === supplierProduct.supplierProductCode )
                {
                    toastr.warning('该［产品］的供应商产品编号已存在于［采购产品］列表中，请不要添加价格不一样的同［供应商编号］的［产品］，因为无法累加');
                    isExisted = true;
                }

            });
        }

        if( ! isExisted )
        {
            $scope.purchaseOrder.items.push( item );
        }
    };

    $('#orderTabs a').click(function(e) {
        e.preventDefault();
        $(this).tab('show');
    });

};

PurchaseOrderOperatorController.$inject = ['$scope', '$rootScope', '$state', '$stateParams', '$filter', 'toastr', '$timeout', '$interval', 'purchaseOrderService', 'Supplier', 'Currency', 'supplierProductService'];

angular.module('ecommApp').controller('PurchaseOrderOperatorController', PurchaseOrderOperatorController);