angular.module('ecommApp')

.controller('SourceController', ['$scope', 'Source', function($scope, Source) {

    var t = $.now();

    $scope.template = {
        operator: {
            url: 'views/product/source/source.operator-slide.html?' + t
        }
    };

    $scope.defaultQuery = {
        page: 0,
        size: 20,
        sort: ['name']
    };

    $scope.query = angular.copy($scope.defaultQuery);

    $scope.searchData = function(query) {
        Source.get({
            page: query.page,
            size: query.size,
            sort: query.sort
        }, function(page) {
            $scope.page = page;
            $scope.sourceSlideChecked = false;
        });
    };

    $scope.searchData($scope.query);

    $scope.updateSource = function(source) {
        $scope.source = angular.copy(source);
        $scope.toggleSourceSlide('edit');
    };

    $scope.showRemoveSource = function(source) {
        $scope.removingSource = source;
        $('#sourceDeleteModal').modal('show');
    };

    $scope.removeSource = function() {
        if (angular.isDefined($scope.removingSource)) {
            Source.remove({
                id: $scope.removingSource.id
            }, {}, function() {
                $scope.removingSource = undefined;
                $('#sourceDeleteModal').modal('hide');
                $scope.searchData($scope.query);
            });
        }
    };

    $scope.saveSource = function(sourceForm, source) {
        Source.save({}, source, function() {
            sourceForm.$setPristine();
            $scope.searchData($scope.query);
        });
    };

    $scope.toggleSourceSlide = function(action) {
        $scope.sourceSlideChecked = !$scope.sourceSlideChecked;
        if (action === 'create') {
            $scope.title = '创建';
            $scope.source = {};
        } else if (action === 'edit') {
            $scope.title = '编辑';
        }
    };

}]);
