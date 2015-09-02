'use strict';

angular.module('ecommApp')

.controller('ProcessController', ['$scope', '$timeout', 'Process', 'ProcessStep',
    function($scope, $timeout, Process, ProcessStep) {
        var $ = angular.element;
        var defaultProcess = {
            type: {
                label: '线性流程',
                value: 1
            },
            defaultStep: undefined,
            hideWhenComplete: {
                label: '否',
                value: false
            },
            steps: [],
            deleted: false,
            editable: true
        };
        var defaultStep = {
            id: null,
            processId: null,
            name: '',
            sequence: 0,
            type: 1,
            $index: 't' + Date.parse(Date()),
            editable: true
        };
        defaultProcess.steps.push(angular.copy(defaultStep));
        $scope.processes = [];
        $scope.types = [{
            label: '线性流程',
            value: 1
        }, {
            label: '开关流程',
            value: 2
        }];
        $scope.objectTypes = [{
            label: '订单',
            value: 1
        }, {
            label: '商品',
            value: 2
        }, {
            label: '库存',
            value: 3
        }, {
            label: '采购',
            value: 4
        }];
        $scope.isorno = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];

        function initField(process) {
            process.type = $scope.types[process.type - 1];
            process.objectType = $scope.objectTypes[process.objectType - 1];
            if (process.defaultStepId !== null) {
                for (var i = 0, len = process.steps.length; i < len; i++) {
                    var step = process.steps[i];
                    if (process.defaultStepId === step.id) {
                        process.defaultStep = angular.copy(step);
                        break;
                    }
                }
            }
            process.hideWhenComplete = $scope.isorno[process.hideWhenComplete ? 0 : 1];
        }

        function refreshField(process) {
            process.type = process.type.value;
            process.objectType = process.objectType.value;
            process.defaultStepId = process.defaultStep && process.defaultStep.id;
            process.defaultStepName = process.defaultStep && process.defaultStep.name;
            process.hideWhenComplete = process.hideWhenComplete.value;
        }

        Process.getAll({
            deleted: false
        }).then(function(processes) {
            console.log('Process.getAll():');
            console.log(processes);
            $scope.processes = processes;
            angular.forEach($scope.processes, function(process) {
                initField(process);
            });
        });

        // process

        $scope.addProcess = function() {
            $scope.processes.unshift(angular.copy(defaultProcess));
        };

        $scope.updateProcess = function(process) {
            process.editable = true;
        };

        $scope.saveProcess = function(process, processForm, $index) {

            console.log('saveProcess:');
            console.log(process);

            refreshField(process);

            Process.save({}, process).$promise.then(function(process) {
                console.log('saveProcess complete:');
                angular.forEach(process.steps, function(step) {
                    step.processId = process.id;
                });
                console.log(process);
                processForm.$setPristine();

                initField(process);

                $scope.processes[$index] = angular.copy(process);

                console.log('$scope.processes:');
                console.log($scope.processes);

            });
        };

        var removingProcess;

        $scope.showRemoveProcess = function(process, $index) {
            removingProcess = process;
            removingProcess.$index = $index;
            $('#processDeleteModal').modal('show');
        };

        $scope.removeProcess = function() {
            if (removingProcess.id) {

                refreshField(removingProcess);
                removingProcess.deleted = true;

                Process.save({}, removingProcess).$promise.then(function() {
                    $scope.processes.splice(removingProcess.$index, 1);
                    $('#processDeleteModal').modal('hide');
                    removingProcess = undefined;
                });
            } else {
                $scope.processes.splice(removingProcess.$index, 1);
                $('#processDeleteModal').modal('hide');
                removingProcess = undefined;
            }
        };

        // step

        $scope.addStep = function(process, step, $index) {
            defaultStep.$index = 't' + $index + Date.parse(Date());
            process.steps.push(angular.copy(defaultStep));
            process.steps = ProcessStep.refresh(process.steps);
        };

        $scope.removeStep = function(process, step, $index) {
            var len = process.steps.length;
            if (len === 1) {
                return;
            }
            process.defaultStep = undefined;
            process.steps.splice($index, 1);
            process.steps = ProcessStep.refresh(process.steps);

        };

        $scope.drop = function(e, ui, process) {
            process.steps = ProcessStep.refresh(process.steps);
            console.log('drop:');
            console.log(process.steps);
        };

    }
]);
