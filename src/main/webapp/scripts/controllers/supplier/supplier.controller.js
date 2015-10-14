angular.module('ecommApp')

.controller('SupplierController', ['$rootScope', '$scope', 'Supplier', 'Utils',
    function($rootScope, $scope, Supplier, Utils) {

        var $ = angular.element;

        $scope.template = {
            operator: {
                url: 'views/supplier/supplier.operator-slide.html?' + (new Date())
            }
        };

        $scope.defaultQuery = {
            pageSize: 20,
            totalPagesList: [],
            sort: ['id,desc']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.defaultSupplier = {
            deleted: false
        };
        $scope.supplier = angular.copy($scope.defaultSupplier);

        $scope.supplierSlideChecked = false;

        $scope.searchData = function(query, number) {
            Supplier.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort,
                deleted: false
            }, function(page) {
                $scope.page = page;
                query.totalPagesList = Utils.setTotalPagesList(page);
                $scope.supplierSlideChecked = false;
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        $scope.updateSupplier = function(supplier) {
            $scope.supplier = angular.copy(supplier);
            $scope.toggleSupplierSlide('edit');
        };

        $scope.showRemoveSupplier = function(supplier) {
            $scope.removingSupplier = supplier;
            $('#supplierDeleteModal').modal('show');
        };

        $scope.removeSupplier = function() {
            if (angular.isDefined($scope.removingSupplier)) {
                $scope.removingSupplier.deleted = true;
                Supplier.save({}, $scope.removingSupplier, function() {
                    $scope.removingSupplier = undefined;
                    $('#supplierDeleteModal').modal('hide');
                    $scope.searchData($scope.query);
                });
            }
        };

        $scope.saveSupplier = function(supplierForm, supplier) {
            Supplier.save({}, supplier, function() {
                supplierForm.$setPristine();
                $scope.searchData($scope.query);
            });
        };

        $scope.toggleSupplierSlide = function(action) {
            if (action === 'create') {
                $scope.title = '创建';
                $scope.supplier = angular.copy($scope.defaultSupplier);
            } else if (action === 'edit') {
                $scope.title = '编辑';
            }
            $scope.supplierSlideChecked = !$scope.supplierSlideChecked;
        };
    }
]);
