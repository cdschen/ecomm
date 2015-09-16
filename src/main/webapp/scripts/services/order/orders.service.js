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

    order.checkItemProductShopTunnel = function(order) {
        var items = order.items;
        $.each(items, function() {
            var item = this;
            if (item.product.shopTunnels.length > 0) {
                $.each(item.product.shopTunnels, function() {
                    var productShopTunnel = this;
                    if (productShopTunnel.shopId === order.shop.id) {
                        console.log('匹配到店铺');
                        $.each(order.shop.tunnels, function() {
                            var tunnel = this;
                            if (tunnel.id === productShopTunnel.tunnelId) {
                                console.log('匹配到通道');
                                item.assignTunnel = tunnel;
                                $.each(tunnel.warehouses, function() {
                                    if (item.assignTunnel.defaultWarehouseId === this.id) {
                                        item.assignTunnel.defaultWarehouse = this;
                                        return false;
                                    }
                                });
                                return false;
                            }
                        });
                        return false;
                    }
                })
            }
        });
    };

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