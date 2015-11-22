angular.module('ecommApp')

.controller('ShipmentImportController', ['$scope', '$timeout', 'toastr', 'shipmentService', '$rootScope',
    function( $scope, $timeout, toastr, shipmentService, $rootScope )
    {
        $scope.operationReviewImportShipment = shipmentService.getOperationReviewImportShipments;

        String.prototype.endsWith = function(suffix)
        {
            return this.indexOf(suffix, this.length - suffix.length) !== -1;
        };

        $scope.emptyOrderErrorTableShow = false;
        $scope.exceptionalShipmentTableShow = true;

        $scope.shipments = [];

        $scope.initDragAndDrop = function()
        {
            $scope.shipments = [];

            var readers = $scope.dragAndDropDirective.readers;
            for( var readerIndex in readers )
            {
                var reader = readers[ readerIndex ];
                if( reader.name.endsWith('.xls') || reader.name.endsWith('.xlsx') )
                {
                    importShipmentsVerify( reader );
                }
                else
                {
                    toastr.warning('［' + reader.name + '］文件格式不正确，请拖拽后缀为［.xls］或［.xlsx］的文件');
                }
            }
        };

        function getShipStatus( shipStatus )
        {
            var intShipStatus = 0;

            switch( shipStatus )
            {
                case '正常'   :   intShipStatus = 1; break;
                case '异常'   :   intShipStatus = 5; break;
                default       :   intShipStatus = 5; break;
            }

            return intShipStatus;
        }

        function importShipmentsVerify( reader )
        {
            reader.onload = function(e)
            {
                $timeout(function()
                {
                var preparedShipments = [];

                var data = e.target.result;
                var workbook = XLSX.read( data, { type: 'binary' } );
                workbook.SheetNames.forEach( function( sheetName )
                {
                    preparedShipments = XLSX.utils.sheet_to_json( workbook.Sheets[ sheetName ] );
                });

                /* 如果有记录 */
                if( preparedShipments.length > 0 )
                {
                    for( var preparedShipmentIndex in preparedShipments )
                    {
                        var shipment =
                        {
                            'receiveName' : preparedShipments[ preparedShipmentIndex ].收货人,
                            'receivePhone' : preparedShipments[ preparedShipmentIndex ].电话号 ? preparedShipments[ preparedShipmentIndex ].电话号 : preparedShipments[ preparedShipmentIndex ].手机号,
                            'receiveAddress' : preparedShipments[ preparedShipmentIndex ].地址,
                            'productContent' : preparedShipments[ preparedShipmentIndex ].物品内容,
                            'orderId'   :   preparedShipments[ preparedShipmentIndex ].邮编,
                            'shipNumber'    :   preparedShipments[ preparedShipmentIndex ].快递单号,
                            'id' : preparedShipments[ preparedShipmentIndex ].发货单号,
                            'shipStatus' : getShipStatus( preparedShipments[ preparedShipmentIndex ].状态 ),
                            'memo' : preparedShipments[ preparedShipmentIndex ].备注
                        };

                        $scope.shipments.push( shipment );
                    }

                    /* 将批量发货单数据传送至后台并进行复核验证
                     */
                    var reviewDTO = {
                        action                    : 'VERIFY',
                        shipments                 : $scope.shipments,
                        dataMap : {
                            operatorId            : $rootScope.user().id
                        }
                    };
                    shipmentService.confirmOperationReviewWhenImportShipment( reviewDTO ).then(function(review){
                        console.log('After Operation Review:');
                        console.log(review);
                    });
                }
                else
                {
                    toastr.warning('抱歉，没有可以导入的数据');
                }
                });
            };
        }


        /* 点击将某个发货单的 ignoreCheck 标为  ! ignoreCheck，在进行复核验证时不再对该发货单进行验证 */
        $scope.ignoreOrNotCheckOrder = function( shiment )
        {
            shiment.ignoreCheck = ! shiment.ignoreCheck;
            var operationReview = shipmentService.getOperationReviewImportShipments();
            var reviewDTO = {
                'action' : 'VERIFY',
                'shipments' : operationReview.shipments,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            shipmentService.confirmOperationReviewWhenImportShipment( reviewDTO ).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

        /* 点击将某个发货单的 ignoreCheck 标为  ! ignoreCheck，在进行复核验证时不再对该发货单进行验证 */
        $scope.verifySavedChanges = function()
        {
            var operationReview = shipmentService.getOperationReviewImportShipments();
            var reviewDTO = {
                'action' : 'VERIFY',
                'shipments' : operationReview.shipments,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            shipmentService.confirmOperationReviewWhenImportShipment( reviewDTO ).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };

        $scope.confirmImportShipments = function()
        {
            /* 将批量发货单数据传送至后台并进行复核确认验证
             */
            var operationReview = shipmentService.getOperationReviewImportShipments();
            var reviewDTO = {
                'action' : 'CONFIRM',
                'shipments' : operationReview.shipments,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            shipmentService.confirmOperationReviewWhenImportShipment( reviewDTO ).then(function(review){
                console.log('After Operation Review:');
                console.log(review);

                /*  如果存在未匹配到订单的发货单
                 */
                if( review.checkMap.emptyOrderError )
                {
                    toastr.error('存在未匹配订单的发货单，请点击［未找到订单的发货单］右侧的［展开列表］按钮来单个移出，或直接点击旁边的［全部移出］按钮再继续');
                }
                else
                {
                    //if(  )
                    //{
                    //
                    //}
                    toastr.success('成功导入 ' + review.resultMap.insertableShipmentsSize + ' 条发货单记录');
                }

            });
        };

        $scope.moveAllEmptyOrderErrorShipment = function( shipments )
        {
            for( var shipmentIndex in shipments )
            {
                var shipment = shipments[ shipmentIndex ];
                if( shipment.checkMap.emptyOrderError || shipment.ignoreCheck )
                {
                    shipments[ shipmentIndex ].ignoreCheck = true;
                }
            }
            var operationReview = shipmentService.getOperationReviewImportShipments();
            var reviewDTO = {
                'action' : 'VERIFY',
                'shipments' : operationReview.shipments,
                'checkMap' : operationReview.checkMap,
                'dataMap' : operationReview.dataMap,
                'ignoredMap' : operationReview.ignoredMap
            };
            shipmentService.confirmOperationReviewWhenImportShipment( reviewDTO ).then(function(review){
                console.log('After Operation Review:');
                console.log(review);
            });
        };
    }
]);