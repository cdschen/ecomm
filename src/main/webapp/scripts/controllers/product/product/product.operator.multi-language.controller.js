angular.module('ecommApp')

.controller('ProductMultiLanguageController', ['$scope', '$stateParams', 'ProductMultiLanguage',
    function($scope, $stateParams, ProductMultiLanguage) {

        $scope.multiLanguage = {};

        $scope.saveLanguage = function(mlAddForm, multiLanguage) {
            if ($scope.action === 'create') {
                $scope.product.multiLanguages.push(angular.copy(multiLanguage));
                mlAddForm.$setPristine();
                $scope.multiLanguage = {};
            } else if ($scope.action === 'update') {
                multiLanguage.productId = $stateParams.id;
                ProductMultiLanguage.save({}, multiLanguage, function(ml) {
                    $scope.product.multiLanguages.push(ml);
                    mlAddForm.$setPristine();
                    $scope.multiLanguage = {};
                });
            }
        };

        $scope.updateLanguage = function(ml) {
            ml.editable = true;
        };

        $scope.saveUpdateLanguage = function(ml, mlForm) {
            if ($scope.action === 'create') {
                ml.editable = false;
                mlForm.$setPristine();
            } else if ($scope.action === 'update') {
                ProductMultiLanguage.save({}, ml, function() {
                    ml.editable = false;
                    mlForm.$setPristine();
                });
            }
        };

        $scope.showRemoveLanguage = function(ml, $index) {
            $scope.removingLanguage = ml;
            $scope.removingLanguage.$index = $index;
            $('#mlDeleteModal').modal('show');
        };

        $scope.removeLanguage = function() {
            if (angular.isDefined($scope.removingLanguage)) {
                if ($scope.action === 'create') {
                    $scope.product.multiLanguages.splice($scope.removingLanguage.$index, 1);
                    $scope.removingLanguage = undefined;
                    $('#mlDeleteModal').modal('hide');
                } else if ($scope.action === 'update') {
                    ProductMultiLanguage.remove({
                        id: $scope.removingLanguage.id
                    }, {}, function() {
                        $scope.product.multiLanguages.splice($scope.removingLanguage.$index, 1);
                        $scope.removingLanguage = undefined;
                        $('#mlDeleteModal').modal('hide');
                    });
                }
            }
        };

    }
]);
