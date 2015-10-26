angular.module('ecommApp')

.controller('ProcessController', ['$scope', '$timeout', 'Process', 'ProcessStep', 'ObjectProcess', 'toastr',
    function($scope, $timeout, Process, ProcessStep, ObjectProcess, toastr) {

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
            $index: 't' + $.now(),
            editable: true
        };

        defaultProcess.steps.push(angular.copy(defaultStep));

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
            process.autoApply = $scope.isorno[process.autoApply ? 0 : 1];
            process.hideWhenComplete = $scope.isorno[process.hideWhenComplete ? 0 : 1];
            if (process.defaultStepId !== null) {
                $.each(process.steps, function() {
                    if (process.defaultStepId === this.id) {
                        process.defaultStep = angular.copy(this);
                        return false;
                    }
                });
            }
        }

        function refreshField(process) {
            process.type = process.type.value;
            process.objectType = process.objectType.value;
            process.defaultStepId = process.defaultStep && process.defaultStep.id;
            process.defaultStepName = process.defaultStep && process.defaultStep.name;
            process.autoApply = process.autoApply.value;
            process.hideWhenComplete = process.hideWhenComplete.value;
        }

        Process.getAll({
            deleted: false
        }).then(function(processes) {
            $scope.processes = processes;
            $.each(processes, function() {
                initField(this);
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
            refreshField(process);
            Process.save({}, process, function(process) {
                processForm.$setPristine();
                initField(process);
                $scope.processes[$index] = angular.copy(process);
            });
        };

        $scope.showRemoveProcess = function(process, $index) {
            ObjectProcess.getCount({
                processId: process.id
            }).then(function(count) {
                if (count > 0) {
                    toastr.warning('当前流程已经被应用，不能被删除。');
                } else {
                    $scope.removingProcess = process;
                    $scope.removingProcess.$index = $index;
                    $('#processDeleteModal').modal('show');
                }
            });
        };

        $scope.removeProcess = function() {
            if ($scope.removingProcess.id) {
                refreshField($scope.removingProcess);
                $scope.removingProcess.deleted = true;
                Process.save({}, $scope.removingProcess, function() {
                    $scope.processes.splice($scope.removingProcess.$index, 1);
                    $('#processDeleteModal').modal('hide');
                    $scope.removingProcess = undefined;
                });
            } else {
                $scope.processes.splice($scope.removingProcess.$index, 1);
                $('#processDeleteModal').modal('hide');
                $scope.removingProcess = undefined;
            }
        };

        // step

        $scope.addStep = function(process, step, $index) {
            defaultStep.$index = 't' + $index + $.now();
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
        };

    }
]);
