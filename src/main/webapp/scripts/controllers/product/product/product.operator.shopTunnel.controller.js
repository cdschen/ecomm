angular.module('ecommApp')

.controller('ProductShopTunnelController', ['$scope', '$stateParams', 'ProductShopTunnel',
    function($scope, $stateParams, ProductShopTunnel) {

        $scope.setProductDefaultTunnel = function(tunnel, tunnels) {
            $.each(tunnels, function() {
                this.selectedDefault = false;
            });
            tunnel.selectedDefault = true;
            if ($scope.action === 'create') {
                var exist = false;
                $.each($scope.product.shopTunnels, function() {
                    if (this.shopId === tunnel.shopId) {
                        this.tunnelId = tunnel.id;
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
            } else if ($scope.action === 'update') {
                var updateShopTunnel;
                $.each($scope.product.shopTunnels, function() {
                    if (this.shopId === tunnel.shopId) {
                        this.tunnelId = tunnel.id;
                        updateShopTunnel = this;
                        return false;
                    }
                });
                if (updateShopTunnel) {
                    ProductShopTunnel.save({}, updateShopTunnel, function() {
                        console.log('[' + $scope.action + '] ProductShopTunnel update shopTunnel complete:');
                        updateShopTunnel = undefined;
                    });
                } else {
                    ProductShopTunnel.save({}, {
                        productId: $scope.product.id,
                        shopId: tunnel.shopId,
                        tunnelId: tunnel.id
                    }, function(shopTunnel) {
                        console.log('[' + $scope.action + '] ProductShopTunnel create shopTunnel complete:');
                        $scope.product.shopTunnels.push(shopTunnel);
                    });
                }
            }
        };
    }
]);
