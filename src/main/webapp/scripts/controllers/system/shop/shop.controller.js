angular.module('ecommApp')

.controller('ShopController', ['$scope', 'Shop', 'Utils', 'Process',
    function($scope, Shop, Utils, Process) {

        var t = $.now();

        $scope.template = {
            process: {
                url: 'views/system/shop/shop.process-slide.html?' + t
            }
        };

        $scope.processSlideChecked = false;
        $scope.processShop = undefined;
        $scope.action = undefined;

        Process.getAll({
            enabled: true,
            objectType: 1
        }).then(function(processes) {
            $scope.processes = processes;
        });

        $scope.defaultQuery = {
            size: 20,
            sort: ['name']
        };
        $scope.query = angular.copy($scope.defaultQuery);

        $scope.searchData = function(query, number) {
            Shop.get({
                page: number ? number : 0,
                size: query.size,
                sort: query.sort
            }, function(page) {
                $scope.page = page;
                Utils.initList(page, query);
            });
        };

        $scope.searchData($scope.query);

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                $scope.searchData($scope.query, number);
            }
        };

        /*
         * Process
         */

        $scope.toggleProcessSlide = function(shop, action) {
            $scope.processSlideChecked = !$scope.processSlideChecked;
            if ($scope.processSlideChecked) {
                $scope.action = action;
                $scope.processShop = shop;
                $scope.processShop.active = true;
            } else {
                if ($scope.processShop) {
                    $scope.processShop.active = false;
                }
            }
        };

        $scope.applyState = function(step) {
            if ($scope.action === 'init') {
                $scope.processShop.initStep = angular.copy(step);
            } else if ($scope.action === 'deploy') {
                $scope.processShop.deployStep = angular.copy(step);
            } else if ($scope.action === 'complete') {
                $scope.processShop.completeStep = angular.copy(step);
            } else if ($scope.action === 'error') {
                $scope.processShop.errorStep = angular.copy(step);
            }
            Shop.save({}, $scope.processShop, function() {
                $scope.toggleProcessSlide();
            });
        };

    }
]);
