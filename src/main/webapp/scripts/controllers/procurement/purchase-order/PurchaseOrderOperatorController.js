var PurchaseOrderOperatorController = function($scope, $rootScope, $state, $stateParams, $filter, toastr, purchaseOrderService, Supplier, Currency) {

    var t = $.now(); 

    $scope.template = {
        info: {
            url: 'views/procurement/purchase-order/purchase-order.operator.info.html?' + t,
            items: {
                url: 'views/procurement/purchase-order/purchase-order.operator.info.items.html?' + t
            }
        }
    };

    $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

    $scope.bookingTypes = [
        { name:'邮件', value:1 },
        { name:'电话', value:2 },
        { name:'Ecomm系统', value:3 },
        { name:'对方系统', value:4 }
    ];

    $scope.init = function()
    {
        /* Activate Date Picker */
        $('input[ng-model="purchaseOrder.estimateReceiveDate"]').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'top left',
            todayHighlight: true,
            autoclose: true
        });
        if( $scope.purchaseOrder )
        {
            $('input[ng-model="purchaseOrder.estimateReceiveDate"]').datepicker('setDate', $scope.purchaseOrder.estimateReceiveDate);
        }
    };

    Currency.getAll().then(function(currencies) {
        $scope.currencies = currencies;
    });

    Supplier.getAll({
        deleted : 0
    }).then(function(suppliers) {
        $scope.suppliers = suppliers;
    });


    function initField(purchaseOrder)
    {
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
                $state.go('purchaseOrder');
            });
        }
    };

    $scope.action = 'create';

    if ($stateParams.id && $stateParams.id !== '') {
        $scope.action = 'update';
        purchaseOrderService.get({
            id: $stateParams.id
        }, {}).$promise
            .then(function(purchaseOrder) {
                console.log('[' + $scope.action + '] loading purchaseOrder');
                console.log(purchaseOrder);
                $scope.purchaseOrder = purchaseOrder;

                initField(purchaseOrder);
                return purchaseOrder;
            });
    }

    $('#orderTabs a').click(function(e) {
        e.preventDefault();
        $(this).tab('show');
    });
};

PurchaseOrderOperatorController.$inject = ['$scope', '$rootScope', '$state', '$stateParams', '$filter', 'toastr', 'purchaseOrderService', 'Supplier', 'Currency'];

angular.module('ecommApp').controller('PurchaseOrderOperatorController', PurchaseOrderOperatorController);