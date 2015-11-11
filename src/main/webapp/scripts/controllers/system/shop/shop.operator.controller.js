angular.module('ecommApp')

.controller('ShopOperatorController', ['$scope', '$state', '$stateParams', 'User', 'Language', 'Currency', 'Shop', 'Supplier', 'Warehouse', 'toastr',
    function($scope, $state, $stateParams, User, Language, Currency, Shop, Supplier, Warehouse, toastr) {

        var t = $.now();
        $scope.action = 'create';
        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.template = {
            tunnel: {
                url: 'views/system/shop/shop.operator.tunnel.html?' + t,
                warehouse: {
                    url: 'views/system/shop/shop.operator.tunnel.warehouse.html?' + t
                },
                supplier: {
                    url: 'views/system/shop/shop.operator.tunnel.supplier.html?' + t
                }
            }
        };

        /*
         *  Init Shop
         */

        $scope.types = [{
            label: '自营',
            value: 0,
        }, {
            label: '合作',
            value: 1
        }];

        $scope.isorno = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];

        $scope.lvls = [];
        $.each([0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10], function() {
            $scope.lvls.push({
                label: 'Level ' + this,
                value: this
            });
        });

        $scope.defaultShop = {
            type: {
                label: '自营',
                value: 0
            },
            apiCallLimit: -1,
            language: undefined,
            currency: undefined,
            priceLevel: {
                label: 'Level 0',
                value: 0
            },
            enabled: {
                label: '是',
                value: true
            },
            tunnels: []
        };

        $scope.shop = angular.copy($scope.defaultShop);

        $scope.changeShopType = function($item) {
            if ($item.value === 0) {
                $scope.shop.priceLevel = $scope.lvls[0];
            } else if ($item.value === 1) {
                $scope.shop.priceLevel = $scope.lvls[1];
            }
        };

        function initShopProperties(shop) {
            shop.type = $scope.types[shop.type];
            shop.priceLevel = $scope.lvls[shop.priceLevel];
            shop.enabled = $scope.isorno[shop.enabled ? 0 : 1];
        }

        function refreshShopProperties(shop) {
            shop.type = shop.type.value;
            shop.priceLevel = shop.priceLevel.value;
            shop.enabled = shop.enabled.value;
        }

        /*
         *  Init Tunnel
         */

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

        $scope.defaultTunnel = {
            defaultWarehouse: undefined,
            defaultSupplier: undefined,
            warehouses: [],
            suppliers: []
        };

        $scope.tunnel = angular.copy($scope.defaultTunnel);

        function initTunnelProperties(tunnel) {
            tunnel.type = $scope.tunnelTypes[tunnel.type - 1];
            tunnel.behavior = $scope.tunnelBehaviors[tunnel.behavior - 1];
            tunnel.defaultOption = $scope.isorno[tunnel.defaultOption ? 0 : 1];
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

        function refreshTunnelProperties(tunnel) {
            tunnel.type = tunnel.type.value;
            tunnel.behavior = tunnel.behavior.value;
            tunnel.defaultOption = tunnel.defaultOption.value;
        }

        /*
         *  Shop
         */

        Language.getAll().then(function(languages) {
            $scope.languages = languages;
        });

        Currency.getAll().then(function(currencies) {
            $scope.currencies = currencies;
        });

        Warehouse.getAll({
            enabled: true,
            sort: ['name']
        }).then(function(warehouses) {
            $scope.warehouses = warehouses;
        });

        Supplier.getAll({
            enabled: true,
            sort: ['name']
        }).then(function(suppliers) {
            $scope.suppliers = suppliers;
        });

        if ($stateParams.id && $stateParams.id !== '') {
            $scope.action = 'update';
            Shop.get({
                id: $stateParams.id
            }, {}, function(shop) {
                $scope.shop = shop;
                initShopProperties(shop);
                $.each(shop.tunnels, function() {
                    initTunnelProperties(this);
                });
            });
        }

        $scope.saveShop = function(shop) {
            if (shop.tunnels.length > 0) {
                refreshShopProperties(shop);
                $.each(shop.tunnels, function() {
                    refreshTunnelProperties(this);
                });
                Shop.save({}, shop, function() {
                    $state.go('shop');
                });
            } else {
                toastr.warning('至少设置一种配货方式');
            }
        };

        /*
         *  Tunnel
         */

        function setDefaultToFalse(tunnels) {
            $.each(tunnels, function() {
                this.defaultOption = $scope.isorno[1];
            });
        }

        function setTunnelDefaultWarehouseIdAndSupplierId(tunnel) {
            if (tunnel.type.value === 1) {
                var existDefaultWarehouse = false;
                $.each(tunnel.warehouses, function() {
                    if (this.defaultOption) {
                        tunnel.defaultWarehouse = this;
                        existDefaultWarehouse = true;
                        return false;
                    }
                });
                if (!existDefaultWarehouse) {
                    tunnel.warehouses[0].defaultOption = true;
                    tunnel.defaultWarehouse = tunnel.warehouses[0];
                }
            } else if (tunnel.type.value === 2) {
                var existDefaultSupplier = false;
                $.each(tunnel.suppliers, function() {
                    if (this.defaultOption) {
                        tunnel.defaultSupplier = this;
                        existDefaultSupplier = true;
                        return false;
                    }
                });
                if (!existDefaultSupplier) {
                    tunnel.suppliers[0].defaultOption = true;
                    tunnel.defaultSupplier = tunnel.suppliers[0];
                }
            }
        }

        function checkTunnelWarehouseAndSuppliers(tunnel) {
            if (tunnel.type.value === 1) {
                if (tunnel.warehouses.length === 0) {
                    toastr.warning('仓库配货方式, 至少设置一家仓库');
                    return false;
                }
            } else if (tunnel.type.value === 2) {
                if (tunnel.suppliers.length === 0) {
                    toastr.warning('供应商配货方式, 至少设置一家供应商');
                    return false;
                }
            }
            return true;
        }


        $scope.saveTunnel = function(tunnel, tunnelAddForm) {

            if (!checkTunnelWarehouseAndSuppliers(tunnel)) {
                return;
            }

            if (tunnel.defaultOption.value) {
                setDefaultToFalse($scope.shop.tunnels);
            }
            setTunnelDefaultWarehouseIdAndSupplierId(tunnel);
            $scope.shop.tunnels.push(angular.copy(tunnel));
            tunnelAddForm.$setPristine();
            $scope.tunnel = angular.copy($scope.defaultTunnel);
            $scope.tunnelWarehouseSlideChecked = false;
            $scope.tunnelSupplierSlideChecked = false;
        };

        $scope.updateTunnel = function(tunnel) {
            tunnel.editable = true;
        };

        $scope.saveUpdateTunnel = function(tunnel, tunnelForm) {

            if (!checkTunnelWarehouseAndSuppliers(tunnel)) {
                return;
            }

            if (tunnel.type.value === 1) {
                tunnel.suppliers = undefined;
                $scope.tunnelWarehouseSlideChecked = false;
            } else if (tunnel.type.value === 2) {
                tunnel.warehouses = undefined;
                $scope.tunnelSupplierSlideChecked = false;
            }
            setTunnelDefaultWarehouseIdAndSupplierId(tunnel);

            if (tunnel.defaultOption.value) {
                setDefaultToFalse($scope.shop.tunnels);
                tunnel.defaultOption = $scope.isorno[0];
            }
            tunnel.editable = false;
            tunnelForm.$setPristine();
        };

        $scope.showRemoveTunnel = function(tunnel) {
            $scope.removingTunnel = tunnel;
            $('#tunnelDeleteModal').modal('show');
        };

        $scope.removeTunnel = function() {
            if (angular.isDefined($scope.removingTunnel)) {
                $.each($scope.shop.tunnels, function(index){
                    if (this.name === $scope.removingTunnel.name) {
                        $scope.shop.tunnels.splice(index, 1);
                        return false;
                    }
                });
                
                $scope.removingTunnel = undefined;
                $('#tunnelDeleteModal').modal('hide');
            }
        };

        /*
         *  Init Slide
         */

        $scope.defaultSelected = {
            warehouses: [],
            suppliers: []
        };
        $scope.selected = angular.copy($scope.defaultSelected);
        $scope.tunnelWarehouseSlideChecked = false;
        $scope.tunnelSupplierSlideChecked = false;
        $scope.optionalWarehouses = [];
        $scope.optionalSuppliers = [];

        /*
         *  Warehouse Slide
         */

        $scope.toggleTunnelWarehouseSlide = function(tunnelWarehouses) {
            $scope.tunnelWarehouseSlideChecked = !$scope.tunnelWarehouseSlideChecked;
            if ($scope.tunnelWarehouseSlideChecked) {
                $scope.selected.warehouses = tunnelWarehouses;
                $scope.optionalWarehouses.length = 0;
                $.each($scope.warehouses, function() {
                    $scope.optionalWarehouses.push(angular.copy(this));
                });
                $.each(tunnelWarehouses, function() {
                    var tunnelWarehouse = this;
                    $.each($scope.optionalWarehouses, function() {
                        if (this.id === tunnelWarehouse.id) {
                            this.selected = true;
                            return false;
                        }
                    });
                });
            }
        };

        $scope.toggleWarehouse = function(warehouse) {
            if (warehouse.selected && warehouse.selected === true) {
                warehouse.selected = false;
                warehouse.defaultOption = false;
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
            tunnel.defaultWarehouse = warehouse;
        };

        /*
         *  Supplier Slide
         */

        $scope.toggleTunnelSupplierSlide = function(tunnelSuppliers) {
            $scope.tunnelSupplierSlideChecked = !$scope.tunnelSupplierSlideChecked;
            if ($scope.tunnelSupplierSlideChecked) {
                $scope.selected.suppliers = tunnelSuppliers;
                $scope.optionalSuppliers.length = 0;
                $.each($scope.suppliers, function() {
                    $scope.optionalSuppliers.push(angular.copy(this));
                });
                $.each(tunnelSuppliers, function() {
                    var tunnelsupplier = this;
                    $.each($scope.optionalSuppliers, function() {
                        if (this.id === tunnelsupplier.id) {
                            this.selected = true;
                            return false;
                        }
                    });
                });
            }
        };

        $scope.toggleSupplier = function(supplier) {
            if (supplier.selected && supplier.selected === true) {
                supplier.selected = false;
                supplier.defaultOption = false;
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
            tunnel.defaultSupplier = supplier;
        };

    }
]);
