angular.module('ecommApp')

.controller('OutInventorySheetOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'Inventory', 'InventoryBatch',
    function($rootScope, $scope, $state, $stateParams, Inventory, InventoryBatch) {

        var $ = angular.element;
        $scope.Math = Math;

        $scope.batch = undefined;
        $scope.products = [];

        InventoryBatch.get({
            id: $stateParams.id
        }, function(batch) {
            console.log('InventoryBatch complete:');
            console.log(batch);
            $scope.batch = batch;
            $scope.products = InventoryBatch.refreshBatchItems(batch);
            console.log('products');
            console.log($scope.products);
        });

        $scope.outInventory = function() {

            $scope.batch.executeOperator = $rootScope.user();
            $scope.batch.type = 2;

            console.clear();
            console.log('outInventory:batch');
            console.log($scope.batch);

            $.each($scope.batch.items, function() {
                var item = this;
                if (item.position) {
                    item.actualQuantity = -item.position.actualTotal;
                } else {
                    item.actualQuantity = -item.product.actualTotal;
                }
            });

            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log('complete outInventory:');
                console.log(batch);
                $state.go('outInventorySheet');
            });
        };
    }
]);
