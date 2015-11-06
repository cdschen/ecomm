angular.module('ecommApp')

.controller('ProcessOperatorController', ['$scope', '$state', '$stateParams', 'Process', 'ProcessStep', 'ObjectProcess', 'toastr',
    function($scope, $state, $stateParams, Process, ProcessStep, ObjectProcess, toastr) {

        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';
        $scope.action = 'create';

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

        $scope.defaultProcess = {
            type: {
                label: '线性流程',
                value: 1
            },
            autoApply: {
                label: '是',
                value: true
            },
            hideWhenComplete: {
                label: '否',
                value: false
            },
            enabled: {
                label: '是',
                value: true
            },
            defaultStep: undefined,
            steps: []
        };

        $scope.defaultStep = {
            name: '',
            sequence: 0,
            type: 1,
            $index: 't' + $.now()
        };

        $scope.defaultProcess.steps.push(angular.copy($scope.defaultStep));
        $scope.process = angular.copy($scope.defaultProcess);

        function initProperties(process) {
            process.type = $scope.types[process.type - 1];
            process.objectType = $scope.objectTypes[process.objectType - 1];
            process.autoApply = $scope.isorno[process.autoApply ? 0 : 1];
            process.hideWhenComplete = $scope.isorno[process.hideWhenComplete ? 0 : 1];
            process.enabled = $scope.isorno[process.enabled ? 0 : 1];
        }

        function refreshProperties(process) {
            process.type = process.type.value;
            process.objectType = process.objectType.value;
            process.defaultStepId = process.defaultStep && process.defaultStep.id;
            process.defaultStepName = process.defaultStep && process.defaultStep.name;
            process.autoApply = process.autoApply.value;
            process.hideWhenComplete = process.hideWhenComplete.value;
            process.enabled = process.enabled.value;
        }

        if ($stateParams.id && $stateParams.id !== '') {
            $scope.action = 'update';
            Process.get({
                id: $stateParams.id
            }, {}, function(process) {
                $scope.process = process;
                initProperties(process);
            });
        }

        /*
         * Process
         */

        $scope.saveProcess = function(process) {

            if ($scope.action === 'update' && process.enabled.value === false) {
                ObjectProcess.getCount({
                    processId: process.id
                }).then(function(count) {
                    if (count > 0) {
                        toastr.warning('当前流程已经被应用, 不能被删除');
                    } else {
                        refreshProperties(process);
                        Process.save({}, process, function() {
                            $state.go('process');
                        });
                    }
                });
            } else {
                refreshProperties(process);
                Process.save({}, process, function() {
                    $state.go('process');
                });
            }

        };

        /*
         * Step
         */

        $scope.addStep = function(process, step, $index) {
            $scope.defaultStep.$index = 't' + $index + $.now();
            process.steps.push(angular.copy($scope.defaultStep));
            ProcessStep.refresh(process.steps);
        };

        $scope.removeStep = function(process, step, $index) {
            if (process.steps.length === 1) {
                return;
            }
            process.defaultStep = undefined;
            process.steps.splice($index, 1);
            ProcessStep.refresh(process.steps);
        };

        $scope.drop = function(e, ui, process) {
            ProcessStep.refresh(process.steps);
        };

    }
]);
