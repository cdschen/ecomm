angular.module('ecommApp')

.controller('OrderController', ['$scope', 'Order', 'Utils', 'Process', 'ObjectProcess',
    function($scope, Order, Utils, Process, ObjectProcess) {

        //var $ = angular.element;
        $scope.template = {
            items: {
                url: 'views/order/order.items.html?' + (new Date())
            },
            process: {
                url: 'views/order/order.process.html?' + (new Date())
            },
            status: {
                url: 'views/order/order.status.html?' + (new Date())
            }
        };
        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.order = {};
        $scope.shop = {};
        $scope.shops = [
            { label:'杀阡陌', value:1 },
            { label:'淘宝宝', value:2 }
        ];
        $scope.processes = [];
        $scope.status = [];
        $scope.selected = {
            status: []
        };
        $scope.popover = {
            url: 'process-tmpl.html'
        };
        $scope.itemsSlideChecked = false;
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

        Order.get({
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
                Order.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id'],
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
            console.log($scope.order);
            Order.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id'],
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
            Order.get({
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

        // items

        $scope.closeItemsSlide = function() {
            $scope.itemsSlideChecked = false;
        };

        $scope.loadItems = function(order) {
            $scope.itemsSlideChecked = true;
            console.log(order);
            $scope.processOrder = order;
        };

    }
])

.controller('OrderItemsController', ['$scope', function($scope) {
    var $ = angular.element;

    $scope.initOrderItemsTabs = function() {
        $('#orderItemsTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    };

}])

.controller('OrderProcessController', ['$scope', '$filter', 'Process', 'ObjectProcess',
    function($scope, $filter, Process, ObjectProcess) {

        $scope.applyProcess = function(process) {
            
            var objectProcess = {
                objectId: $scope.processOrder.id,
                objectType: 1,
                process: {
                    id: process.id
                }
            };

            process.steps = $filter('orderBy')(process.steps, 'sequence');

            if (process.defaultStepId) {
                objectProcess.step = {
                    id: process.defaultStepId
                };
            } else {
                objectProcess.step = {
                    id: process.steps[0].id
                };
            }

            ObjectProcess.save({}, objectProcess).$promise.then(function(objectProcess) {
                return objectProcess;
            }).then(function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    console.log('refresh Processes:');
                    console.log(objectProcesses);
                    $scope.processOrder.processes = angular.copy(objectProcesses);
                    $scope.closeProcessSlide();
                });
            });

        };

        $scope.removeProcess = function(process) {
            var objectProcesses = $scope.processOrder.processes;
            var objectProcess;
            for (var i = 0, len = objectProcesses.length; i < len; i++) {
                objectProcess = objectProcesses[i];
                if (process.id === objectProcess.process.id) {
                    break;
                }
            }
            if (objectProcess) {
                ObjectProcess.remove({
                    id: objectProcess.id
                }, {}, function() {
                    ObjectProcess.getAll({
                        objectId: objectProcess.objectId
                    }).then(function(objectProcesses) {
                        console.log('refresh Processes:');
                        console.log(objectProcesses);
                        $scope.processOrder.processes = angular.copy(objectProcesses);
                        $scope.closeProcessSlide();
                    });
                });
            }

        };

        $scope.forObjectProcesses = function(step) {
            if ($scope.processOrder) {
                var objectProcesses = $scope.processOrder.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (objectProcess.process.id === step.processId) {
                        if (objectProcess.process.type === 1) {
                            if (step.sequence > objectProcess.step.sequence) {
                                return false;
                            } else if (step.sequence <= objectProcess.step.sequence) {
                                return true;
                            }
                        } else if (objectProcess.process.type === 2) {
                            if (step.id === objectProcess.step.id) {
                                return true;
                            } else if (step.id !== objectProcess.step.id) {
                                return false;
                            }
                        }
                    } else {
                        continue;
                    }
                }
                return false;
            }
        };

        $scope.appliedProcess = function(process) {
            if ($scope.processOrder) {
                var objectProcesses = $scope.processOrder.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (process.id === objectProcess.process.id) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
])

.controller('OrderOperatorController', ['$scope', '$state', '$stateParams', 'Order',
    function($scope, $state, $stateParams, Order) {

        console.clear();
        var $ = angular.element;
        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.template = {
            info: {
                url: 'views/order/order.operator.info.html?' + new Date(),
                items: {
                    url: 'views/order/order.operator.info.items.html?' + new Date()
                }
            }
        };

        $scope.action = 'create';

            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                Order.get({
                        id: $stateParams.id
                    }, {}).$promise
                    .then(function(order) {
                        console.log('[' + $scope.action + '] loading order');
                        console.log(order);
                        $scope.order = order;
                        return order;
                    });
            }

        $('#orderTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    }
])

.controller('OrderInformationController', ['$scope', '$state', 'Order',
    function($scope, $state, Order) {

        $scope.save = function(order) {
            //console.clear();
            console.log('[' + $scope.action + '] save order');
            console.log(order);
            Order.save({
                action: $scope.action
            }, order, function(order) {
                console.log('[' + $scope.action + '] save order complete:');
                console.log(order);
                $state.go('order');
            });
        };
    }
]);
