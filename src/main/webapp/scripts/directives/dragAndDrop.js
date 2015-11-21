angular.module('ecommApp')

.directive('dragAndDrop', ['toastr', function( toastr ) {
    return {
        restrict: 'A',
        link: function($scope, elem) {
            elem.bind('dragover', function (e) {
                e.stopPropagation();
                e.preventDefault();
                //debugger;
                //e.dataTransfer.dropEffect = 'copy';
            });
            elem.bind('dragenter', function(e) {
                e.stopPropagation();
                e.preventDefault();
                //$scope.$apply(function() {
                //    $scope.divClass = 'on-drag-enter';
                //});
            });
            elem.bind('dragleave', function(e) {
                e.stopPropagation();
                e.preventDefault();
                //$scope.$apply(function() {
                //    $scope.divClass = '';
                //});
            });
            elem.bind('drop', function(e) {
                e.stopPropagation();
                e.preventDefault();
                // Removing everything other than .stopPropagation()
                // and .preventDefault() without any luck

                if( ! $scope.dragAndDropDirective )
                {
                    $scope.dragAndDropDirective =
                    {
                        readers     :   [],
                        progress    :   0
                    };
                }

                function errorHandler(evt) {
                    switch(evt.target.error.code) {
                        case evt.target.error.NOT_FOUND_ERR:
                            toastr.warning('File Not Found!');
                            break;
                        case evt.target.error.NOT_READABLE_ERR:
                            toastr.warning('File is not readable');
                            break;
                        case evt.target.error.ABORT_ERR:
                            break; // noop
                        default:
                            toastr.warning('An error occurred reading this file.');
                    }
                }

                function updateProgress(evt)
                {
                    // evt is an ProgressEvent.
                    if ( evt.lengthComputable )
                    {
                        var percentLoaded = Math.round((evt.loaded / evt.total) * 100);
                        // Increase the progress bar length.
                        if (percentLoaded <= 100)
                        {
                            $scope.dragAndDropDirective.progress = percentLoaded;
                        }
                    }
                }

                $scope.dragAndDropDirective.readers = [];

                var droppedFiles = e.originalEvent.dataTransfer.files;
                if ( droppedFiles.length > 0 )
                {
                    for (var i=0, ii=droppedFiles.length; i<ii; i++)
                    {
                        //var file = droppedFiles[i];
                        var reader = new FileReader();
                        reader.onerror = errorHandler;
                        reader.onprogress = updateProgress;
                        reader.readAsBinaryString( droppedFiles[i] );
                        reader.name = droppedFiles[i].name;
                        $scope.dragAndDropDirective.readers.push( reader );
                    }
                    if( $scope.initDragAndDrop )
                    {
                        /* Found initDragAndDrop function in controller */
                        $scope.initDragAndDrop();
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