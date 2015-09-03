'use strict';

angular.module('ecommApp')

.controller('ProductController', ['$scope', 'Product', 'Utils', 'Process', 'ObjectProcess',
    function($scope, Product, Utils, Process, ObjectProcess) {

        //var $ = angular.element;
        $scope.template = {
            details: {
                url: 'views/product/product.details.html?' + (new Date())
            },
            process: {
                url: 'views/product/product.process.html?' + (new Date())
            },
            status: {
                url: 'views/product/product.status.html?' + (new Date())
            }
        };
        $scope.totalPagesList = [];
        $scope.pageSize = 20;
        $scope.product = {
            sku: '',
            name: ''
        };
        $scope.processes = [];
        $scope.status = [];
        $scope.selected = {
            status: []
        };
        $scope.popover = {
            url: 'process-tmpl.html'
        };
        $scope.detailsSlideChecked = false;
        $scope.processSlideChecked = false;
        $scope.statusSlideChecked = false;
        $scope.processProduct = undefined;

        function initStatus(processes) {
            angular.forEach(processes, function(process) {
                angular.forEach(process.steps, function(step) {
                    step.processName = process.name;
                    $scope.status.push(step);
                });
            });
            // console.log('$scope.status:');
            // console.log($scope.status);
        }

        function refreshStatus(status) {
            var selectedStatus = [];
            angular.forEach(status, function(state) {
                selectedStatus.push(state.id);
            });
            return selectedStatus;
        }

        Product.get({
            page: 0,
            size: $scope.pageSize,
            sort: ['name'],
            deleted: false
        }).$promise.then(function(page) {
            console.clear();
            console.log('page:');
            console.log(page);
            $scope.page = page;
            $scope.totalPagesList = Utils.setTotalPagesList(page);
        }).then(function() {
            Process.getAll({
                deleted: false,
                objectType: 2
            }).then(function(processes) {
                console.log('Process.getAll:');
                console.log(processes);
                $scope.processes = processes;
                initStatus(processes);
            });
        });

        $scope.turnPage = function(number) {
            console.clear();
            console.log('turnPage:');
            console.log($scope.product);
            if (number > -1 && number < $scope.page.totalPages) {
                Product.get({
                    page: number,
                    size: $scope.pageSize,
                    sort: ['name'],
                    sku: $scope.product.sku,
                    name: $scope.product.name,
                    deleted: false,
                    status: refreshStatus($scope.selected.status)
                }, function(page) {
                    $scope.page = page;
                    $scope.totalPagesList = Utils.setTotalPagesList(page);
                });
            }
        };

        $scope.search = function() {
            console.clear();
            console.log('search:');
            console.log($scope.product);
            Product.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['name'],
                sku: $scope.product.sku,
                name: $scope.product.name,
                status: refreshStatus($scope.selected.status)
            }, function(page) {
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        $scope.reset = function() {
            console.clear();
            console.log('reset:');
            $scope.product = {
                sku: '',
                name: ''
            };
            $scope.selected.status.length = 0;
            console.log($scope.product);
            Product.get({
                page: 0,
                size: $scope.pageSize,
                sort: ['name']
            }, function(page) {
                $scope.page = page;
                $scope.totalPagesList = Utils.setTotalPagesList(page);
            });
        };

        // status

        $scope.colseStatusSlide = function() {
            $scope.statusSlideChecked = false;
        };

        $scope.loadStatus = function() {
            $scope.statusSlideChecked = true;
        };

        $scope.selectState = function(step) {
            if (step.selected && step.selected === true) {
                step.selected = false;
                for (var i = 0, len = $scope.selected.status.length; i < len; i++) {
                    if ($scope.selected.status[i].id === step.id) {
                        $scope.selected.status.splice(i, 1);
                        break;
                    }
                }
            } else {
                step.selected = true;
                $scope.selected.status.push(step);
            }
            console.log('$scope.selectState():');
            console.log($scope.selected.status);
        };

        // process

        $scope.colseProcessSlide = function() {
            $scope.processSlideChecked = false;
            if ($scope.processProduct) {
                $scope.processProduct.active = false;
            }
        };

        $scope.loadProcesses = function(product) {
            console.log(product);
            $scope.processSlideChecked = true;
            $scope.processProduct = product;
            $scope.processProduct.active = true;
        };

        $scope.updateStep = function(product) {
            console.log('updateStep:');
            $scope.processProduct = product;
        };

        $scope.saveUpdateStep = function(process, stepId) {
            console.log('saveUpdateStep:');
            process.step.id = stepId;
            console.log(process);
            ObjectProcess.save({}, process, function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    console.log('refresh Processes:');
                    console.log(objectProcesses);
                    $scope.processProduct.processes = angular.copy(objectProcesses);
                });
            });
        };

        // details

        $scope.colseDetailsSlide = function() {
            $scope.detailsSlideChecked = false;
        };

        $scope.loadDetails = function(product) {
            $scope.detailsSlideChecked = true;
            $scope.processProduct = product;
        };

    }
])

