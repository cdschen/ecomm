angular.module('ecommApp')

.controller('ProductMultiCurrencyController', ['$scope', '$stateParams', 'ProductMultiCurrency',
    function($scope, $stateParams, ProductMultiCurrency) {

        var $ = angular.element;
        var defaultMultiCurrency = {
            priceL1: 0.00,
            priceL2: 0.00,
            priceL3: 0.00,
            priceL4: 0.00,
            priceL5: 0.00,
            priceL6: 0.00,
            priceL7: 0.00,
            priceL8: 0.00,
            priceL9: 0.00,
            priceL10: 0.00
        };
        $scope.multiCurrency = angular.copy(defaultMultiCurrency);

        $scope.saveCurrency = function(mcAddForm, multiCurrency) {
            if ($scope.action === 'create') {
                $scope.product.multiCurrencies.push(angular.copy(multiCurrency));
                mcAddForm.$setPristine();
                $scope.multiCurrency = angular.copy(defaultMultiCurrency);
            } else if ($scope.action === 'update') {
                multiCurrency.productId = $stateParams.id;
                ProductMultiCurrency.save({}, multiCurrency, function(mc) {
                    $scope.product.multiCurrencies.push(angular.copy(mc));
                    mcAddForm.$setPristine();
                    $scope.multiCurrency = defaultMultiCurrency;
                });
            }
        };

        $scope.updateCurrency = function(mc) {
            mc.editable = true;
        };

        $scope.saveUpdateCurrency = function(mc) {
            if ($scope.action === 'create') {
                mc.editable = false;
            } else if ($scope.action === 'update') {
                ProductMultiCurrency.save({}, mc, function() {
                    mc.editable = false;
                });
            }
        };

        $scope.removingCurrency = undefined;

        $scope.showRemoveCurrency = function(mc, $index) {
            $scope.removingCurrency = mc;
            $scope.removingCurrency.$index = $index;
            $('#mcDeleteModal').modal('show');
        };

        $scope.removeCurrency = function() {
            if (angular.isDefined($scope.removingCurrency)) {
                if ($scope.action === 'create') {
                    $scope.product.multiCurrencies.splice($scope.removingCurrency.$index, 1);
                    $scope.removingCurrency = undefined;
                    $('#mcDeleteModal').modal('hide');
                } else if ($scope.action === 'update') {
                    ProductMultiCurrency.remove({
                        id: $scope.removingCurrency.id
                    }, {}, function() {
                        $scope.product.multiCurrencies.splice($scope.removingCurrency.$index, 1);
                        $scope.removingCurrency = undefined;
                        $('#mcDeleteModal').modal('hide');
                    });
                }
            }
        };

    }
]);
