angular.module('ecommApp')

.controller('ShopController', ['$rootScope', '$scope', 'Shop', 'Utils', 'Process',
    function($rootScope, $scope, Shop, Utils, Process) {

        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.processSlideChecked = false;
        $scope.processShop = undefined;
        $scope.action = undefined;

        $scope.template = {
            process: {
                url: 'views/system/shop/shop.process-slide.html?' + (new Date())
            }
        };

        Shop.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['name'],
            deleted: false
        }).$promise.then(function(page) {
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        }).then(function() {
            Process.getAll({
                deleted: false,
                objectType: 1
            }).then(function(processes) {
                $scope.processes = processes;
            });
        });

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Shop.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['name'],
                    deleted: false
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        // process
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