.controller('ProductDetailsController', ['$scope', function($scope) {
    var $ = angular.element;

    $scope.initProductDetailsTabs = function() {
        $('#productDetailsTabs a').click(function(e) {
            e.preventDefault();
            $(this).tab('show');
        });
    };

}])

.controller('ProductProcessController', ['$scope', '$filter', 'Process', 'ObjectProcess',
    function($scope, $filter, Process, ObjectProcess) {

        $scope.applyProcess = function(process) {
            
            var objectProcess = {
                objectId: $scope.processProduct.id,
                objectType: 2,
                process: {
                    id: process.id
                }
            };

            process.steps = $filter('orderBy')(process.steps, 'sequence');

            if (process.defaultStepId) {
                objectProcess.step = {
                    id: process.defaultStepId
                };
            } else {
                objectProcess.step = {
                    id: process.steps[0].id
                };
            }

            ObjectProcess.save({}, objectProcess).$promise.then(function(objectProcess) {
                return objectProcess;
            }).then(function(objectProcess) {
                ObjectProcess.getAll({
                    objectId: objectProcess.objectId
                }).then(function(objectProcesses) {
                    console.log('refresh Processes:');
                    console.log(objectProcesses);
                    $scope.processProduct.processes = angular.copy(objectProcesses);
                    $scope.colseProcessSlide();
                });
            });

        };

        $scope.removeProcess = function(process) {
            var objectProcesses = $scope.processProduct.processes;
            var objectProcess;
            for (var i = 0, len = objectProcesses.length; i < len; i++) {
                objectProcess = objectProcesses[i];
                if (process.id === objectProcess.process.id) {
                    break;
                }
            }
            if (objectProcess) {
                ObjectProcess.remove({
                    id: objectProcess.id
                }, {}, function() {
                    ObjectProcess.getAll({
                        objectId: objectProcess.objectId
                    }).then(function(objectProcesses) {
                        console.log('refresh Processes:');
                        console.log(objectProcesses);
                        $scope.processProduct.processes = angular.copy(objectProcesses);
                        $scope.colseProcessSlide();
                    });
                });
            }

        };

        $scope.forObjectProcesses = function(step) {
            if ($scope.processProduct) {
                var objectProcesses = $scope.processProduct.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (objectProcess.process.id === step.processId) {
                        if (objectProcess.process.type === 1) {
                            if (step.sequence > objectProcess.step.sequence) {
                                return false;
                            } else if (step.sequence <= objectProcess.step.sequence) {
                                return true;
                            }
                        } else if (objectProcess.process.type === 2) {
                            if (step.id === objectProcess.step.id) {
                                return true;
                            } else if (step.id !== objectProcess.step.id) {
                                return false;
                            }
                        }
                    } else {
                        continue;
                    }
                }
                return false;
            }
        };

        $scope.appliedProcess = function(process) {
            if ($scope.processProduct) {
                var objectProcesses = $scope.processProduct.processes;
                for (var i = 0, len = objectProcesses.length; i < len; i++) {
                    var objectProcess = objectProcesses[i];
                    if (process.id === objectProcess.process.id) {
                        return true;
                    }
                }
                return false;
            }
        };
    }
])

