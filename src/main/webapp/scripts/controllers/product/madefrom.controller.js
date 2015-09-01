'use strict';

angular.module('ecommApp')

    .controller('MadeFromController', ['$rootScope', '$scope', 'MadeFrom', 'Utils',
        function($rootScope, $scope, MadeFrom, Utils) {

            var $ = angular.element;

            $scope.template = {
                operator: {
                    url: 'views/product/madefrom/madefrom.operator.html?' + (new Date())
                }
            };

            $scope.totalPagesList = [];
            $scope.pageSize = 20;
            $scope.madeFromSlideChecked = false;

            $scope.refresh = function() {
                MadeFrom.get({
                    page: 0,
                    size: $scope.pageSize,
                    sort: ['id,desc']
                }, function(page) {
                    console.clear();
                    console.log('page:');
                    console.log(page);
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                    $scope.colseMadeFromSlide();
                });
            };

            $scope.refresh();

            $scope.turnPage = function(number) {
                if (number > -1 && number < $scope.page.totalPages) {
                    MadeFrom.get({
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

            $scope.updateMadeFrom = function(madeFrom) {
                console.clear();
                console.log('updateMadeFrom:');
                console.log(madeFrom);
                $scope.madeFrom = madeFrom;
                $scope.operateMadeFrom();
            };

            $scope.removingMadeFrom = undefined;

            $scope.showRemoveMadeFrom = function(madeFrom, $index) {
                console.clear();
                console.log('showRemoveMadeFrom $index: ' + $index);
                console.log(madeFrom);

                $scope.removingMadeFrom = madeFrom;
                $('#madeFromDeleteModal').modal('show');
            };

            $scope.removeMadeFrom = function() {
                console.clear();
                console.log('removeMadeFrom:');
                console.log($scope.removingMadeFrom);

                if (angular.isDefined($scope.removingMadeFrom)) {
                    MadeFrom.remove({
                        id: $scope.removingMadeFrom.id
                    }, {}, function() {
                        $scope.removingMadeFrom = undefined;
                        $('#madeFromDeleteModal').modal('hide');
                        $scope.refresh();
                    });
                }
            };

            $scope.saveMadeFrom = function(madeFromForm, madeFrom) {
                console.clear();
                console.log('saveMadeFrom:');
                console.log(madeFrom);

                MadeFrom.save({}, madeFrom, function(madeFrom) {
                    console.log('saveMadeFrom complete:');
                    console.log(madeFrom);
                    madeFromForm.$setPristine();
                    $scope.refresh();
                });
            };

            // operator

            $scope.colseMadeFromSlide = function() {
                $scope.madeFromSlideChecked = false;
            };

            $scope.operateMadeFrom = function() {
                $scope.madeFromSlideChecked = true;
            };
        }
    ]);
