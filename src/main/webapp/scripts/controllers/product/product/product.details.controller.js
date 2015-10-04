angular.module('ecommApp')

.controller('ProductDetailsController', ['$scope', function($scope) {

    var $ = angular.element;

    $scope.initProductDetailsTabs = function() {
        $('#productDetailsTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    };

}]);
