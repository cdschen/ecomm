angular.module('ecommApp')

.controller('ProductController', ['$scope', 'Product', 'Utils', 'Process', 'ObjectProcess',
    function($scope, Product, Utils, Process, ObjectProcess) {

        var t = $.now();

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
            size: 20,
            sort: ['name'],
            product: {
                sku: '',
                name: '',
                enabled: true
            },
            statuses: []
        };

        $scope.query = angular.copy($scope.defaultQuery);

        $scope.processes = [];
        $scope.detailsSlideChecked = false;
        $scope.processSlideChecked = false;
        $scope.statusSlideChecked = false;

        Process.getAll({
            enabled: true,
            objectType: 2
        }).then(function(processes) {
            $scope.processes = processes;
            Process.initStatus(processes);
        });

        $scope.searchData = function(query, number) {
            Product.get({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort,
                nameOrSku: query.product.nameOrSku,
                enabled: query.product.enabled,
                statusIds: Process.refreshStatus(query.statuses),
                action: 'getInventories'
            }, function(page) {
                $scope.page = page;
                console.log(page);
                Utils.initList(page, query);
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

        /*
         *  Status
         */

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

        /*
         *  Process
         */

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

        /*
         *  details
         */

        $scope.toggleDetailsSlide = function(product) {
            $scope.detailsSlideChecked = !$scope.detailsSlideChecked;
            $('body').css('overflow', 'auto');
            $('div[ps-open="detailsSlideChecked"]').css('overflow', 'hidden');
            if ($scope.detailsSlideChecked) {
                $('body').css('overflow', 'hidden');
                $('div[ps-open="detailsSlideChecked"]').css('overflow', 'auto');
<<<<<<< HEAD
                Product.get({
                    id: product.id
                }, function(product){
                     $scope.processProduct = product;
                });
=======
                $scope.processProduct = product;
>>>>>>> steven20151121
            }
        };

    }
]);
