
var OrderPrintController = function($scope, $rootScope, $location, $interval, toastr, orderService, Utils)
{

    $scope.defaultQuery = {
        orderIds    :   []
    };
    $scope.query = angular.copy($scope.defaultQuery);

    $scope.operator = $rootScope.user();


    function updatePrintTime() {
        $scope.printTime = new Date();
    }

    var printTimeInterval = $interval( updatePrintTime, 500 );

    $scope.$on('$destroy', function(){
        $interval.cancel( printTimeInterval );
    });

    $scope.searchData = function(query, number)
    {
        var search = $location.search();
        var orderId = search.orderId;
        //var orderIds = search.orderIds;


        //if( orderId )
        //{
        //    query.orderIds.push( orderId );
        //}
        if( orderId )
        {
            query.orderIds = orderId.split(',');
        }

        if( query.orderIds.length > 0 )
        {
            orderService.get({
                page: number ? number : 0,
                orderIds: query.orderIds
            }, function(page) {
                $scope.page = page;
                query.totalPagesList = Utils.setTotalPagesList(page);
                toastr.success('配货单准备就绪，可以进行打印操作');
            });
        }
    };

    $scope.searchData($scope.query);

};

OrderPrintController.$inject = ['$scope', '$rootScope', '$location', '$interval', 'toastr', 'orderService', 'Utils'];

angular.module('ecommApp').controller('OrderPrintController', OrderPrintController);