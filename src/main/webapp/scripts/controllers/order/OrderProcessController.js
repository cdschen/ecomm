
var OrderProcessController = function($scope, $filter, Process, ObjectProcess) {

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
                $scope.toggleProcessSlide();
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
                    $scope.toggleProcessSlide();
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
};

OrderProcessController.$inject = ['$scope', '$filter', 'Process', 'ObjectProcess'];

angular.module('ecommApp').controller('OrderProcessController', OrderProcessController);