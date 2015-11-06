angular.module('ecommApp')

.controller('TagController', ['$scope', 'Tag', 'Utils',
    function($scope, Tag, Utils) {

    	var t = $.now();
        
        $scope.template = {
            operator: {
                url: 'views/product/tag/tag.operator-slide.html?' + t
            }
        };

    	$scope.defaultQuery = {
            size: 20,
            sort: ['name']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.tagSlideChecked = false;

        $scope.searchData = function(query, number) {
            Tag.get({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
                Utils.initList(page, query);
                $scope.tagSlideChecked = false;
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };
       
        $scope.updateTag = function(tag) {
            $scope.tag = angular.copy(tag);
            $scope.toggleTagSlide('edit');
        };

        $scope.showRemoveTag = function(tag) {
            $scope.removingTag = tag;
            $('#tagDeleteModal').modal('show');
        };

        $scope.removeTag = function() {
            if (angular.isDefined($scope.removingTag)) {
                Tag.remove({
                    id: $scope.removingTag.id
                }, {}, function() {
                    $scope.removingTag = undefined;
                    $('#tagDeleteModal').modal('hide');
                    $scope.searchData($scope.query);
                });
            }
        };

        $scope.saveTag = function(tagForm, tag) {
            Tag.save({}, tag, function() {
                tagForm.$setPristine();
                $scope.searchData($scope.query);
            });
        };

        $scope.toggleTagSlide = function(action) {
            if (action === 'create') {
                $scope.title = '创建';
                $scope.tag = {};
            } else if (action === 'edit') {
                $scope.title = '编辑';
            }
            $scope.tagSlideChecked = !$scope.tagSlideChecked;
        };
    }
]);
