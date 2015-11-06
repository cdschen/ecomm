angular.module('ecommApp')

.controller('EnterInventorySheetController', ['$scope', '$rootScope', '$state', 'toastr', 'Warehouse', 'purchaseOrderDeliveryService', 'InventoryBatch',
    function($scope, $rootScope, $state, toastr, Warehouse, purchaseOrderDeliveryService, InventoryBatch) {


        $scope.selectAll = function(purchaseOrderDelivery, checkedAll) {
            $.each(purchaseOrderDelivery.items, function() {
                this.checked = checkedAll;
            });
        };

        $scope.defaultLeftHeight = function() {
            return {
                height: $(window).height() - 350
            };
        };

        $scope.defaultRightHeight = function() {
            return {
                height: $(window).height() - 200
            };
        };

        $scope.saveItem = function(item, itemAddForm) {

            item.warehouse = angular.copy($scope.batch.warehouse);
            if (!item.position) {
                item.position = item.warehouse.positions[0];
            }
            $.each($scope.purchaseOrderDelivery.items, function() {
                var pItem = this;
                if (pItem.checked) {
                    item.product = pItem.product;
                    $scope.items.push(angular.copy(item));
                    pItem.addedQty += item.changedQuantity;
                }
            });
            //$scope.batch.receiveId = $scope.purchaseOrderDelivery.id;
            //$scope.batches.push(angular.copy($scope.batch));

            $scope.batch = angular.copy($scope.defaultBatch);
            $scope.item = angular.copy($scope.defaultItem);
            itemAddForm.$setPristine();

            console.log($scope.items);
        };

        $scope.removeItem = function(item, $index) {
            $scope.items.splice($index, 1);
            $.each($scope.purchaseOrderDelivery.items, function() {
                var pItem = this;
                if (pItem.product.id === item.product.id) {
                    pItem.addedQty -= item.changedQuantity;
                }
            });
        };

        $scope.enterInventory = function(){
        	if ($scope.items.length > 0) {
        		
        		$.each($scope.items,function(){
        			var item = this;
        			var existItem = false;
        			$.each($scope.batches,function(){
        				var batch = this;
        				if (batch.warehouse.id === item.warehouse.id) {
        					batch.items.push(item);
        					existItem = true;
        					return false;
        				}
        			});
        			if (!existItem) {
        				$scope.batch = angular.copy($scope.defaultBatch);
        				$scope.batch.receiveId = $scope.purchaseOrderDelivery.id;
        				$scope.batch.warehouse = angular.copy(item.warehouse);
        				$scope.batch.items.push(item);
        				$scope.batches.push(angular.copy($scope.batch));
        			}
        		});

        		console.log($scope.batches);

        		InventoryBatch.saveList($scope.batches).then(function(){
        			$state.reload('purchaseOrderDelivery');
        		});
        	} else {
        		toastr.warning('请添加要入库的商品!');
        	}
        };
    }
]);
