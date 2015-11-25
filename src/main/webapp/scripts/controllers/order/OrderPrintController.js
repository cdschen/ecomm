
var OrderPrintController = function($scope, $location, orderService, Utils)
{

    $scope.defaultQuery = {
        orderIds    :   []
    };
    $scope.query = angular.copy($scope.defaultQuery);

    $scope.searchData = function(query, number)
    {
        var search = $location.search();
        var orderId = search.orderId;
        query.orderIds.push( orderId );

        orderService.get({
            page: number ? number : 0,
            orderIds: query.orderIds
        }, function(page) {
            $scope.page = page;
            query.totalPagesList = Utils.setTotalPagesList(page);
        });
    };

    $scope.searchData($scope.query);

};

OrderPrintController.$inject = ['$scope', '$location', 'orderService', 'Utils'];

angular.module('ecommApp').controller('OrderPrintController', OrderPrintController);