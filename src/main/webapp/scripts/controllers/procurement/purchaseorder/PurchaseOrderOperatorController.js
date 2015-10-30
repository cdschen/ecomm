
var PurchaseOrderOperatorController = function($scope, $rootScope, $state, $stateParams, $filter, toastr, purchaseOrderService, Supplier, Currency) {

    $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

    $scope.bookingTypes = [
        { name:'邮件', value:1 },
        { name:'电话', value:2 },
        { name:'Ecomm系统', value:3 },
        { name:'对方系统', value:4 }
    ];

    $scope.template = {
        info: {
            url: 'views/procurement/purchaseorder/purchaseorder.operator.info.html?' + new Date(),
            items: {
                url: 'views/procurement/purchaseorder/purchaseorder.operator.info.items.html?' + new Date()
            }
        }
    };

    $scope.init = function()
    {
        /* Activate Date Picker */
        $('input[ng-model="purchaseOrder.estimateReceiveDate"]').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'top left',
            todayHighlight: true
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

    $scope.save = function(purchaseOrder) {
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
            $state.go('purchaseorder');
        });
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