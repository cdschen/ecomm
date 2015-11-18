
var OrderOperatorController = function($scope, $state, $stateParams, toastr, orderService, Shop, Currency) {

    console.clear();
    var $ = angular.element;
    $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

    $scope.deliveryMethods = [
        { name:'快递', value:1 },
        { name:'自提', value:2 },
        { name:'送货上门', value:3 }
    ];

    $scope.template = {
        info: {
            url: 'views/order/order.operator.info.html?' + new Date(),
            items: {
                url: 'views/order/order.operator.info.items.html?' + new Date()
            }
        }
    };

    Currency.getAll().then(function(currencies) {
        $scope.currencies = currencies;
    });


    function initField(order) {
        order.deliveryMethod = $scope.deliveryMethods[order.deliveryMethod - 1];
    }

    function refreshField(order) {
        if( order.deliveryMethod )
        {
            order.deliveryMethod = order.deliveryMethod.value;
        }
    }

    Shop.getAll().then(function(shops) {
        $scope.shops = shops;
    });

    $scope.save = function(order, formValid)
    {
        var isQualified = true;
        //!order.shop || !order.externalSn
        //|| !order.shippingFee || !order.receiveName || !order.receivePhone || !order.receiveAddress
        if( ! formValid )
        {
            if( !order.shop )
            {
                toastr.warning('请选择一个［店铺］');
            }
            if( !order.externalSn )
            {
                toastr.warning('请填写［店铺订单号］');
            }
            if( !order.shippingFee )
            {
                toastr.warning('请填写［运费金额］');
            }
            if( !order.receiveName )
            {
                toastr.warning('请填写［收件人姓名］');
            }
            if( !order.receivePhone )
            {
                toastr.warning('请填写［收件人电话］');
            }
            if( !order.receiveAddress )
            {
                toastr.warning('请填写［收件地址］');
            }
            isQualified = false;
        }
        if( order.receiveEmail && ! echeck( order.receiveEmail ) )
        {
            toastr.warning('［收件人email］邮件格式不正确');
            $('[ng-model="order.receiveEmail"]').css('border', '3px solid red');
            isQualified = false;
        }
        else
        {
            $('[ng-model="order.receiveEmail"]').css('border', '1px solid #CCC');
        }
        if( order.senderEmail && ! echeck( order.senderEmail ) )
        {
            toastr.warning('［发件人email］邮件格式不正确');
            $('[ng-model="order.senderEmail"]').css('border', '3px solid red');
            isQualified = false;
        }
        else
        {
            $('[ng-model="order.senderEmail"]').css('border', '1px solid #CCC');
        }
        /* 如果没有添加订购详情 */
        if( !order.items || order.items.length < 1 )
        {
            toastr.warning('请添加至少一个［订购商品］');
            isQualified = false;
        }

        if( isQualified )
        {
            refreshField(order);

            orderService.save({
                action: $scope.action
            }, order, function()
            {
                if ($stateParams.id && $stateParams.id !== '')
                {
                    toastr.success('编辑订单成功');
                }
                else
                {
                    toastr.success('创建订单成功');
                }
                $state.go('order');
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

    if ($stateParams.id && $stateParams.id !== '') {
        $scope.action = 'update';
        orderService.get({
            id: $stateParams.id
        }, {}).$promise
            .then(function(order) {
                console.log('[' + $scope.action + '] loading order');
                console.log(order);
                $scope.order = order;
                console.log(order.shop);

                if(order.deliveryMethod)
                {
                    initField(order);
                }
                return order;
            });
    }

    $('#orderTabs a').click(function(e) {
        e.preventDefault();
        $(this).tab('show');
    });
};

OrderOperatorController.$inject = ['$scope', '$state', '$stateParams', 'toastr', 'orderService', 'Shop', 'Currency'];

angular.module('ecommApp').controller('OrderOperatorController', OrderOperatorController);