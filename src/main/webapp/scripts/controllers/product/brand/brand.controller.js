angular.module('ecommApp')

.controller('BrandController', ['$rootScope', '$scope', 'Brand', 'Utils',
    function($rootScope, $scope, Brand, Utils) {

        var $ = angular.element,
            t = new Date().getTime();

        $scope.template = {
            operator: {
                url: 'views/product/brand/brand.operator-slide.html?' + t
            }
        };

        $scope.defaultQuery = {
            pageSize: 20,
            totalPagesList: [],
            sort: ['id,desc']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.brandSlideChecked = false;

        $scope.searchData = function(query, number) {
            Brand.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
                query.totalPagesList = Utils.setTotalPagesList(page);
                $scope.brandSlideChecked = false;
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

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

        $scope.toggleBrandSlide = function (action){
            if (action === 'create') {
                $scope.title = '创建';
                $scope.brand = {};
            } else if (action === 'edit') {
                $scope.title = '编辑';
            }
            $scope.brandSlideChecked = !$scope.brandSlideChecked;
        };
    }
]);
