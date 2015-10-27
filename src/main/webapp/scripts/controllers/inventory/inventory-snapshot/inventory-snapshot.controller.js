angular.module('ecommApp')

.controller('InventorySnapshotController', ['$scope', 'Warehouse', 'Utils', 'Inventory', 'InventoryBatch', 'InventoryBatchItem', 'Auth',
    function($scope, Warehouse, Utils, Inventory, InventoryBatch, InventoryBatchItem, Auth) {

        $scope.Math = Math;

        $scope.warehouses = [];
        $scope.operates = [{
            label: '入库',
            value: 1
        }, {
            label: '出库',
            value: 2
        }];

        $scope.defautlQuery = {
            pageSize: 50,
            totalPagesList: [],
            sort: ['createTime,desc'],
            warehouse: undefined,
            product: {
                sku: '',
                name: ''
            },
            batchItem: {
                createTimeStart: undefined,
                createTimeEnd: undefined,
                batchOperate: undefined,
            }
        };
        $scope.query = angular.copy($scope.defautlQuery);

        Warehouse.getAll({
            deleted: false,
            sort: ['name'],
            warehouseIds: Auth.refreshManaged('warehouse')
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        });

        $scope.searchData = function(query, number) {
            InventoryBatchItem.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort,
                productSKU: query.product.sku,
                productName: query.product.name,
                warehouseId: query.warehouse ? query.warehouse.id : null,
                warehouseIds: Auth.refreshManaged('warehouse'),
                createTimeStart: query.batchItem.createTimeStart,
                createTimeEnd: query.batchItem.createTimeEnd,
                batchOperate: query.batchItem.batchOperate ? query.batchItem.batchOperate.value : null
            }, function(page) {
                $scope.page = page;
                $.each(page.content, function() {
                    this.inventorySnapshot = angular.fromJson(this.inventorySnapshot);
                });
                query.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        $scope.search = function(query) {
            $scope.searchData(query);
        };

        $scope.reset = function() {
            $scope.query = angular.copy($scope.defautlQuery);
            $scope.searchData($scope.query);
        };

        $('#sandbox-container-create .input-daterange').datepicker({
            format: 'yyyy/mm/dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'top left',
            todayHighlight: true,
        });

    }

]);
