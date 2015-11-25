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
                        readers      :   []
                    };
                }

                if ( changeEvent.target.files.length > 0 )
                {
                    for (var i=0, ii=changeEvent.target.files.length; i<ii; i++)
                    {
                        //var file = droppedFiles[i];
                        var reader = new FileReader();
                        reader.readAsBinaryString( changeEvent.target.files[i] );
                        reader.name = changeEvent.target.files[i].name;
                        $scope.inputFileReaderDirective.readers.push( reader );
                    }
                    if( $scope.initInputFileReader )
                    {
                        /* Found initDragAndDrop function in controller */
                        $scope.initInputFileReader();
                    }
                    else
                    {
                        /* Don't find initDragAndDrop function in controller */
                    }
                }
            });
        }
    };
}]);