angular.module('ecommApp')

.controller('BrandController', ['$scope', 'Brand', function($scope, Brand) {

    var t = $.now();

    $scope.template = {
        operator: {
            url: 'views/product/brand/brand.operator-slide.html?' + t
        }
    };

    $scope.defaultQuery = {
        page: 0,
        size: 20,
        sort: ['name']
    };

    $scope.query = angular.copy($scope.defaultQuery);

    $scope.searchData = function(query) {
        Brand.get({
            page: query.page,
            size: query.size,
            sort: query.sort
        }, function(page) {
            $scope.page = page;
            $scope.brandSlideChecked = false;
        });
    };

    $scope.searchData($scope.query);

    $scope.updateBrand = function(brand) {
        $scope.brand = angular.copy(brand);
        $scope.toggleBrandSlide('edit');
    };

    $scope.showRemoveBrand = function(brand) {
        $scope.removingBrand = brand;
        $('#brandDeleteModal').modal('show');
    };

    $scope.removeBrand = function() {
        if (angular.isDefined($scope.removingBrand)) {
            Brand.remove({
                id: $scope.removingBrand.id
            }, {}, function() {
                $scope.removingBrand = undefined;
                $('#brandDeleteModal').modal('hide');
                $scope.searchData($scope.query);
            });
        }
    };

    $scope.saveBrand = function(brandForm, brand) {
        Brand.save({}, brand, function() {
            brandForm.$setPristine();
            $scope.searchData($scope.query);
        });
    };

    $scope.toggleBrandSlide = function(action) {
        $scope.brandSlideChecked = !$scope.brandSlideChecked;
        if (action === 'create') {
            $scope.title = '创建';
            $scope.brand = {};
        } else if (action === 'edit') {
            $scope.title = '编辑';
        }
    };

}]);
