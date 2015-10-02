angular.module('ecommApp')

.controller('InventorySnapshotController', ['$rootScope', '$scope', 'Warehouse', 'Utils', 'Inventory', 'InventoryBatch', 'InventoryBatchItem',
    function($rootScope, $scope, Warehouse, Utils, Inventory, InventoryBatch, InventoryBatchItem) {

        var $ = angular.element;

        $scope.template = {
            productSnapshot: {
                url: 'views/inventory/inventory-snapshot/inventory-snapshot.product-snapshot-slide.html?' + (new Date())
            }
        };

        $scope.page = undefined;
        $scope.Math = Math;

        $scope.warehouses = [];
        $scope.operates = [{
            label: '入库',
            value: 1
        }, {
            label: '出库',
            value: 2
        }];
        // $scope.types = [{
        //     label: '作废',
        //     value: 0
        // }, {
        //     label: '待出库',
        //     value: 1
        // }, {
        //     label: '已出库',
        //     value: 2
        // }];

        $scope.defautlQuery = {
            pageSize: 50,
            totalPagesList: [],
            sort: ['operateTime,desc'],
            warehouse: undefined,
            batch: {
                operateTimeStart: undefined,
                operateTimeEnd: undefined,
                operate: undefined
            }
        };
        $scope.query = angular.copy($scope.defautlQuery);

        Warehouse.getAll({
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        });

        $scope.searchData = function(query, number) {
            InventoryBatch.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort,
                warehouseId: query.warehouse ? query.warehouse.id : null,
                operateTimeStart: query.batch.operateTimeStart,
                operateTimeEnd: query.batch.operateTimeEnd,
                operate: query.batch.operate ? query.batch.operate.value : null
            }).$promise.then(function(page) {
                console.log(page);
                $scope.page = page;
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

        $scope.toggleItemDetailsChecked = function(batch) {
            if (batch.itemDetailsChecked !== 'undefined') {
                batch.itemDetailsChecked = !batch.itemDetailsChecked;
            } else {
                batch.itemDetailsChecked = true;
            }
        };

        $scope.productSnapshotSlideChecked = false;
        $scope.productBatchItems = undefined;

        $scope.defaultHeight = function() {
            return {
                height: $(window).height() / 1.3
            };
        };

        $scope.toggleProductSnapshotSlide = function(item) {
            console.log(item);
            $scope.productSnapshotSlideChecked = !$scope.productSnapshotSlideChecked;
            if ($scope.productSnapshotSlideChecked) {
                InventoryBatchItem.getAll({
                    productId: item.productId,
                    warehouseId: item.warehouseId,
                    sort: ['createTime,desc']
                }).then(function(items){
                    $scope.productBatchItems = items;
                    $.each(items, function(){
                        this.inventorySnapshot = angular.fromJson(this.inventorySnapshot);
                    });
                    console.log(items);
                });
            }
        };

    }

]);
