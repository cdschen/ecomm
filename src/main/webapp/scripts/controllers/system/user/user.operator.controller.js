angular.module('ecommApp')

.controller('UserOperatorController', ['$scope', '$state', '$stateParams', 'User', 'Role', 'toastr', 'Shop', 'Warehouse', 'Principal',
    function($scope, $state, $stateParams, User, Role, toastr, Shop, Warehouse, Principal) {

        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.defaultUser = {
            roles: [],
            managedShopsArray: [],
            managedWarehousesArray: [],
            enabled: {
                label: '是',
                value: true
            }
        };

        $scope.user = angular.copy($scope.defaultUser);

        $scope.isorno = [{
            label: '是',
            value: true
        }, {
            label: '否',
            value: false
        }];

        $scope.action = 'create';
        $scope.modules = [];

        function initProperties(user) {
            user.enabled = $scope.isorno[user.enabled ? 0 : 1];
            user.managedShopsArray = (user.managedShops && user.managedShops.length > 0) ? user.managedShops.split(',') : [];
            user.managedWarehousesArray = (user.managedWarehouses && user.managedWarehouses.length > 0) ? user.managedWarehouses.split(',') : [];
        }

        function refreshProperties(user) {
            user.enabled = user.enabled.value;
            user.managedShops = user.managedShopsArray.join(',');
            user.managedWarehouses = user.managedWarehousesArray.join(',');
            var systemAdminRole;
            $.each(user.roles, function() {
                if (this.code === 'SYSTEM_ADMIN') {
                    systemAdminRole = this;
                    return false;
                }
            });
            if (systemAdminRole) {
                user.roles.length = 0;
                user.roles.push(systemAdminRole);
            }
        }

        Role.query().$promise.then(function(roles) {
            $.each(roles, function() {
                var role = this;
                if (role.code !== 'SYSTEM_ADMIN') {
                    var existModule = false;
                    $.each($scope.modules, function() {
                        var module = this;
                        if (module.name === role.module) {
                            module.roles.push(role);
                            existModule = true;
                            return false;
                        }
                    });
                    if (!existModule) {
                        var module = {
                            name: role.module,
                            sequence: role.moduleSequence,
                            roles: []
                        };
                        module.roles.push(role);
                        $scope.modules.push(module);
                    }
                } else {
                    $scope.systemAdminRole = role;
                }
            });
        }).then(function() {
            Shop.getAll({
                deleted: false
            }).then(function(shops) {
                $scope.shops = shops;
            });
        }).then(function() {
            Warehouse.getAll({
                deleted: false
            }).then(function(warehouses) {
                $scope.warehouses = warehouses;
            });
        }).then(function() {
            if ($stateParams.id && $stateParams.id !== '') {
                $scope.action = 'update';
                User.get({
                    id: $stateParams.id
                }, function(user) {
                    initProperties(user);
                    $scope.user = user;
                    //console.log(user);
                });
            }
        });

        $scope.toggleRole = function(role) {
            var existRole = false;
            $.each($scope.user.roles, function(i) {
                if (this.code === role.code) {
                    existRole = true;
                    $scope.user.roles.splice(i, 1);
                    return false;
                }
            });
            if (!existRole) {
                $scope.user.roles.push(role);
            }
        };

        $scope.checkedRole = function(role) {
            var checked = false;
            $.each($scope.user.roles, function() {
                if (this.code === role.code) {
                    checked = true;
                    return false;
                }
            });
            return checked;
        };

        $scope.saveUser = function(user) {
            if (user.roles.length === 0) {
                toastr.warning('用户还没有被设置权限，请至少分配一个权限。');
            } else {
                refreshProperties(user);
                User.save({}, user, function() {
                    Principal.identify(true).then(function(){
                        $state.go('user');
                    });
                });
            }
        };

        $scope.showUpdatePassword = function() {
            $('#passwordUpdateModal').modal('show');
        };

        $scope.updatePassword = function(password, userId, passwordForm) {
            var user = {
                id: userId,
                password: password
            };
            User.updatePassword(user).then(function() {
                passwordForm.$setPristine();
                User.get({
                    id: user.id
                }, function(user) {
                    initProperties(user);
                    $scope.user = user;
                    $('#passwordUpdateModal').modal('hide');
                });
            });
        };

        $scope.toggleShop = function(shop) {
            var existShop = false;
            $.each($scope.user.managedShopsArray, function(i) {
                if (parseInt(this) === shop.id) {
                    existShop = true;
                    $scope.user.managedShopsArray.splice(i, 1);
                    return false;
                }
            });
            if (!existShop) {
                $scope.user.managedShopsArray.push(shop.id);
            }
        };

        $scope.toggleWarehouse = function(warehouse) {
            var existWarehouse = false;
            $.each($scope.user.managedWarehousesArray, function(i) {
                if (parseInt(this) === warehouse.id) {
                    existWarehouse = true;
                    $scope.user.managedWarehousesArray.splice(i, 1);
                    return false;
                }
            });
            if (!existWarehouse) {
                $scope.user.managedWarehousesArray.push(warehouse.id);
            }
        };

        $scope.checkedShop = function(shop) {
            var checked = false;
            $.each($scope.user.managedShopsArray, function() {
                if (parseInt(this) === shop.id) {
                    checked = true;
                    return false;
                }
            });
            return checked;
        };

        $scope.checkedWarehouse = function(warehouse) {
            var checked = false;
            $.each($scope.user.managedWarehousesArray, function() {
                if (parseInt(this) === warehouse.id) {
                    checked = true;
                    return false;
                }
            });
            return checked;
        };

    }
]);
