angular.module('ecommApp')

.controller('TagController', ['$rootScope', '$scope', 'Tag', 'Utils',
    function($rootScope, $scope, Tag, Utils) {

    	var $ = angular.element;
        
        $scope.template = {
            operator: {
                url: 'views/product/tag/tag.operator.html?' + (new Date())
            }
        };

    	$scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.tagSlideChecked = false;

        $scope.refresh = function() {
            Tag.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['id,desc']
            }, function(page) {
                console.clear();
                console.log('page:');
                console.log(page);
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
                $scope.closeTagSlide();
            });
        };

        $scope.refresh();

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Tag.get({
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
       
        $scope.updateTag = function(tag) {
            console.clear();
            console.log('updateTag:');
            console.log(tag);
            $scope.tag = tag;
            $scope.operateTag();
        };

        $scope.removingTag = undefined;

        $scope.showRemoveTag = function(tag, $index) {
            console.clear();
            console.log('showRemoveTag $index: ' + $index);
            console.log(tag);

            $scope.removingTag = tag;
            $('#tagDeleteModal').modal('show');
        };

        $scope.removeTag = function() {
            console.clear();
            console.log('removeTag:');
            console.log($scope.removingTag);

            if (angular.isDefined($scope.removingTag)) {
                Tag.remove({
                    id: $scope.removingTag.id
                }, {}, function() {
                    $scope.removingTag = undefined;
                    $('#tagDeleteModal').modal('hide');
                    $scope.refresh();
                });
            }
        };

        $scope.saveTag = function(tagForm, tag) {
            console.clear();
            console.log('saveTag:');
            console.log(tag);

            Tag.save({}, tag, function(tag) {
                console.log('saveTag complete:');
                console.log(tag);
                tagForm.$setPristine();
                $scope.tag = angular.copy($scope.defaultTag);
                $scope.refresh();
            });
        };

        // operator

        $scope.closeTagSlide = function() {
            $scope.tagSlideChecked = false;
        };

        $scope.operateTag = function(action) {
            if (action === 'create') {
                $scope.tag = {};
            }
            $scope.tagSlideChecked = true;
        };
    }
]);
