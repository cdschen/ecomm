angular.module('ecommApp')

.controller('SupplierController', ['$scope', 'Supplier', 'Utils',
    function($scope, Supplier, Utils) {

        var t = $.now();

        $scope.template = {
            operator: {
                url: 'views/system/supplier/supplier.operator-slide.html?' + t
            }
        };

        $scope.isorno = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];

        $scope.defaultQuery = {
            size: 20,
            sort: ['name']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.defaultSupplier = {
            enabled: {
                label: '是',
                value: true
            }
        };
        $scope.supplier = angular.copy($scope.defaultSupplier);

        $scope.supplierSlideChecked = false;

        function initProperties(supplier) {
            supplier.enabled = $scope.isorno[supplier.enabled ? 0 : 1];
        }

        function refreshProperties(supplier) {
            supplier.enabled = supplier.enabled.value;
        }

        $scope.searchData = function(query, number) {
            Supplier.get({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
                Utils.initList(page, query);
                $scope.supplierSlideChecked = false;
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        $scope.saveSupplier = function(supplierForm, supplier) {
            refreshProperties(supplier);
            Supplier.save({}, supplier, function() {
                supplierForm.$setPristine();
                $scope.searchData($scope.query);
            });
        };

        $scope.toggleSupplierSlide = function(action, supplier) {
            $.each($scope.page.content, function(){
                this.active = false;
            });
            if (action === 'create') {
                $scope.title = '创建';
                $scope.supplier = angular.copy($scope.defaultSupplier);
            } else if (action === 'edit') {
                $scope.title = '编辑';
                supplier.active = true;
                $scope.supplier = angular.copy(supplier);
                initProperties($scope.supplier);
            }
            $scope.supplierSlideChecked = !$scope.supplierSlideChecked;
        };
    }
]);
