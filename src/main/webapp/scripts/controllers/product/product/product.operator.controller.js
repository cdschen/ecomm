angular.module('ecommApp')

.controller('ProductOperatorController', ['$scope', '$state', '$stateParams', '$filter', 'Product', 'Brand', 'Category', 'Source', 'Language', 'Currency', 'Tag', 'Shop',
    function($scope, $state, $stateParams, $filter, Product, Brand, Category, Source, Language, Currency, Tag, Shop) {

        var t = $.now();

        $scope.action = 'create';
        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.template = {
            info: {
                url: 'views/product/product/product.operator.info.html?' + t,
                attr: {
                    url: 'views/product/product/product.operator.info.attr.html?' + t
                },
                price: {
                    url: 'views/product/product/product.operator.info.price.html?' + t
                },
                group: {
                    url: 'views/product/product/product.operator.info.group.html?' + t
                }
            },
            img: {
                url: 'views/product/product/product.operator.img.html?' + t
            },
            multiLanguage: {
                url: 'views/product/product/product.operator.multi-language.html?' + t
            },
            multiCurrency: {
                url: 'views/product/product/product.operator.multi-currency.html?' + t
            },
            shopTunnel: {
                url: 'views/product/product/product.operator.shop-tunnel.html?' + t
            }
        };

        $scope.productTypes = [{
            label: '简单商品',
            value: 0
        }, {
            label: '组合商品',
            value: 1
        }];

        $scope.shops = [];
        $scope.brands = [];
        $scope.categories = [];
        $scope.sources = [];
        $scope.languages = [];
        $scope.currencies = [];
        $scope.tags = [];
        $scope.members = [];

        $scope.defaultProduct = {
            weight: 0,
            priceL1: 0.00,
            priceL2: 0.00,
            priceL3: 0.00,
            priceL4: 0.00,
            priceL5: 0.00,
            priceL6: 0.00,
            priceL7: 0.00,
            priceL8: 0.00,
            priceL9: 0.00,
            priceL10: 0.00,
            productType: $scope.productTypes[0],
            multiLanguages: [],
            multiCurrencies: [],
            members: [],
            processes: [],
            shopTunnels: [],
            enabled: true
        };

        $scope.product = angular.copy($scope.defaultProduct);

        function initDefaultProductShopTunnel(tunnels) {
            $.each(tunnels, function() {
                this.selectedDefault = false;
            });
        }

        function setDefaultProductShopTunnel(shopTunnels) {
            $.each(shopTunnels, function() {
                var shopTunnel = this;
                $.each($scope.shops, function() {
                    var shop = this;
                    $.each(shop.tunnels, function() {
                        if (this.shopId === shopTunnel.shopId && this.id === shopTunnel.tunnelId) {
                            initDefaultProductShopTunnel(shop.tunnels);
                            this.selectedDefault = true;
                            return false;
                        }
                    });
                });
            });
        }

        Brand.getAll().then(function(brands) {
            $scope.brands = brands;
        });

        Category.getAll().then(function(categories) {
            $scope.categories = categories;
        });

        Source.getAll().then(function(sources) {
            $scope.sources = sources;
        });

        Language.getAll().then(function(languages) {
            $scope.languages = languages;
        });

        Currency.getAll().then(function(currencies) {
            $scope.currencies = currencies;
        });

        Tag.getAll().then(function(tags) {
            $scope.tags = tags;
        });

        Shop.getAll().then(function(shops) {
            $scope.shops = shops;
            $.each(shops, function() {
                var shop = this;
                initDefaultProductShopTunnel(shop.tunnels);
            });
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                Product.get({
                        id: $stateParams.id
                    }, {}).$promise
                    .then(function(product) {
                        console.log('[' + $scope.action + '] loading product');
                        console.log(product);
                        product.productType = $scope.productTypes[product.productType];
                        $scope.product = product;
                        setDefaultProductShopTunnel(product.shopTunnels);
                        return product;
                    }).then(function(product) {
                        $scope.title = product.productType.label;
                        if (product.productType.value === 1) {
                            Product.getAll({
                                productType: 0,
                                enabled: true
                            }).then(function(members) {
                                $scope.members = members;
                            });
                        }
                    });
            }
        });

        $('#productTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    }
]);
