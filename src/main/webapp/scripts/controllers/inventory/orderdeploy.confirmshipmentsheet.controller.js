angular.module('ecommApp')

.controller('ConfirmShipmentSheetController', ['$scope',
    function($scope) {

        $scope.init = function() {
            $scope.defaultHeight = {
                height: $(window).height()/2
            };
        };

    }
]);
