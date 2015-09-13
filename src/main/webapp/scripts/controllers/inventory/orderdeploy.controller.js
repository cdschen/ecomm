angular.module('ecommApp')

.controller('OrderDeployController', ['$scope', 'Warehouse', 'Shop', 'orderService',
    function($scope, Warehouse, Shop, orderService) {

        var $ = angular.element,
            warehouse = {
                selected: undefined
            },
            shop = {
                selected: undefined
            },
            warehouses = [],
            shops = [];


        Warehouse.getAll({
            deleted: false,
            sort: ['name']
        }).then(function(warehouses) {
        	console.log(warehouses);
            $scope.warehouses = warehouses;
        }).then(function() {
            return Shop.getAll({
                deleted: false,
                sort: ['name']
            }).then(function(shops) {
                $scope.shops = shops;
            });
        }).then(function(){
        	orderService.getAll({

        	}).then(function(){

        	})
        });

    }
]);
