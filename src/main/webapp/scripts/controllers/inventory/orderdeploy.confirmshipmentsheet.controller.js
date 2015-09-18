angular.module('ecommApp')

.controller('ConfirmShipmentSheetController', ['$scope', 'orderService',
    function($scope, orderService) {

        $scope.operateDate = Date.now();
        $scope.operationReview = orderService.getOperationReview;
        $scope.defaultHeight = function(){
            return {
                height: $(window).height() / 2
            };
        };

    }
]);