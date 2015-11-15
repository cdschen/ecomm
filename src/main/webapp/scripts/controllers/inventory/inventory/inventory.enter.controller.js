angular.module('ecommApp')

.controller('InventoryEnterController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse', 'Product', 'InventoryBatch', 'Auth', 
    function($rootScope, $scope, $state, $stateParams, Warehouse, Product, InventoryBatch, Auth) {

        $scope.getProduct = function(val) {
            return Product.get({
                page: 0,
                size: 30,
                enabled: true,
                nameOrSku: val
            }).$promise.then(function(page) {
                return page.content;
            });
        };

        $scope.defaultBatch = {
            operate: 1, // 入库操作
            operateTime: undefined,
            memo: '',
            warehouse: undefined,
            user: $rootScope.user(),
            executeOperator: null,
            items: []
        };
        
        $scope.defaultItem = {
            product: undefined,
            warehouse: undefined,
            position: null,
            user: $rootScope.user(),
            executeOperator: null,
            changedQuantity: 1,
            expireDate: undefined,
            outBatch: null
        };

        $scope.batch = angular.copy($scope.defaultBatch);
        $scope.item = angular.copy($scope.defaultItem);

        $scope.changeWarehouse = function() {
            $scope.batch.items.length = 0;
            $scope.item.position = undefined;
            console.clear();
            console.log('$scope.batch:');
            console.log($scope.batch);
        };

        $('#sandbox-container input').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'bottom left',
            todayHighlight: true,
            autoclose: true
        });

        Warehouse.getAll({
            enabled: true,
            sort: ['name'],
            warehouseIds: Auth.refreshManaged('warehouse')
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
            if ($stateParams.id && $stateParams.id !== '') {
                $.each(warehouses, function() {
                    if (this.id === parseInt($stateParams.id)) {
                        $scope.batch.warehouse = angular.copy(this);
                        return false;
                    }
                });
            }
        });

        $scope.saveItem = function(item, itemAddForm) {
            item.warehouse = $scope.batch.warehouse;
            if (!item.position) {
                item.position = item.warehouse.positions[0];
            }
            $scope.batch.items.push(angular.copy(item));
            $scope.item = angular.copy($scope.defaultItem);
            itemAddForm.$setPristine();
        };

        $scope.showRemoveItem = function(item, $index) {
            console.clear();
            console.log('showRemoveItem $index: ' + $index);
            console.log(item);

            $scope.removingItem = item;
            $scope.removingItem.$index = $index;
            $('#itemDeleteModal').modal('show');
        };

        $scope.removeItem = function() {
            console.clear();
            console.log('removeItem:');
            console.log($scope.removingItem);

            if (angular.isDefined($scope.removingItem)) {
                $scope.batch.items.splice($scope.removingItem.$index, 1);
                $scope.removingItem = undefined;
                $('#itemDeleteModal').modal('hide');
            }
        };

        $scope.saveBatch = function(batch) {
            console.clear();
            console.log('saveBatch:');
            console.log(batch);
            InventoryBatch.save({}, $scope.batch, function(batch) {
                console.log(batch);
                $rootScope.usingWarehouseId = batch.warehouse.id;
                $state.go('inventory');
            });
        };

    }
]);
