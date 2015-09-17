angular.module('ecommApp')

.controller('ConfirmOutInventorySheetController', ['$scope', 'orderService',
    function($scope, orderService) {

        $scope.differnetWarehouseErrorOrders = orderService.selectedOrders;

        $scope.init = function() {
            $scope.defaultHeight = {
                height: $(window).height()/2
            };
        };

    }
]);
