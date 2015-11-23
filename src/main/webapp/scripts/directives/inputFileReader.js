angular.module('ecommApp')

.directive('inputFileReader', [function () {
    return {
        restrict: 'A',
        link: function($scope, element) {
            element.bind('change', function ( changeEvent )
            {
                if( ! $scope.inputFileReaderDirective )
                {
                    $scope.inputFileReaderDirective =
                    {
                        reader      :   {}
                    };
                }

                var reader = new FileReader();
                reader.readAsBinaryString( changeEvent.target.files[0] );
                reader.name = changeEvent.target.files[0].name;
                $scope.inputFileReaderDirective.reader = reader;
                if( $scope.initInputFileReader )
                {
                    /* Found initDragAndDrop function in controller */
                    $scope.initInputFileReader();
                }
                else
                {
                    /* Don't find initDragAndDrop function in controller */
                }
            });
        }
    };
}]);