angular.module('ecommApp')

.controller('ShopOperatorController', ['$rootScope', '$scope', '$state', '$stateParams', 'User', 'Language', 'Currency', 'Shop', 'Supplier', 'Warehouse',
    function($rootScope, $scope, $state, $stateParams, User, Language, Currency, Shop, Supplier, Warehouse) {

        var $ = angular.element;
        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.template = {
            tunnel: {
                url: 'views/system/shop/shop.operator.tunnel.html?' + (new Date()),
                warehouse: {
                    url: 'views/system/shop/shop.operator.tunnel.warehouse.html?' + (new Date()),
                }
            }
        };
        $scope.users = [];
        $scope.languages = [];
        $scope.currencies = [];
        $scope.warehouses = [];
        $scope.suppliers = [];
        $scope.types = [{
            label: '自营',
            value: 0,
        }, {
            label: '合作',
            value: 1
        }];
        $scope.statuses = [{
            label: '禁用',
            value: 0
        }, {
            label: '正常',
            value: 1
        }];
        $scope.lvls = [{
            label: 'Level 0',
            value: 0
        }, {
            label: 'Level 1',
            value: 1
        }, {
            label: 'Level 2',
            value: 2
        }, {
            label: 'Level 3',
            value: 3
        }, {
            label: 'Level 4',
            value: 4
        }, {
            label: 'Level 5',
            value: 5
        }, {
            label: 'Level 6',
            value: 6
        }, {
            label: 'Level 7',
            value: 7
        }, {
            label: 'Level 8',
            value: 8
        }, {
            label: 'Level 9',
            value: 9
        }, {
            label: 'Level 10',
            value: 10
        }, ];
        $scope.shop = {
            type: 0,
            status: 1,
            apiCallLimit: -1,
            user: undefined,
            language: undefined,
            currency: undefined,
            priceLevel: 0,
            tunnels: [],
            deleted: false
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
        $scope.defaultTunnel = {
            warehouses: [],
            suppliers: []
        };

        $scope.action = 'create';
        $scope.tunnel = angular.copy($scope.defaultTunnel);

        function initShopField(shop) {
            shop.type = $scope.types[shop.type];
            shop.status = $scope.statuses[shop.status];
            shop.priceLevel = $scope.lvls[shop.priceLevel];
        }

        function refreshShopField(shop) {
            shop.type = shop.type.value;
            shop.status = shop.status.value;
            shop.priceLevel = shop.priceLevel.value;
        }

        function initTunnelField(tunnel) {
            tunnel.type = $scope.tunnelTypes[tunnel.type - 1];
            tunnel.behavior = $scope.tunnelBehaviors[tunnel.behavior - 1];
            tunnel.defaultOption = $scope.tunnelDefaultOptions[tunnel.defaultOption ? 0 : 1];
            if (tunnel.type.value === 1) {
                $.each(tunnel.warehouses, function() {
                    if (this.id === tunnel.defaultWarehouseId) {
                        this.defaultOption = true;
                        return false;
                    }
                });
            } else if (tunnel.type.value === 2) {
                $.each(tunnel.suppliers, function() {
                    if (this.id === tunnel.defaultSupplierId) {
                        this.defaultOption = true;
                        return false;
                    }
                });
            }
        }

        function refreshTunnelField(tunnel) {
            tunnel.type = tunnel.type.value;
            tunnel.behavior = tunnel.behavior.value;
            tunnel.defaultOption = tunnel.defaultOption.value;
        }

        User.getAll().then(function(users) {
            $scope.users = users;
        }).then(function() {
            return Language.getAll().then(function(languages) {
                $scope.languages = languages;
            });
        }).then(function() {
            return Currency.getAll().then(function(currencies) {
                $scope.currencies = currencies;
            });
        }).then(function() {
            return Warehouse.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(warehouses) {
                $scope.warehouses = warehouses;
            });
        }).then(function() {
            return Supplier.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(suppliers) {
                $scope.suppliers = suppliers;
            });
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                Shop.get({
                    id: $stateParams.id
                }, {}, function(shop) {
                    initShopField(shop);
                    $scope.shop = shop;
                    angular.forEach($scope.shop.tunnels, function(tunnel) {
                        initTunnelField(tunnel);
                    });
                });
            } else {
                initShopField($scope.shop);
            }
        });

        $scope.saveShop = function(shop) {

            refreshShopField(shop);

            angular.forEach(shop.tunnels, function(tunnel) {
                refreshTunnelField(tunnel);
            });

            Shop.save({}, shop, function() {
                $state.go('shop');
            }, function(err) {
                console.log(err);
            });
        };

        // $scope.remove = function() {
        //     Shop.remove({
        //         id: $stateParams.id
        //     }, {}, function() {
        //         $state.go('shop');
        //     }, function(err) {
        //         console.log(err);
        //     });
        // };

        function setDefaultToFalse(tunnels) {
            $.each(tunnels, function() {
                this.defaultOption = $scope.tunnelDefaultOptions[1];
            });
        }

        function setTunnelDefaultWarehouseIdAndSupplierId(tunnel) {
            if (tunnel.type.value === 1) {
                $.each(tunnel.warehouses, function() {
                    if (this.defaultOption) {
                        tunnel.defaultWarehouseId = this.id;
                        return false;
                    }
                });
            } else if (tunnel.type.value === 2) {
                $.each(tunnel.suppliers, function() {
                    if (this.defaultOption) {
                        tunnel.defaultSupplierId = this.id;
                        return false;
                    }
                });
            }
        }

        $scope.saveTunnel = function(tunnel, tunnelAddForm) {

            if (tunnel.defaultOption.value) {
                setDefaultToFalse($scope.shop.tunnels);
            }
            setTunnelDefaultWarehouseIdAndSupplierId(tunnel);
            $scope.shop.tunnels.push(angular.copy(tunnel));
            tunnelAddForm.$setPristine();
            $scope.tunnel = angular.copy($scope.defaultTunnel);
            $scope.closeTunnelWarehouseSlide();
        };

        $scope.updateTunnel = function(tunnel) {
            tunnel.editable = true;
        };

        $scope.saveUpdateTunnel = function(tunnel, tunnelForm) {
            if (tunnel.type.value === 1) {
                tunnel.suppliers = undefined;
                $scope.closeTunnelWarehouseSlide();
            } else if (tunnel.type.value === 2) {
                tunnel.warehouses = undefined;
                $scope.closeTunnelSupplierSlide();
            }
            setTunnelDefaultWarehouseIdAndSupplierId(tunnel);

            if (tunnel.defaultOption.value) {
                setDefaultToFalse($scope.shop.tunnels);
                tunnel.defaultOption = $scope.tunnelDefaultOptions[0];
            }
            tunnel.editable = false;
            tunnelForm.$setPristine();
        };

        $scope.removingTunnel = undefined;

        $scope.showRemoveTunnel = function(tunnel, $index) {
            $scope.removingTunnel = tunnel;
            $scope.removingTunnel.$index = $index;
            $('#tunnelDeleteModal').modal('show');
        };

        $scope.removeTunnel = function() {
            if (angular.isDefined($scope.removingTunnel)) {
                $scope.shop.tunnels.splice($scope.removingTunnel.$index, 1);
                $scope.removingTunnel = undefined;
                $('#tunnelDeleteModal').modal('hide');
            }
        };

        // slide
        $scope.defaultSelected = {
            warehouses: [],
            suppliers: []
        };
        $scope.selected = angular.copy($scope.defaultSelected);
        $scope.tunnelWarehouseSlideChecked = false;
        $scope.tunnelSupplierSlideChecked = false;

        // warehouse slide
        $scope.operateWarehouses = [];

        $scope.closeTunnelWarehouseSlide = function() {
            $scope.tunnelWarehouseSlideChecked = false;
        };

        $scope.loadTunnelWarehouses = function(tunnelWarehouses) {
            $scope.selected.warehouses = tunnelWarehouses;
            $scope.tunnelWarehouseSlideChecked = true;
            $scope.operateWarehouses.length = 0;
            $.each($scope.warehouses, function() {
                $scope.operateWarehouses.push(angular.copy(this));
            });
            $.each(tunnelWarehouses, function() {
                var tunnelWarehouse = this;
                $.each($scope.operateWarehouses, function() {
                    if (this.id === tunnelWarehouse.id) {
                        this.selected = true;
                        return false;
                    }
                });
            });
        };

        $scope.selectTunnelDeployWarehouse = function(warehouse) {
            if (warehouse.selected && warehouse.selected === true) {
                warehouse.selected = false;
                $.each($scope.selected.warehouses, function(i) {
                    if (this.id === warehouse.id) {
                        $scope.selected.warehouses.splice(i, 1);
                        return false;
                    }
                });
            } else {
                warehouse.selected = true;
                $scope.selected.warehouses.push(warehouse);
            }
        };

        $scope.setTunnelDefaultWarehouse = function(warehouse, tunnel) {
            $.each(tunnel.warehouses, function() {
                this.defaultOption = false;
            });
            warehouse.defaultOption = true;
            tunnel.defaultWarehouseId = warehouse.id;
        };

        // supplier slide
        $scope.operateSuppliers = [];

        $scope.closeTunnelSupplierSlide = function() {
            $scope.tunnelSupplierSlideChecked = false;
        };

        $scope.loadTunnelSuppliers = function(tunnelSuppliers) {
            $scope.selected.suppliers = tunnelSuppliers;
            $scope.tunnelSupplierSlideChecked = true;
            $scope.operateSuppliers.length = 0;
            $.each($scope.suppliers, function() {
                $scope.operateSuppliers.push(angular.copy(this));
            });
            $.each(tunnelSuppliers, function() {
                var tunnelsupplier = this;
                $.each($scope.operateSuppliers, function() {
                    if (this.id === tunnelsupplier.id) {
                        this.selected = true;
                        return false;
                    }
                });
            });
        };

        $scope.selectTunnelDeploySupplier = function(supplier) {
            if (supplier.selected && supplier.selected === true) {
                supplier.selected = false;
                $.each($scope.selected.suppliers, function(i) {
                    if (this.id === supplier.id) {
                        $scope.selected.suppliers.splice(i, 1);
                        return false;
                    }
                });
            } else {
                supplier.selected = true;
                $scope.selected.suppliers.push(supplier);
            }
        };

        $scope.setTunnelDefaultSupplier = function(supplier, tunnel) {
            $.each(tunnel.suppliers, function() {
                this.defaultOption = false;
            });
            supplier.defaultOption = true;
            tunnel.defaultSupplierId = supplier.id;
        };

    }
]);
