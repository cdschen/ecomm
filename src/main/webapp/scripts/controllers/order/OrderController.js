
var OrderController = function($scope, $location, toastr, orderService, Utils, Process, ObjectProcess, Shop, Auth)
{

    /* Activate Date Picker */
    $('input[ng-model="query.order.internalCreateTimeStart"], input[ng-model="query.order.internalCreateTimeEnd"], ' +
        'input[ng-model="query.order.shippingTimeStart"], input[ng-model="query.order.shippingTimeEnd"]').datepicker({
        format: 'yyyy-mm-dd',
        clearBtn: true,
        language: 'zh-CN',
        orientation: 'bottom left',
        todayHighlight: true
    });

    $scope.template = {
        detail: {
            url: 'views/order/order.detail.html?' + (new Date())
        },
        process: {
            url: 'views/order/order.process.html?' + (new Date())
        },
        status: {
            url: 'views/order/order.status.html?' + (new Date())
        },
        items: {
            url: 'views/product/order.operator.info.items.html?' + new Date()
        },
        popover: {
            url: 'process-tmpl.html'
        }
    };

    $scope.isCheckedAll = false;
    $scope.batchManipulationValue = 'batchManipulation';

    $scope.checkAllOrders = function()
    {
        for( var orderIndex in $scope.page.content )
        {
            $scope.page.content[ orderIndex ].isSelected = $scope.isCheckedAll;
        }
    };

    $scope.defaultQuery = {
        size: 20,
        totalPagesList: [],
        sort: ['internalCreateTime,desc'],
        order: {},
        shop: {},
        status: []
    };
    $scope.query = angular.copy($scope.defaultQuery);

    $scope.shops = [];
    $scope.processes = [];

    $scope.detailsSlideChecked = false;
    $scope.processSlideChecked = false;
    $scope.statusSlideChecked = false;

    Shop.getAll({
        deleted: false,
        sort: ['name'],
        shopIds: Auth.refreshManaged('shop')
    }).then(function(shops) {
        $scope.shops = shops;
    });

    Process.getAll({
        deleted: false,
        objectType: 1
    }).then(function(processes) {
        $scope.processes = processes;
        console.log('processes');
        console.log(processes);
        Process.initStatus(processes);
    }).then(function() {
        $scope.searchData( $scope.query, $scope.number );
    });

    $scope.searchData = function(query, number)
    {
        $scope.number = number;
        orderService.get({
            page: number ? number : 0,
            size: query.size,
            sort: query.sort,
            orderId: query.order.orderId,
            shipNumber: query.order.shipNumber,
            shopId: query.shop.selected ? query.shop.selected.id : null,
            shopIds: Auth.refreshManaged('shop'),
            receiveName: query.order.receiveName,
            internalCreateTimeStart: query.order.internalCreateTimeStart,
            internalCreateTimeEnd: query.order.internalCreateTimeEnd,
            shippingTimeStart: query.order.shippingTimeStart,
            shippingTimeEnd: query.order.shippingTimeEnd,
            statusIds: Process.refreshStatus(query.status)
        }, function(page) {
            $scope.page = page;
            console.log(page.content);
            console.log( page );
            Utils.initList(page, $scope.query);
            console.log( $scope.query );
        });
    };

    $scope.turnPage = function(number) {
        if (number > -1 && number < $scope.page.totalPages) {
            $scope.searchData($scope.query, number);
        }
    };

    $scope.search = function(query) {
        $scope.searchData(query);
    };

    $scope.reset = function() {
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.searchData($scope.query);
    };

    // status
    $scope.toggleStatusSlide = function() {
        $scope.statusSlideChecked = !$scope.statusSlideChecked;
    };

    $scope.selectStatus = function(step) {
        if (step.selected && step.selected === true) {
            step.selected = false;
            $.each($scope.query.status, function(i) {
                if (this.id === step.id) {
                    $scope.query.status.splice(i, 1);
                    return false;
                }
            });
        } else {
            step.selected = true;
            $scope.query.status.push(step);
        }
    };

    // process
    $scope.toggleProcessSlide = function(order) {
        $scope.processSlideChecked = !$scope.processSlideChecked;
        if ($scope.processSlideChecked) {
            $scope.processOrder = order;
            $scope.processOrder.active = true;
        } else {
            if ($scope.processOrder) {
                $scope.processOrder.active = false;
            }
        }
    };

    $scope.updateStep = function(order) {
        console.log('updateStep:');
        $scope.processOrder = order;
    };

    $scope.saveUpdateStep = function(process, stepId) {
        process.step.id = stepId;
        ObjectProcess.save({}, process, function(objectProcess) {
            ObjectProcess.getAll({
                objectId: objectProcess.objectId
            }).then(function(objectProcesses) {
                $scope.processOrder.processes = angular.copy(objectProcesses);
            });
        });
    };

    // details
    $scope.toggleDetailsSlide = function(order) {
        $scope.detailsSlideChecked = !$scope.detailsSlideChecked;
        if ($scope.detailsSlideChecked) {
            $scope.processOrder = order;
            $scope.defaultHeight = {
                height: $(window).height() - 100
            };
        }
    };

    ///* 批量操作 */
    $scope.batchManipulation = function()
    {
        var orders = $scope.page.content;
        var selectedOrders = [];
        var orderIds = [];
        $.each( orders, function()
        {
            var order = this;
            if ( order.isSelected )
            {
                selectedOrders.push( angular.copy( order ) );
                orderIds.push( order.id );
            }
        });
        if ( selectedOrders.length > 0 )
        {
            if($scope.batchManipulationValue === 'orderPrint')
            {
                var url = '/order-print?orderId=' +( orderIds || '');
                $location.url( url );
            }
        }
        else
        {
            toastr.error('请选择一到多个订单来继续！');
        }

        $scope.batchManipulationValue = 'batchManipulation';
    };

    $scope.printSingle = function( orderId )
    {
        var url = '/order-print?orderId=' +( orderId || '');
        $location.url( url );
    };

};

OrderController.$inject = ['$scope', '$location', 'toastr', 'orderService', 'Utils', 'Process', 'ObjectProcess', 'Shop', 'Auth'];

angular.module('ecommApp').controller('OrderController', OrderController);