angular.module('ecommApp')

.controller('TagController', ['$scope', 'Tag', function($scope, Tag) {

    var t = $.now();

    $scope.template = {
        operator: {
            url: 'views/product/tag/tag.operator-slide.html?' + t
        }
    };

    $scope.defaultQuery = {
        page: 0,
        size: 20,
        sort: ['name']
    };

    $scope.query = angular.copy($scope.defaultQuery);

    $scope.searchData = function(query) {
        Tag.get({
            page: query.page,
            size: query.size,
            sort: query.sort
        }, function(page) {
            $scope.page = page;
            $scope.tagSlideChecked = false;
        });
    };

    $scope.searchData($scope.query);

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
        $scope.tagSlideChecked = !$scope.tagSlideChecked;
        if (action === 'create') {
            $scope.title = '创建';
            $scope.tag = {};
        } else if (action === 'edit') {
            $scope.title = '编辑';
        }
    };

}]);
