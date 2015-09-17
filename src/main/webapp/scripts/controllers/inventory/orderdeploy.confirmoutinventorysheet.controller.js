angular.module('ecommApp')

.controller('ConfirmOutInventorySheetController', ['$scope', 'orderService',
    function($scope, orderService) {

    	$scope.operateDate = Date.now();


        $scope.differnetWarehouseErrorOrders = orderService.selectedOrders;

        $scope.init = function() {

        	
            $scope.defaultHeight = {
                height: $(window).height()/2
            };
        };

    }
]);