.controller('ProductOperatorController', ['$scope', '$state', '$stateParams', 'Product', 'Brand', 'Category', 'MadeFrom', 'Language', 'Currency', 'Tag',
    function($scope, $state, $stateParams, Product, Brand, Category, MadeFrom, Language, Currency, Tag) {

        console.clear();
        var $ = angular.element;
        $scope.actionLabel = ($stateParams.id && $stateParams.id !== '') ? '编辑' : '创建';

        $scope.template = {
            info: {
                url: 'views/product/product.operator.info.html?' + new Date(),
                attr: {
                    url: 'views/product/product.operator.info.attr.html?' + new Date()
                },
                price: {
                    url: 'views/product/product.operator.info.price.html?' + new Date()
                },
                group: {
                    url: 'views/product/product.operator.info.group.html?' + new Date()
                }
            },
            img: {
                url: 'views/product/product.operator.img.html?' + new Date()
            },
            multilanguage: {
                url: 'views/product/product.operator.multilanguage.html?' + new Date()
            },
            multicurrency: {
                url: 'views/product/product.operator.multicurrency.html?' + new Date()
            }
        };

        $scope.productTypes = [{
            label: '简单商品',
            value: 0
        }, {
            label: '组合商品',
            value: 1
        }];

        $scope.brands = [];
        $scope.categories = [];
        $scope.madeFroms = [];
        $scope.languages = [];
        $scope.currencies = [];
        $scope.tags = [];
        $scope.members = [];
        $scope.product = {
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
            members: []
        };
        $scope.action = 'create';

        Brand.getAll().then(function(brands) {
            $scope.brands = brands;
        }).then(function() {
            return Category.getAll().then(function(categories) {
                $scope.categories = categories;
            });
        }).then(function() {
            return MadeFrom.getAll().then(function(madeFroms) {
                $scope.madeFroms = madeFroms;
            });
        }).then(function() {
            return Language.getAll().then(function(languages) {
                $scope.languages = languages;
            });
        }).then(function() {
            return Currency.getAll().then(function(currencies) {
                $scope.currencies = currencies;
            });
        }).then(function() {
            return Tag.getAll().then(function(tags) {
                $scope.tags = tags;
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
                        return product;
                    }).then(function(product) {
                        $scope.title = product.productType.label;
                        if (product.productType.value === 1) {
                            Product.getAll({
                                productType: 0,
                            }).then(function(members) {
                                // console.log('members:');
                                // console.log(members);
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
])

.controller('ProductInformationController', ['$scope', '$state', '$stateParams', 'Product',
    function($scope, $state, $stateParams, Product) {

        var $ = angular.element;
        var defaultMember = {
            quantity: 1
        };
        $scope.member = angular.copy(defaultMember);
        var defaultSum = {
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
        $scope.sum = angular.copy(defaultSum);

        $scope.changeProductType = function($item) {
            //console.clear();
            console.log('$item:');
            console.log($item);

            if ($item.value === 1) {
                Product.getAll({
                    productType: 0,
                }).then(function(members) {
                    console.log('members:');
                    console.log(members);
                    $scope.members = members;
                });
            }
        };

        $scope.save = function(product) {
            //console.clear();
            console.log('[' + $scope.action + '] save product');
            product.productType = product.productType.value;
            if (product.productType === 0) {
                product.members.length = 0;
            }
            console.log(product);
            Product.save({
                action: $scope.action
            }, product, function(product) {
                console.log('[' + $scope.action + '] save product complete:');
                console.log(product);
                $state.go('product');
            });
        };

        $scope.saveMember = function(memberAddForm, member) {
            //console.clear();
            console.log('[' + $scope.action + '] saveMember complete:');
            console.log(member);
            $scope.product.members.push(angular.copy(member));
            memberAddForm.$setPristine();
            $scope.member = angular.copy(defaultMember);
        };

        $scope.updateMember = function(member) {
            //console.clear();
            console.log('updateMember:');
            console.log(member);
            member.editable = true;
        };

        $scope.saveUpdateMember = function(member, memberForm) {
            //console.clear();
            console.log('[' + $scope.action + '] saveUpdateMember complete:');
            console.log(member);
            member.editable = false;
            memberForm.$setPristine();

        };

        $scope.removingMember = undefined;

        $scope.showRemoveMember = function(member, $index) {
            console.clear();
            console.log('showRemoveMember $index: ' + $index);
            console.log(member);

            $scope.removingMember = member;
            $scope.removingMember.$index = $index;
            $('#memberDeleteModal').modal('show');
        };

        $scope.removeMember = function() {
            console.clear();
            console.log('removeMember:');
            console.log($scope.removingBrand);

            if (angular.isDefined($scope.removingMember)) {
                $scope.product.members.splice($scope.removingMember.$index, 1);
                $scope.removingBrand = undefined;
                $('#memberDeleteModal').modal('hide');
            }
        };

        $scope.$watch('product.productType', function(obj) {
            // console.log('$watch -> product.productType:');
            // console.log(obj);
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
            // console.log('$watch -> product.members:');
            // console.log(members);
            if ($scope.product.productType && $scope.product.productType.value === 1) {
                if (members.length > 0) {
                    $scope.passed = true;
                } else {
                    $scope.passed = false;
                }
            }
            $scope.sum = angular.copy(defaultSum);
            angular.forEach(members, function(member) {
                var quantity = parseInt(member.quantity);
                $scope.sum.totalPriceL1 += parseFloat(member.product.priceL1) * quantity;
                $scope.sum.totalPriceL2 += parseFloat(member.product.priceL2) * quantity;
                $scope.sum.totalPriceL3 += parseFloat(member.product.priceL3) * quantity;
                $scope.sum.totalPriceL4 += parseFloat(member.product.priceL4) * quantity;
                $scope.sum.totalPriceL5 += parseFloat(member.product.priceL5) * quantity;
                $scope.sum.totalPriceL6 += parseFloat(member.product.priceL6) * quantity;
                $scope.sum.totalPriceL7 += parseFloat(member.product.priceL7) * quantity;
                $scope.sum.totalPriceL8 += parseFloat(member.product.priceL8) * quantity;
                $scope.sum.totalPriceL9 += parseFloat(member.product.priceL9) * quantity;
                $scope.sum.totalPriceL10 += parseFloat(member.product.priceL10) * quantity;
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
])

.controller('ProductImageController', ['$scope', '$cookies', '$stateParams',
    function($scope, $cookies, $stateParams) {

        var $ = angular.element;
        var id = 0;
        if ($stateParams.id && $stateParams.id !== '') {
            id = $stateParams.id;
        } else {
            id = 0;
        }

        $scope.initImgFunction = function() {
            $('input[type="file"][name="image"]').on('change', function() {
                console.log(this.value.substring(this.value.lastIndexOf('\\') + 1));
                var imgUrl = this.value.substring(this.value.lastIndexOf('\\') + 1);
                if (id !== 0) {
                    imgUrl = id + '-' + imgUrl;
                }
                var field = this.id;

                $.ajax({
                    url: '/api/products/image/upload/' + id + '/' + field,
                    type: 'POST',
                    data: new FormData($('#' + this.id + 'Form')[0]),
                    enctype: 'multipart/form-data',
                    headers: {
                        'X-XSRF-TOKEN': $cookies.get('XSRF-TOKEN')
                    },
                    processData: false,
                    contentType: false,
                    cache: false,
                    success: function() {
                        $scope.$apply(function() {
                            $scope.product[field] = imgUrl;
                        });
                    },
                    error: function() {}
                });
            });
        };

    }
])

.controller('ProductMultiLanguageController', ['$scope', '$stateParams', 'ProductMultiLanguage',
    function($scope, $stateParams, ProductMultiLanguage) {

        var $ = angular.element;
        $scope.multiLanguage = {};

        $scope.saveLanguage = function(mlAddForm, multiLanguage) {
            console.clear();
            console.log('[' + $scope.action + '] saveLanguage:');
            console.log(multiLanguage);

            if ($scope.action === 'create') {
                console.log('[' + $scope.action + '] saveLanguage complete:');
                console.log(multiLanguage);
                $scope.product.multiLanguages.push(angular.copy(multiLanguage));
                mlAddForm.$setPristine();
                $scope.multiLanguage = {};
            } else if ($scope.action === 'update') {
                multiLanguage.productId = $stateParams.id;
                ProductMultiLanguage.save({}, multiLanguage, function(ml) {
                    console.log('[' + $scope.action + '] saveLanguage complete:');
                    console.log(ml);
                    $scope.product.multiLanguages.push(angular.copy(ml));
                    mlAddForm.$setPristine();
                    $scope.multiLanguage = {};
                });
            }
        };

        $scope.updateLanguage = function(ml) {
            console.clear();
            console.log('updateLanguage:');
            console.log(ml);
            ml.editable = true;
        };

        $scope.saveUpdateLanguage = function(ml, mlForm) {
            console.clear();
            console.log('[' + $scope.action + '] saveUpdateLanguage complete:');
            if ($scope.action === 'create') {
                console.log(ml);
                ml.editable = false;
                mlForm.$setPristine();
            } else if ($scope.action === 'update') {
                ProductMultiLanguage.save({}, ml, function() {
                    console.log(ml);
                    ml.editable = false;
                    mlForm.$setPristine();
                });
            }
        };

        $scope.removingLanguage = undefined;

        $scope.showRemoveLanguage = function(ml, $index) {
            console.clear();
            console.log('showRemoveLanguage $index: ' + $index);
            console.log(ml);

            $scope.removingLanguage = ml;
            $scope.removingLanguage.$index = $index;
            $('#mlDeleteModal').modal('show');
        };

        $scope.removeLanguage = function() {
            console.clear();
            console.log('removeLanguage:');
            console.log($scope.removingLanguage);

            if (angular.isDefined($scope.removingLanguage)) {
                if ($scope.action === 'create') {
                    $scope.product.multiLanguages.splice($scope.removingLanguage.$index, 1);
                    $scope.removingLanguage = undefined;
                    $('#mlDeleteModal').modal('hide');
                } else if ($scope.action === 'update') {
                    ProductMultiLanguage.remove({
                        id: $scope.removingLanguage.id
                    }, {}, function() {
                        $scope.product.multiLanguages.splice($scope.removingLanguage.$index, 1);
                        $scope.removingLanguage = undefined;
                        $('#mlDeleteModal').modal('hide');
                    });
                }
            }
        };

    }
])

.controller('ProductMultiCurrencyController', ['$scope', '$stateParams', 'ProductMultiCurrency',
    function($scope, $stateParams, ProductMultiCurrency) {

        var $ = angular.element;
        var defaultMultiCurrency = {
            priceL1: 0.00,
            priceL2: 0.00,
            priceL3: 0.00,
            priceL4: 0.00,
            priceL5: 0.00,
            priceL6: 0.00,
            priceL7: 0.00,
            priceL8: 0.00,
            priceL9: 0.00,
            priceL10: 0.00
        };
        $scope.multiCurrency = angular.copy(defaultMultiCurrency);

        $scope.saveCurrency = function(mcAddForm, multiCurrency) {
            console.clear();
            console.log('[' + $scope.action + '] saveCurrency:');
            console.log(multiCurrency);

            if ($scope.action === 'create') {
                console.log('[' + $scope.action + '] saveCurrency complete:');
                console.log(multiCurrency);
                $scope.product.multiCurrencies.push(angular.copy(multiCurrency));
                mcAddForm.$setPristine();
                $scope.multiCurrency = angular.copy(defaultMultiCurrency);
            } else if ($scope.action === 'update') {
                multiCurrency.productId = $stateParams.id;
                ProductMultiCurrency.save({}, multiCurrency, function(mc) {
                    console.log('[' + $scope.action + '] saveCurrency complete:');
                    console.log(mc);
                    $scope.product.multiCurrencies.push(angular.copy(mc));
                    mcAddForm.$setPristine();
                    $scope.multiCurrency = defaultMultiCurrency;
                });
            }
        };

        $scope.updateCurrency = function(mc) {
            console.clear();
            console.log('updateCurrency:');
            console.log(mc);
            mc.editable = true;
        };

        $scope.saveUpdateCurrency = function(mc) {
            console.clear();
            console.log('[' + $scope.action + '] updateCurrency complete:');
            if ($scope.action === 'create') {
                console.log(mc);
                mc.editable = false;
            } else if ($scope.action === 'update') {
                ProductMultiCurrency.save({}, mc, function() {
                    console.log(mc);
                    mc.editable = false;
                });
            }
        };

        $scope.removingCurrency = undefined;

        $scope.showRemoveCurrency = function(mc, $index) {
            console.clear();
            console.log('showRemoveCurrency $index: ' + $index);
            console.log(mc);

            $scope.removingCurrency = mc;
            $scope.removingCurrency.$index = $index;
            $('#mcDeleteModal').modal('show');
        };

        $scope.removeCurrency = function() {
            console.clear();
            console.log('removeCurrency:');
            console.log($scope.removingCurrency);

            if (angular.isDefined($scope.removingCurrency)) {
                if ($scope.action === 'create') {
                    $scope.product.multiCurrencies.splice($scope.removingCurrency.$index, 1);
                    $scope.removingCurrency = undefined;
                    $('#mcDeleteModal').modal('hide');
                } else if ($scope.action === 'update') {
                    ProductMultiCurrency.remove({
                        id: $scope.removingCurrency.id
                    }, {}, function() {
                        $scope.product.multiCurrencies.splice($scope.removingCurrency.$index, 1);
                        $scope.removingCurrency = undefined;
                        $('#mcDeleteModal').modal('hide');
                    });
                }
            }
        };

    }
]);
