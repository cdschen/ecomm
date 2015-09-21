angular.module('ecommApp')

.controller('OutInventorySheetController', ['$rootScope', '$scope', 'Warehouse', 'Utils', 'Inventory', 'InventoryBatch',
    function($rootScope, $scope, Warehouse, Utils, Inventory, InventoryBatch) {

        var $ = angular.element;

        $scope.page = undefined;
        $scope.pageSize = 20;
        $scope.totalPagesList = [];

        $scope.warehouses = [];

        Warehouse.getAll({
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        });

        InventoryBatch.get({
            page: 0,
            size: $scope.pageSize,
            type: 1 // 已完成
        }).$promise.then(function(page) {
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        });

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                InventoryBatch.get({
                    page: number,
                    size: $scope.pageSize,
                    type: 1
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.initField = function(batch){
            
        };
    }
])

.controller('OutInventorySheetOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'Warehouse',
    function($rootScope, $scope, $state, $stateParams, Warehouse) {



    }
]);
