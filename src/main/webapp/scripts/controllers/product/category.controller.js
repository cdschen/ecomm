'use strict';

angular.module('ecommApp')

.controller('CategoryController', ['$scope', 'Category', 'Utils',
    function($scope, Category, Utils) {

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.category = {};

        function refresh() {
            // Category.get({
            //     page: 0,
            //     size: $scope.pageSize,
            //     sort: ['id,desc']
            // }, function(page) {
            //     console.clear();
            //     console.log('page:');
            //     console.log(page);
            //     $scope.page = page;
            //     $scope.totalPagesList = Utils.setTotalPagesList(page);
            // });

            Category.getAll().then(function(cateogries){
                console.clear();
                console.log('cateogries:');
                console.log(cateogries);
            });
        };

        refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Category.get({
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

        $scope.saveCategory = function(categoryAddForm, category) {
            console.clear();
            console.log('saveCategory:');
            console.log(category);

            Category.save({}, category, function(category) {
                console.log('saveCategory complete:');
                console.log(category);
                categoryAddForm.$setPristine();
                $scope.category = {};
                refresh();
            });
        }

        $scope.updateCategory = function(category, $index) {
            console.clear();
            console.log('updateCategory:');
            console.log(category);
            category.editable = true;
        };

        $scope.saveUpdateCategory = function(category, $index) {
            console.clear();
            console.log('saveUpdateCategory complete:');
            Category.save({}, category, function() {
                console.log(category);
                category.editable = false;
            });
        };

        $scope.removingCategory = undefined;

        $scope.showRemoveCategory = function(category, $index) {
            console.clear();
            console.log('showRemoveCategory $index: ' + $index);
            console.log(category);

            $scope.removingCategory = category;
            $('#categoryDeleteModal').modal('show');
        }

        $scope.removeCategory = function() {
            console.clear();
            console.log('removeCategory:');
            console.log($scope.removingCategory);

            if (angular.isDefined($scope.removingCategory)) {
                Category.remove({
                    id: $scope.removingCategory.id
                }, {}, function() {
                    $scope.removingCategory = undefined;
                    $('#categoryDeleteModal').modal('hide');
                    refresh();
                });
            }
        };
    }
]);
