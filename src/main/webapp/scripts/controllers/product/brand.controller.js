'use strict';

angular.module('ecommApp')

.controller('BrandController', ['$scope', 'Brand', 'Utils',
    function($scope, Brand, Utils) {

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.brand = {};

        function refresh() {
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
            });
        }

        refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Brand.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['id,desc'],
                }, function(page) {
                    console.clear();
                    console.log('turnPage:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.saveBrand = function(brandAddForm, brand) {
            console.clear();
            console.log('saveBrand:');
            console.log(brand);

            Brand.save({}, brand, function(brand) {
                console.log('saveBrand complete:');
                console.log(brand);
                brandAddForm.$setPristine();
                $scope.brand = {};
                refresh();
            });
        }

        $scope.updateBrand = function(brand, $index) {
            console.clear();
            console.log('updateBrand:');
            console.log(brand);
            brand.editable = true;
        };

        $scope.saveUpdateBrand = function(brand, brandForm, $index) {
            console.clear();
            console.log('saveUpdateBrand complete:');
            Brand.save({}, brand, function() {
                console.log(brand);
                brand.editable = false;
                brandForm.$setPristine();
            });
        };

        $scope.removingBrand = undefined;

        $scope.showRemoveBrand = function(brand, $index) {
            console.clear();
            console.log('showRemoveBrand $index: ' + $index);
            console.log(brand);

            $scope.removingBrand = brand;
            $('#brandDeleteModal').modal('show');
        }

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
                    refresh();
                });
            }
        };
    }
]);
