'use strict';

angular.module('ecommApp')

.factory('orderService', ['$resource', '$http', function($resource, $http) {

    var $ = angular.element;
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
            return res.data
        });
    };

    order.ordersConfirmOrderDeploy = function(orders) {
        return $http.post('/api/orders/confirm/orderdeploy', orders)
            .then(function(res) {
                return res.data;
            });
    }

    order.checkItemProductShopTunnel = function(order) {
        var items = order.items;
        $.each(items, function() {
            var item = this;
            if (item.warehouseId) {
                var exitShopTunnels = false;
                $.each(order.shop.tunnels, function() {
                    var tunnel = this;
                    $.each(tunnel.warehouses, function() {
                        var warehouse = this;
                        if (warehouse.id === item.warehouseId) {
                            item.assignTunnel = angular.copy(tunnel);
                            item.assignTunnel.defaultWarehouse = angular.copy(this);
                            exitShopTunnels = true;
                            return false;
                        }
                    });
                    if (!exitShopTunnels) {
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
                                    $.each(tunnel.warehouses, function() {
                                        if (item.assignTunnel.defaultWarehouseId === this.id) {
                                            item.assignTunnel.defaultWarehouse = angular.copy(this);
                                            return false;
                                        }
                                    });
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

    order.selectedOrders = [];


    return order;
}])

.factory('OrderItem', ['$resource', '$http', function($resource, $http) {

    var $ = angular.element;
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
