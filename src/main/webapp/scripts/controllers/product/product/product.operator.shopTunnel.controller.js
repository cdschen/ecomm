angular.module('ecommApp')

.controller('ProductShopTunnelController', ['$scope', '$stateParams', 'ProductShopTunnel',
    function($scope, $stateParams, ProductShopTunnel) {

        var $ = angular.element;

        $scope.setProductDefaultTunnel = function(tunnel, tunnels) {
            $.each(tunnels, function() {
                this.selectedDefault = false;
            });
            tunnel.selectedDefault = true;
            $scope.selectedDefaultTunnelsMap[tunnel.shopId] = tunnel;
            if ($scope.action === 'create') {
                angular.forEach($scope.selectedDefaultTunnelsMap, function(tunnel) {
                    var exist = false;
                    $.each($scope.product.shopTunnels, function() {
                        var shopTunnel = this;
                        if (this.shopId === tunnel.shopId) {
                            shopTunnel = tunnel;
                            exist = true;
                            return false;
                        }
                    });
                    if (!exist) {
                        $scope.product.shopTunnels.push({
                            shopId: tunnel.shopId,
                            tunnelId: tunnel.id
                        });
                    }
                });
            } else if ($scope.action === 'update') {
                $.each($scope.selectedDefaultTunnelsMap, function(key, value) {
                    var tunnel = value;
                    var exist = false;
                    var updateShopTunnel;
                    $.each($scope.product.shopTunnels, function() {
                        var shopTunnel = this;
                        if (this.shopId === tunnel.shopId) {
                            shopTunnel.tunnelId = tunnel.id;
                            exist = true;
                            updateShopTunnel = shopTunnel;
                            return false;
                        }
                    });
                    if (exist && updateShopTunnel) {
                        console.log('exist');
                        ProductShopTunnel.save({}, updateShopTunnel, function() {
                            console.log('[' + $scope.action + '] ProductShopTunnel update shopTunnel complete:');
                            updateShopTunnel = undefined;
                        });
                        return false;
                    } else {
                        ProductShopTunnel.save({}, {
                            productId: $scope.product.id,
                            shopId: tunnel.shopId,
                            tunnelId: tunnel.id
                        }, function(shopTunnel) {
                            console.log('[' + $scope.action + '] ProductShopTunnel create shopTunnel complete:');
                            $scope.product.shopTunnels.push(shopTunnel);
                        });
                        return false;
                    }
                });
            }
        };
    }
]);
