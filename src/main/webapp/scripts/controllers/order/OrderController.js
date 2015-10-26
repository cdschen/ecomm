
var OrderController = function($scope, orderService, Utils, Process, ObjectProcess, Shop)
{
    var $ = angular.element;

    /* Activate Date Picker */
    $('input[ng-model="query.order.internalCreateTimeStart"], input[ng-model="query.order.internalCreateTimeEnd"], ' +
        'input[ng-model="query.order.shippingTimeStart"], input[ng-model="query.order.shippingTimeEnd"]').datepicker({
        format: 'yyyy-mm-dd',
        clearBtn: true,
        language: 'zh-CN',
        orientation: 'top left',
        todayHighlight: true,
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

    $scope.defaultQuery = {
        pageSize: 20,
        totalPagesList: [],
        sort: ['id'],
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

    Shop.getAll().then(function(shops) {
        $scope.shops = shops;
    });

    Process.getAll({
        deleted: false,
        objectType: 1
    }).then(function(processes) {
        $scope.processes = processes;
        Process.initStatus(processes);
    });

    $scope.searchData = function(query, number)
    {
        orderService.get({
            page: number ? number : 0,
            size: query.pageSize,
            sort: query.sort,
            orderId: query.order.orderId,
            shipNumber: query.order.shipNumber,
            shopId: query.shop.selected ? query.shop.selected.id : null,
            receiveName: query.order.receiveName,
            internalCreateTimeStart: query.order.internalCreateTimeStart,
            internalCreateTimeEnd: query.order.internalCreateTimeEnd,
            shippingTimeStart: query.order.shippingTimeStart,
            shippingTimeEnd: query.order.shippingTimeEnd,
            statusIds: Process.refreshStatus(query.status)
        }, function(page) {
            $scope.page = page;
            query.totalPagesList = Utils.setTotalPagesList(page);
        });
    };

    $scope.searchData($scope.query);

    $scope.turnPage = function(number) {
        if (number > -1 && number < $scope.page.totalPages) {
            $scope.searchData($scope.query, number);
        }
    };

    $scope.search = function() {
        $scope.searchData($scope.query);
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

};

OrderController.$inject = ['$scope', 'orderService', 'Utils', 'Process', 'ObjectProcess', 'Shop'];

angular.module('ecommApp').controller('OrderController', OrderController);