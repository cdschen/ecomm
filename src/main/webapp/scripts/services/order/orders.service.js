angular.module('ecommApp')

.factory('orderService', ['$resource', '$http', function($resource, $http) {

    var operationReview;
    var order = $resource('/api/orders/:id', {}, {});

    order.getAll = function(params) {
        return $http.get('/api/orders/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    order.getPagedOrdersForOrderDeploy = function(params) {
        return $http.get('/api/orders/for/orderdeploy', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    order.confirmOrderWhenGenerateShipment = function(reviewDTO) {
        return $http.post('/api/orders/confirm/shipment', reviewDTO)
            .then(function(res) {
                return (operationReview = res.data);
            });
    };

    order.confirmOrderWhenGenerateOutInventory = function(reviewDTO) {
        return $http.post('/api/orders/confirm/outinventory', reviewDTO)
            .then(function(res) {
                return (operationReview = res.data);
            });
    };

    order.checkItemProductShopTunnel = function(order) {
        var items = order.items;
        $.each(items, function() {
            var item = this;
            if (item.warehouseId) {
                var exitShopTunnels = false;
                $.each(order.shop.tunnels, function() {
                    var tunnel = this;
                    // 判断通道是不是仓库通道，并且行为是包含
                    if (tunnel.type === 1 && tunnel.behavior === 1) {
                        $.each(tunnel.warehouses, function() {
                            var warehouse = this;
                            if (warehouse.id === item.warehouseId) {
                                item.assignTunnel = angular.copy(tunnel);
                                item.assignTunnel.defaultWarehouse = angular.copy(this);
                                exitShopTunnels = true;
                                return false;
                            }
                        });
                    }
                    if (exitShopTunnels) {
                        return false;
                    }
                });
            } else {
                if (item.product.shopTunnels.length > 0) {
                    var match = false;
                    $.each(item.product.shopTunnels, function() {
                        var productShopTunnel = this;
                        if (productShopTunnel.shopId === order.shop.id) {
                            match = true;
                            $.each(order.shop.tunnels, function() {
                                var tunnel = this;
                                if (tunnel.id === productShopTunnel.tunnelId) {
                                    item.assignTunnel = angular.copy(tunnel);
                                    // 如何选择的通道是一个供应商通道
                                    if (!tunnel.defaultWarehouse) {
                                        item.assignTunnel = angular.copy(order.shop.defaultTunnel);
                                    }
                                    return false;
                                }
                            });
                            return false;
                        }
                    });

                    if (!match) {
                        item.assignTunnel = angular.copy(order.shop.defaultTunnel);
                    }
                } else {
                    item.assignTunnel = angular.copy(order.shop.defaultTunnel);
                }
            }
        });
    };

    order.getOperationReview = function() {
        return operationReview;
    };
    order.setOperationReview = function(reviewDTO) {
        operationReview = reviewDTO;
    };

    order.selectedOrders = [];

    return order;
}])

.factory('OrderItem', ['$resource', '$http', function($resource, $http) {

    //var $ = angular.element;
    var item = $resource('/api/orderitems/:id', {}, {});

    item.getAll = function(params) {
        return $http.get('/api/orderitems/get/all', {
            params: params
        }).then(function(res) {
            return res.data;
        });
    };

    return item;

}]);
