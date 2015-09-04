angular.module('ecommApp')

.controller('SupplierController', ['$rootScope', '$scope', 'Supplier', 'Utils',
    function($rootScope, $scope, Supplier, Utils) {

    	var $ = angular.element;
        
        $scope.template = {
            operator: {
                url: 'views/supplier/supplier.operator.html?' + (new Date())
            }
        };

    	$scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.defaultSupplier = {
            deleted: false
        };
        $scope.supplier = angular.copy($scope.defaultSupplier);
        $scope.supplierSlideChecked = false;
        $scope.title = '';

        $scope.refresh = function() {
            Supplier.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id,desc'],
                deleted: false
            }, function(page) {
                console.clear();
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.closeSupplierSlide();
            });
        };

        $scope.refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Supplier.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id,desc'],
                    deleted: false
                }, function(page) {
                    console.clear();
                    console.log('turnPage:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };
       
        $scope.updateSupplier = function(supplier) {
            console.clear();
            console.log('updateSupplier:');
            console.log(supplier);
            $scope.supplier = supplier;
            $scope.operateSupplier();
        };

        $scope.removingSupplier = undefined;

        $scope.showRemoveSupplier = function(supplier, $index) {
            console.clear();
            console.log('showRemoveSupplier $index: ' + $index);
            console.log(supplier);

            $scope.removingSupplier = supplier;
            $('#supplierDeleteModal').modal('show');
        };

        $scope.removeSupplier = function() {
            console.clear();
            console.log('removeSupplier:');
            console.log($scope.removingSupplier);

            if (angular.isDefined($scope.removingSupplier)) {
                $scope.removingSupplier.deleted = true;
                Supplier.save({}, $scope.removingSupplier, function() {
                    $scope.removingSupplier = undefined;
                    $('#supplierDeleteModal').modal('hide');
                    $scope.refresh();
                });
            }
        };

        $scope.saveSupplier = function(supplierForm, supplier) {
            console.clear();
            console.log('saveSupplier:');
            console.log(supplier);

            Supplier.save({}, supplier, function(supplier) {
                console.log('saveSupplier complete:');
                console.log(supplier);
                supplierForm.$setPristine();
                $scope.supplier = angular.copy($scope.defaultSupplier);
                $scope.refresh();
            });
        };

        // operator

        $scope.closeSupplierSlide = function() {
            $scope.supplierSlideChecked = false;
        };

        $scope.operateSupplier = function(action) {
            $scope.title = '编辑';
            if (action === 'create') {
                $scope.title = '创建';
                $scope.supplier = angular.copy($scope.defaultSupplier);
            }
            $scope.supplierSlideChecked = true;
        };
    }
]);
