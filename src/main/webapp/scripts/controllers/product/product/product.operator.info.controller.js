angular.module('ecommApp')

.controller('ProductInformationController', ['$scope', '$state', '$stateParams', 'Product',
    function($scope, $state, $stateParams, Product) {

        $scope.defaultMember = {
            quantity: 1
        };

        $scope.member = angular.copy($scope.defaultMember);

        $scope.defaultSum = {
            totalPriceL1: 0,
            totalPriceL2: 0,
            totalPriceL3: 0,
            totalPriceL4: 0,
            totalPriceL5: 0,
            totalPriceL6: 0,
            totalPriceL7: 0,
            totalPriceL8: 0,
            totalPriceL9: 0,
            totalPriceL10: 0
        };

        $scope.sum = angular.copy($scope.defaultSum);

        $scope.getProduct = function(val) {
            return Product.get({
                page: 0,
                size: 30,
                enabled: true,
                productType: 0,
                nameOrSku: val
            }).$promise.then(function(page) {
                return page.content;
            });
        };

        $scope.save = function(product) {
            product.productType = product.productType.value;
            product.tempPurchasing = product.tempPurchasing.value;
            if (product.productType === 0) {
                product.members.length = 0;
            }
            Product.save({
                action: $scope.action
            }, product, function() {
                $state.go('product');
            });
        };

        $scope.saveMember = function(memberAddForm, member) {
            $scope.product.members.push(angular.copy(member));
            memberAddForm.$setPristine();
            $scope.member = angular.copy($scope.defaultMember);
        };

        $scope.updateMember = function(member) {
            member.editable = true;
        };

        $scope.saveUpdateMember = function(member, memberForm) {
            member.editable = false;
            memberForm.$setPristine();
        };

        $scope.showRemoveMember = function(member, $index) {
            $scope.removingMember = member;
            $scope.removingMember.$index = $index;
            $('#memberDeleteModal').modal('show');
        };

        $scope.removeMember = function() {
            if (angular.isDefined($scope.removingMember)) {
                $scope.product.members.splice($scope.removingMember.$index, 1);
                $scope.removingBrand = undefined;
                $('#memberDeleteModal').modal('hide');
            }
        };

        $scope.$watch('product.productType', function(obj) {
            if (obj) {
                $scope.title = obj.label;
                if (obj.value === 0) {
                    $scope.passed = true;
                } else if (obj.value === 1 && $scope.product.members.length > 0) {
                    $scope.passed = true;
                } else {
                    $scope.passed = false;
                }
            } else {
                $scope.passed = false;
            }
        });

        $scope.$watch('product.members', function(members) {
            if ($scope.product.productType && $scope.product.productType.value === 1) {
                if (members.length > 0) {
                    $scope.passed = true;
                } else {
                    $scope.passed = false;
                }
            }
            $scope.sum = angular.copy($scope.defaultSum);
            $.each(members, function() {
                var quantity = parseInt(this.quantity);
                $scope.sum.totalPriceL1 += parseFloat(this.product.priceL1) * quantity;
                $scope.sum.totalPriceL2 += parseFloat(this.product.priceL2) * quantity;
                $scope.sum.totalPriceL3 += parseFloat(this.product.priceL3) * quantity;
                $scope.sum.totalPriceL4 += parseFloat(this.product.priceL4) * quantity;
                $scope.sum.totalPriceL5 += parseFloat(this.product.priceL5) * quantity;
                $scope.sum.totalPriceL6 += parseFloat(this.product.priceL6) * quantity;
                $scope.sum.totalPriceL7 += parseFloat(this.product.priceL7) * quantity;
                $scope.sum.totalPriceL8 += parseFloat(this.product.priceL8) * quantity;
                $scope.sum.totalPriceL9 += parseFloat(this.product.priceL9) * quantity;
                $scope.sum.totalPriceL10 += parseFloat(this.product.priceL10) * quantity;
            });
            $scope.product.priceL1 = $scope.sum.totalPriceL1;
            $scope.product.priceL2 = $scope.sum.totalPriceL2;
            $scope.product.priceL3 = $scope.sum.totalPriceL3;
            $scope.product.priceL4 = $scope.sum.totalPriceL4;
            $scope.product.priceL5 = $scope.sum.totalPriceL5;
            $scope.product.priceL6 = $scope.sum.totalPriceL6;
            $scope.product.priceL7 = $scope.sum.totalPriceL7;
            $scope.product.priceL8 = $scope.sum.totalPriceL8;
            $scope.product.priceL9 = $scope.sum.totalPriceL9;
            $scope.product.priceL10 = $scope.sum.totalPriceL10;
        }, true);
    }
]);