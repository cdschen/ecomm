
var OrderDetailController = function($scope) {
    var $ = angular.element;

    $scope.initOrderDetailTabs = function() {
        $('#orderDetailTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    };

};

OrderDetailController.$inject = ['$scope'];

angular.module('ecommApp').controller('OrderDetailController', OrderDetailController);