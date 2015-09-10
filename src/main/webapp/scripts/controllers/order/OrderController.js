
var OrderController = function($scope, orderService, Utils, Process, ObjectProcess, Shop)
{
    var $ = angular.element;

    /* Activate Date Picker */
    $('input[ng-model="order.internalCreateTimeStart"], input[ng-model="order.internalCreateTimeEnd"], ' +
        'input[ng-model="order.shippingTimeStart"], input[ng-model="order.shippingTimeEnd"]').datepicker({
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
        }
    };
    $scope.totalPagesList = [];
    $scope.pageSize = 20;
    $scope.order = {};
    $scope.shop = {};
    $scope.shops = [];
    $scope.processes = [];
    $scope.status = [];
    $scope.selected = {
        status: []
    };
    $scope.popover = {
        url: 'process-tmpl.html'
    };
    $scope.detailSlideChecked = false;
    $scope.processSlideChecked = false;
    $scope.statusSlideChecked = false;
    $scope.processOrder = undefined;

    function initStatus(processes) {
        angular.forEach(processes, function(process) {
            angular.forEach(process.steps, function(step) {
                step.processName = process.name;
                $scope.status.push(step);
            });
        });
        // console.log('$scope.status:');
        // console.log($scope.status);
    }

    function refreshStatus(status) {
        var selectedStatus = [];
        angular.forEach(status, function(state) {
            selectedStatus.push(state.id);
        });
        return selectedStatus;
    }

    Shop.getAll().then(function(shops) {
        $scope.shops = shops;
    });

    orderService.get({
        page: 0,
        size: $scope.pageSize,
        sort: ['id'],
        deleted: false
    }).$promise.then(function(page) {
            console.clear();
            console.log('page:');
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        }).then(function() {
            Process.getAll({
                deleted: false,
                objectType: 1
            }).then(function(processes) {
                console.log('Process.getAll:');
                console.log(processes);
                $scope.processes = processes;
                initStatus(processes);
            });
        });

    $scope.turnPage = function(number) {
        console.clear();
        console.log('turnPage:');
        console.log($scope.order);
        if (number > -1 && number < $scope.page.totalPages) {
            orderService.get({
                page: number,
                size: $scope.pageSize,
                sort: ['id'],
                internalCreateTimeStart: $scope.order.internalCreateTimeStart,
                internalCreateTimeEnd: $scope.order.internalCreateTimeEnd,
                shippingTimeStart: $scope.order.shippingTimeStart,
                shippingTimeEnd: $scope.order.shippingTimeEnd,
                status: refreshStatus($scope.selected.status)
            }, function(page) {
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        }
    };

    $scope.search = function() {
        console.clear();
        console.log('search:');
        //console.log($scope.order);
        //console.log($scope.selected.status);
        console.log($scope.shop.selected);
        orderService.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['id'],
            orderId: $scope.order.orderId,
            shopId: $scope.shop.selected ? $scope.shop.selected.id : null,
            receiveName: $scope.order.receiveName,
            internalCreateTimeStart: $scope.order.internalCreateTimeStart,
            internalCreateTimeEnd: $scope.order.internalCreateTimeEnd,
            shippingTimeStart: $scope.order.shippingTimeStart,
            shippingTimeEnd: $scope.order.shippingTimeEnd,
            status: refreshStatus($scope.selected.status)
        }, function(page) {
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        });
    };

    $scope.reset = function() {
        console.clear();
        console.log('reset:');
        $scope.order = {};
        $scope.selected.status.length = 0;
        console.log($scope.order);
        orderService.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['id']
        }, function(page) {
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        });
    };

    // status

    $scope.closeStatusSlide = function() {
        $scope.statusSlideChecked = false;
    };

    $scope.loadStatus = function() {
        $scope.statusSlideChecked = true;
    };

    $scope.selectState = function(step) {
        if (step.selected && step.selected === true) {
            step.selected = false;
            for (var i = 0, len = $scope.selected.status.length; i < len; i++) {
                if ($scope.selected.status[i].id === step.id) {
                    $scope.selected.status.splice(i, 1);
                    break;
                }
            }
        } else {
            step.selected = true;
            $scope.selected.status.push(step);
        }
        console.log('$scope.selectState():');
        console.log($scope.selected.status);
    };

    // process

    $scope.closeProcessSlide = function() {
        $scope.processSlideChecked = false;
        if ($scope.processOrder) {
            $scope.processOrder.active = false;
        }
    };

    $scope.loadProcesses = function(order) {
        console.log(order);
        $scope.processSlideChecked = true;
        $scope.processOrder = order;
        $scope.processOrder.active = true;
    };

    $scope.updateStep = function(order) {
        console.log('updateStep:');
        $scope.processOrder = order;
    };

    $scope.saveUpdateStep = function(process, stepId) {
        console.log('saveUpdateStep:');
        process.step.id = stepId;
        console.log(process);
        ObjectProcess.save({}, process, function(objectProcess) {
            ObjectProcess.getAll({
                objectId: objectProcess.objectId
            }).then(function(objectProcesses) {
                console.log('refresh Processes:');
                console.log(objectProcesses);
                $scope.processOrder.processes = angular.copy(objectProcesses);
            });
        });
    };

    // detail
    $scope.closeDetailSlide = function() {
        $scope.detailSlideChecked = false;
    };

    $scope.loadDetail = function(order) {
        $scope.detailSlideChecked = true;
        console.log(order);
        $scope.processOrder = order;
    };

};

OrderController.$inject = ['$scope', 'orderService', 'Utils', 'Process', 'ObjectProcess', 'Shop'];

angular.module('ecommApp').controller('OrderController', OrderController);