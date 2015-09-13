
var OrderOperatorController = function($scope, $state, $stateParams, orderService, Shop, Currency) {

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
        order.deliveryMethod = order.deliveryMethod.value;
    }

    Shop.getAll().then(function(shops) {
        $scope.shops = shops;
    });

    $scope.save = function(order) {
        //console.clear();
        console.log('[' + $scope.action + '] save order');
        console.log(order);

        refreshField(order);

        orderService.save({
            action: $scope.action
        }, order, function(order) {
            console.log('[' + $scope.action + '] save order complete:');
            console.log(order);
            $state.go('order');
        });
    };

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

OrderOperatorController.$inject = ['$scope', '$state', '$stateParams', 'orderService', 'Shop', 'Currency'];

angular.module('ecommApp').controller('OrderOperatorController', OrderOperatorController);