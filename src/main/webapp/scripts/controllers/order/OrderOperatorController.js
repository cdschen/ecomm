
var OrderOperatorController = function($scope, $state, $stateParams, orderService, Shop, Currency) {

    console.clear();
    var $ = angular.element;
    $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

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

    Shop.getAll().then(function(shops) {
        $scope.shops = shops;
    });

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