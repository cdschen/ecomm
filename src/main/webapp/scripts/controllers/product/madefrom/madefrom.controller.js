angular.module('ecommApp')

.controller('MadeFromController', ['$rootScope', '$scope', 'MadeFrom', 'Utils',
    function($rootScope, $scope, MadeFrom, Utils) {

        var $ = angular.element,
            t = new Date().getTime();

        $scope.template = {
            operator: {
                url: 'views/product/madefrom/madefrom.operator-slide.html?' + t
            }
        };

        $scope.defaultQuery = {
            pageSize: 20,
            totalPagesList: [],
            sort: ['id,desc']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.madeFromSlideChecked = false;

        $scope.searchData = function(query, number) {
            MadeFrom.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
                query.totalPagesList = Utils.setTotalPagesList(page);
                $scope.madeFromSlideChecked = false;
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        $scope.updateMadeFrom = function(madeFrom) {
            $scope.madeFrom = angular.copy(madeFrom);
            $scope.toggleMadeFromSlide('edit');
        };

        $scope.showRemoveMadeFrom = function(madeFrom) {
            $scope.removingMadeFrom = madeFrom;
            $('#madeFromDeleteModal').modal('show');
        };

        $scope.removeMadeFrom = function() {
            if (angular.isDefined($scope.removingMadeFrom)) {
                MadeFrom.remove({
                    id: $scope.removingMadeFrom.id
                }, {}, function() {
                    $scope.removingMadeFrom = undefined;
                    $('#madeFromDeleteModal').modal('hide');
                    $scope.searchData($scope.query);
                });
            }
        };

        $scope.saveMadeFrom = function(madeFromForm, madeFrom) {
            MadeFrom.save({}, madeFrom, function() {
                madeFromForm.$setPristine();
                $scope.searchData($scope.query);
            });
        };

        $scope.toggleMadeFromSlide = function(action) {
            if (action === 'create') {
                $scope.title = '创建';
                $scope.madeFrom = {};
            } else if (action === 'edit') {
                $scope.title = '编辑';
            }
            $scope.madeFromSlideChecked = !$scope.madeFromSlideChecked;
        };
    }
]);
