angular.module('ecommApp')

.factory('Category', ['$resource', '$http', function($resource, $http) {

    var category = $resource('/api/categories/:id', {}, {});

    category.getAll = function() {
        return $http.get('/api/categories/get/all').then(function(res) {
            return res.data;
        });
    };

    return category;
}]);
