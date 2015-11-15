angular.module('ecommApp')

.controller('SupplierProductController', ['$scope', '$rootScope', 'toastr', 'Supplier', 'User', 'Utils', 'supplierProductService',
    function($scope, $rootScope, toastr, Supplier, User, Utils, supplierProductService) {

        /* Activate Date Picker */
        $('input[ng-model="query.queryCreateTimeStart"], input[ng-model="query.queryCreateTimeEnd"], ' +
          'input[ng-model="query.queryLastUpdateStart"], input[ng-model="query.queryLastUpdateEnd"]').datepicker({
            format: 'yyyy-mm-dd',
            clearBtn: true,
            language: 'zh-CN',
            orientation: 'bottom left',
            todayHighlight: true,
            autoclose: true
        });

        $scope.selectedSupplierProduct = {};

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultQuery = {};
        $scope.query = angular.copy($scope.defaultQuery);
        $scope.suppliers = [];
        $scope.creators = [];
        $scope.supplierProducts = [];

        /* 查询采购单分页数据所需查询参数 */
        function getQueryParamJSON()
        {
            return {
                page: 0,
                size: $scope.pageSize,
                sort: ['createTime,desc'],
                queryProductBarcode: $scope.query.queryProductBarcode ? $scope.query.queryProductBarcode : null,
                querySupplierProductCode: $scope.query.querySupplierProductCode ? $scope.query.querySupplierProductCode : null,
                querySupplierProductName: $scope.query.querySupplierProductName ? $scope.query.querySupplierProductName : null,
                querySupplierId: $scope.query.querySupplier ? $scope.query.querySupplier.id : null,
                queryCreatorId: $scope.query.queryCreator ? $scope.query.queryCreator.id : null,
                queryCreateTimeStart: $scope.query.queryCreateTimeStart ? $scope.query.queryCreateTimeStart : null,
                queryCreateTimeEnd: $scope.query.queryCreateTimeEnd ? $scope.query.queryCreateTimeEnd : null,
                queryLastUpdateStart: $scope.query.queryLastUpdateStart ? $scope.query.queryLastUpdateStart : null,
                queryLastUpdateEnd: $scope.query.queryLastUpdateEnd ? $scope.query.queryLastUpdateEnd : null
            };
        }

        Supplier.getAll({ // 导入所有供应商
            enabled: true,
            sort: ['name']
        }).then(function(suppliers)
        {
            $scope.suppliers = suppliers;
        }).then(function()
        { // 导入所有用户
            return User.getAll({
                enabled: true,
                sort: ['username']
            }).then(function(creators) {
                $scope.creators = creators;
            });
        }).then(function() {
            supplierProductService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        });

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.query);
            supplierProductService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.query = angular.copy($scope.defaultQuery);
            console.log($scope.query);
            supplierProductService.get( getQueryParamJSON(), function(page) {
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.isCheckedAll = false;
            });
        };


        $scope.isCheckedAll = false;
        $scope.batchManipulationValue = 'batchManipulation';

        $scope.checkAllSupplierProducts = function()
        {
            for( var supplierProduct in $scope.page.content )
            {
                $scope.page.content[ supplierProduct ].isSelected = $scope.isCheckedAll;
            }
        };

        ///* 批量操作 */
        $scope.batchManipulation = function()
        {
            var supplierProducts = $scope.page.content;
            supplierProductService.selectedSupplierProducts.length = 0;
            $.each(supplierProducts, function()
            {
                var supplierProduct = this;
                if ( supplierProduct.isSelected )
                {
                    supplierProductService.selectedSupplierProducts.push( angular.copy( supplierProduct ) );
                }
            });
            if ( supplierProductService.selectedSupplierProducts.length > 0 )
            {
                if($scope.batchManipulationValue === 'supplierProductExport')
                {
                    toastr.info('供应商产品导出！');
                }
                else if($scope.batchManipulationValue === 'supplierProductPrint')
                {
                    toastr.info('供应商产品打印！');
                }
            }
            else
            {
                toastr.error('请选择一到多个供应商产品来继续！');
            }

            $scope.batchManipulationValue = 'batchManipulation';
        };

    }
]);
