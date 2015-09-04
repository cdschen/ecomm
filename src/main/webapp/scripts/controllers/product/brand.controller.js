angular.module('ecommApp')

.controller('BrandController', ['$rootScope', '$scope', 'Brand', 'Utils',
    function($rootScope, $scope, Brand, Utils) {

        var $ = angular.element;

        $scope.template = {
            operator: {
                url: 'views/product/brand/brand.operator.html?' + (new Date())
            }
        };

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.brandSlideChecked = false;
        $scope.title = '';

        $scope.refresh = function() {
            Brand.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id,desc']
            }, function(page) {
                console.clear();
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.closeBrandSlide();
            });
        };

        $scope.refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Brand.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id,desc']
                }, function(page) {
                    console.clear();
                    console.log('turnPage:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.updateBrand = function(brand) {
            console.clear();
            console.log('updateBrand:');
            console.log(brand);
            $scope.brand = brand;
            $scope.operateBrand();
        };

        $scope.removingBrand = undefined;

        $scope.showRemoveBrand = function(brand, $index) {
            console.clear();
            console.log('showRemoveBrand $index: ' + $index);
            console.log(brand);

            $scope.removingBrand = brand;
            $('#brandDeleteModal').modal('show');
        };

        $scope.removeBrand = function() {
            console.clear();
            console.log('removeBrand:');
            console.log($scope.removingBrand);

            if (angular.isDefined($scope.removingBrand)) {
                Brand.remove({
                    id: $scope.removingBrand.id
                }, {}, function() {
                    $scope.removingBrand = undefined;
                    $('#brandDeleteModal').modal('hide');
                    $scope.refresh();
                });
            }
        };

        $scope.saveBrand = function(brandForm, brand) {
            console.clear();
            console.log('saveBrand:');
            console.log(brand);

            Brand.save({}, brand, function(brand) {
                console.log('saveBrand complete:');
                console.log(brand);
                brandForm.$setPristine();
                $scope.refresh();
            });
        };

        // operator

        $scope.closeBrandSlide = function() {
            $scope.brandSlideChecked = false;
        };

        $scope.operateBrand = function(action) {
            $scope.title = '编辑';
            if (action === 'create') {
                $scope.title = '创建';
                $scope.brand = {};
            }
            $scope.brandSlideChecked = true;
        };
    }
]);
