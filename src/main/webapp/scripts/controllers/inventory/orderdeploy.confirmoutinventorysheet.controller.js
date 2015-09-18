angular.module('ecommApp')

.controller('ConfirmOutInventorySheetController', ['$scope', 'orderService',
    function($scope, orderService) {

        var $ = angular.element;
        $scope.operateDate = Date.now();
        $scope.operationReview = orderService.getOperationReview;
        $scope.defaultHeight = function(){
            return {
                height: $(window).height() / 2
            };
        };

    }
]);
