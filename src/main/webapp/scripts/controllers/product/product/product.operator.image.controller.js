angular.module('ecommApp')

.controller('ProductImageController', ['$scope', '$cookies', '$stateParams',
    function($scope, $cookies, $stateParams) {

        var id = 0;
        
        if ($stateParams.id && $stateParams.id !== '') {
            id = $stateParams.id;
        } else {
            id = 0;
        }

        $scope.initImage = function() {
            $('input[type="file"][name="image"]').on('change', function() {
                // console.log(this.value.substring(this.value.lastIndexOf('\\') + 1));
                // var imgUrl = this.value.substring(this.value.lastIndexOf('\\') + 1);
                // if (id !== 0) {
                //     imgUrl = id + '-' + imgUrl;
                // }
                var field = this.id;

                $.ajax({
                    url: '/api/products/image/upload/' + id + '/' + field,
                    type: 'POST',
                    data: new FormData($('#' + this.id + 'Form')[0]),
                    enctype: 'multipart/form-data',
                    headers: {
                        'X-XSRF-TOKEN': $cookies.get('XSRF-TOKEN')
                    },
                    processData: false,
                    contentType: false,
                    cache: false,
                    success: function(res) {
                        $scope.$apply(function() {
                            $scope.product[field] = res;
                        });
                    },
                    error: function() {}
                });
            });
        };

    }
]);