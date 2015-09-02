'use strict';

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
                url: 'views/system/shop/shop.process.html?' + (new Date())
            }
        };

        Shop.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['name']
        }).$promise.then(function(page) {
            console.clear();
            console.log('page:');
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        }).then(function() {
            Process.getAll({
                deleted: false,
                objectType: 1
            }).then(function(processes) {
                console.log('Process.getAll:');
                console.log(processes);
                $scope.processes = processes;
                //initStatus(processes);
            });
        });

        $scope.turnPage = function(number) {
            if (number > -1 && number < $scope.page.totalPages) {
                Shop.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['name']
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        // process

        $scope.colseProcessSlide = function() {
            $scope.processSlideChecked = false;
            if ($scope.processShop) {
                $scope.processShop.active = false;
            }
        };

        $scope.loadProcesses = function(shop, action) {
            console.clear();
            console.log('loadProcesses:');
            console.log(shop);
            $scope.action = action;
            $scope.processSlideChecked = true;
            $scope.processShop = shop;
            $scope.processShop.active = true;
        };

        $scope.applyState = function(step) {
            console.clear();
            console.log('applyState:[' + $scope.action + ']');
            console.log(step);
            if ($scope.action === 'init') {
                $scope.processShop.initStep = angular.copy(step);
            } else if ($scope.action === 'complete') {
                $scope.processShop.completeStep = angular.copy(step);
            } else if ($scope.action === 'error') {
                $scope.processShop.errorStep = angular.copy(step);
            }
            console.log($scope.processShop);
            Shop.save({}, $scope.processShop, function(shop){
                console.log('applyState complete:');
                console.log(shop);
                $scope.colseProcessSlide();
            });
        };
    }
])

.controller('ShopOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'User', 'Language', 'Currency', 'Shop', 'Supplier', 'Warehouse',
    function($rootScope, $scope, $state, $stateParams, User, Language, Currency, Shop, Supplier, Warehouse) {

        var $ = angular.element;
        $scope.users = [];
        $scope.languages = [];
        $scope.currencies = [];
        $scope.warehouses = [];
        $scope.suppliers = [];
        $scope.types = [{
            id: 0,
            label: '自营'
        }, {
            id: 1,
            label: '合作'
        }];
        $scope.statuses = [{
            id: 0,
            label: '禁用'
        }, {
            id: 1,
            label: '正常'
        }];
        $scope.lvls = [{
            id: 0,
            label: 'Level 0'
        }, {
            id: 1,
            label: 'Level 1'
        }, {
            id: 2,
            label: 'Level 2'
        }, {
            id: 3,
            label: 'Level 3'
        }, {
            id: 4,
            label: 'Level 4'
        }, {
            id: 5,
            label: 'Level 5'
        }, {
            id: 6,
            label: 'Level 6'
        }, {
            id: 7,
            label: 'Level 7'
        }, {
            id: 8,
            label: 'Level 8'
        }, {
            id: 9,
            label: 'Level 9'
        }, {
            id: 10,
            label: 'Level 10'
        }, ];
        $scope.shop = {
            type: 0,
            status: 1,
            apiCallLimit: -1,
            language: {
                id: 100
            },
            currency: {
                id: 101
            },
            priceLevel: 0,
            tunnels: []
        };
        $scope.tunnelTypes = [{
            label: '仓库配货方式(自营)',
            value: 1
        }, {
            label: '供应商配货方式(代发)',
            value: 2
        }];
        $scope.tunnelBehaviors = [{
            label: '包括',
            value: 1
        }, {
            label: '排除',
            value: 2
        }];
        $scope.tunnelDefaultOptions = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];
        $scope.tunnel = {};

        $scope.action = 'create';

        function initField(tunnel) {
            tunnel.type = $scope.tunnelTypes[tunnel.type - 1];
            tunnel.behavior = $scope.tunnelBehaviors[tunnel.behavior - 1];
            tunnel.defaultOption = $scope.tunnelDefaultOptions[tunnel.defaultOption - 1];
        }

        function refreshField(tunnel) {
            tunnel.type = tunnel.type.value;
            tunnel.behavior = tunnel.behavior.value;
            tunnel.defaultOption = tunnel.defaultOption.value;
        }

        console.clear();

        User.getAll().then(function(users) {
            console.log('users loaded');
            $scope.users = users;
        }).then(function() {
            return Language.getAll().then(function(languages) {
                console.log('languaegs loaded');
                $scope.languages = languages;
            });
        }).then(function() {
            return Currency.getAll().then(function(currencies) {
                console.log('currencies loaded');
                $scope.currencies = currencies;
            });
        }).then(function() {
            return Warehouse.getAll().then(function(warehouses) {
                console.log('warehouses loaded');
                $scope.warehouses = warehouses;
            });
        }).then(function() {
            return Supplier.getAll({
                deleted: false
            }).then(function(suppliers) {
                console.log('suppliers loaded');
                $scope.suppliers = suppliers;
            });
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                Shop.get({
                    id: $stateParams.id
                }, {}, function(shop) {
                    $scope.shop = shop;
                    angular.forEach($scope.shop.tunnels, function(tunnel) {
                        initField(tunnel);
                    });
                    console.log(shop);
                });
            }
        });

        $scope.saveShop = function(shop) {
            console.clear();
            console.log('saveShop:');

            angular.forEach(shop.tunnels, function(tunnel) {
                refreshField(tunnel);
            });

            console.log(shop);

            Shop.save({}, shop, function() {
                $state.go('shop');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.remove = function() {
            Shop.remove({
                id: $stateParams.id
            }, {}, function() {
                $state.go('shop');
            }, function(err) {
                console.log(err);
            });
        };

        $scope.saveTunnel = function(tunnelAddForm, tunnel) {
            console.clear();
            console.log('[' + $scope.action + '] saveTunnel complete:');
            console.log(tunnel);

            $scope.shop.tunnels.push(angular.copy(tunnel));
            tunnelAddForm.$setPristine();
            $scope.tunnel = angular.copy($scope.defaultTunnel);
        };

        $scope.updateTunnel = function(tunnel) {
            //console.clear();
            console.log('updateTunnel:');
            console.log(tunnel);
            tunnel.editable = true;
        };

        $scope.saveUpdateTunnel = function(tunnel, tunnelForm) {
            //console.clear();
            if (tunnel.type.value === 1) {
                tunnel.suppliers.length = 0;
            } else if (tunnel.type.value === 2) {
                tunnel.warehouses.length = 0;
            }
            console.log('[' + $scope.action + '] saveUpdateTunnel complete:');
            console.log(tunnel);
            tunnel.editable = false;
            tunnelForm.$setPristine();

        };

        $scope.removingTunnel = undefined;

        $scope.showRemoveTunnel = function(tunnel, $index) {
            console.clear();
            console.log('showRemoveTunnel $index: ' + $index);
            console.log(tunnel);

            $scope.removingTunnel = tunnel;
            $scope.removingTunnel.$index = $index;
            $('#tunnelDeleteModal').modal('show');
        };

        $scope.removeTunnel = function() {
            console.clear();
            console.log('removeTunnel:');
            console.log($scope.removingTunnel);

            if (angular.isDefined($scope.removingTunnel)) {
                $scope.shop.tunnels.splice($scope.removingTunnel.$index, 1);
                $scope.removingTunnel = undefined;
                $('#tunnelDeleteModal').modal('hide');
            }
        };
    }
]);
