angular.module('ecommApp')

.controller('ProductController', ['$scope', 'Product', 'Utils', 'Process', 'ObjectProcess',
    function($scope, Product, Utils, Process, ObjectProcess) {

        var $ = angular.element,
            t = $.now();

        $scope.template = {
            details: {
                url: 'views/product/product/product.details-slide.html?' + t
            },
            process: {
                url: 'views/product/product/product.process-slide.html?' + t
            },
            status: {
                url: 'views/product/product/product.status-slide.html?' + t
            },
            popover: {
                url: 'process-tmpl.html'
            }
        };

        $scope.defaultQuery = {
            pageSize: 20,
            totalPagesList: [],
            sort: ['name'],
            product: {
                sku: '',
                name: '',
                deleted: false
            },
            statuses: []
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.processes = [];
        $scope.detailsSlideChecked = false;
        $scope.processSlideChecked = false;
        $scope.statusSlideChecked = false;

        Process.getAll({
            deleted: false,
            objectType: 2
        }).then(function(processes) {
            $scope.processes = processes;
            Process.initStatus(processes);
        });

        $scope.searchData = function(query, number) {
            Product.get({
                page: number ? number : 0,
                size: query.pageSize,
                sort: query.sort,
                sku: query.product.sku,
                name: query.product.name,
                deleted: query.product.deleted,
                statusIds: Process.refreshStatus(query.statuses)
            }, function(page) {
                $scope.page = page;
                query.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        $scope.search = function(query) {
            $scope.searchData(query);
        };

        $scope.reset = function() {
            $scope.query = angular.copy($scope.defaultQuery);
            $scope.searchData($scope.query);
        };

        // status
        $scope.toggleStatusSlide = function() {
            $scope.statusSlideChecked = !$scope.statusSlideChecked;
        };

        $scope.selectStatus = function(step) {
            if (step.selected && step.selected === true) {
                step.selected = false;
                $.each($scope.query.statuses, function(i) {
                    if (this.id === step.id) {
                        $scope.query.statuses.splice(i, 1);
                        return false;
                    }
                });
            } else {
                step.selected = true;
                $scope.query.statuses.push(step);
            }
        };

        // process
        $scope.toggleProcessSlide = function(product) {
            $scope.processSlideChecked = !$scope.processSlideChecked;
            if ($scope.processSlideChecked) {
                $scope.processProduct = product;
                $scope.processProduct.active = true;
            } else {
                if ($scope.processProduct) {
                    $scope.processProduct.active = false;
                }
            }
        };

        $scope.updateStep = function(product) {
            $scope.processProduct = product;
            console.log('updateStep');
            console.log(product);
        };

        $scope.saveUpdateStep = function(process, stepId) {
            process.step.id = stepId;
            ObjectProcess.save({}, process, function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    $scope.processProduct.processes = angular.copy(objectProcesses);
                });
            });
        };

        // details
        $scope.toggleDetailsSlide = function(product) {
            $scope.detailsSlideChecked = !$scope.detailsSlideChecked;
            if ($scope.detailsSlideChecked) {
                $scope.processProduct = product;
                $scope.defaultHeight = {
                    height: $(window).height() - 100
                };
            }
        };

    }
]);
