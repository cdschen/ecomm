angular.module('ecommApp')

.controller('ProductProcessController', ['$scope', '$filter', 'Process', 'ObjectProcess',
    function($scope, $filter, Process, ObjectProcess) {

        $scope.applyProcess = function(process) {
            var objectProcess = {
                objectId: $scope.processProduct.id,
                objectType: 2,
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
            ObjectProcess.save({}, objectProcess, function() {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    $scope.processProduct.processes = objectProcesses;
                    $scope.toggleProcessSlide();
                });
            });
        };

        $scope.removeProcess = function(process) {
            var objectProcesses = $scope.processProduct.processes;
            var objectProcess;
            $.each(objectProcesses, function() {
                objectProcess = this;
                if (process.id === objectProcess.process.id) {
                    return false;
                }
            });
            if (objectProcess) {
                ObjectProcess.remove({
                    id: objectProcess.id
                }, {}, function() {
                    ObjectProcess.getAll({
                        objectId: objectProcess.objectId
                    }).then(function(objectProcesses) {
                        $scope.processProduct.processes = angular.copy(objectProcesses);
                        $scope.toggleProcessSlide();
                    });
                });
            }
        };

        $scope.forObjectProcesses = function(step) {
            if ($scope.processProduct) {
                var result = false;
                $.each($scope.processProduct.processes, function() {
                    var objectProcess = this;
                    if (objectProcess.process.id === step.processId) {
                        if (objectProcess.process.type === 1) {
                            if (step.sequence > objectProcess.step.sequence) {
                                result = false;
                            } else if (step.sequence <= objectProcess.step.sequence) {
                                result = true;
                            }
                        } else if (objectProcess.process.type === 2) {
                            if (step.id === objectProcess.step.id) {
                                result = true;
                            } else if (step.id !== objectProcess.step.id) {
                                result = false;
                            }
                        }
                        return false;
                    }
                });
                return result;
            }
        };

        $scope.appliedProcess = function(process) {
            if ($scope.processProduct) {
                var result = false;
                $.each($scope.processProduct.processes, function() {
                    var objectProcess = this;
                    if (process.id === objectProcess.process.id) {
                        result = true;
                        return false;
                    }
                });
                return result;
            }
        };
    }
]);
