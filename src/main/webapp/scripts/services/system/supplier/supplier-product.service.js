
angular.module('ecommApp')

.factory('supplierProductService', ['$resource', '$http', function($resource, $http) {

    var supplierProduct = $resource('/api/supplierproducts/:id', {}, {});

        supplierProduct.getAll = function(params)
        {
            return $http.get('/api/supplierproducts/get/all', {
                params: params
            }).then(function(res) {
                return res.data;
            });
        };

        supplierProduct.checkUniqueSupplierProductCode = function( params )
        {
            return $http.get('/api/supplierproducts/check-unique', {
                params: params
            }).success( function( data )
            {
                return data;
            }).error( function()
            {
                return false;
            });
        };


        /* 批量选中 */
        supplierProduct.selectedSupplierProducts = [];
        /* 单个选中 */
        supplierProduct.selectedSupplierProduct = {};

        supplierProduct.getSelectedSupplierProduct = function()
        {
            return supplierProduct.selectedSupplierProduct;
        };

        supplierProduct.setSelectedSupplierProduct = function( selectedSupplierProduct )
        {
            supplierProduct.selectedSupplierProduct = selectedSupplierProduct;
        };

    return supplierProduct;
}]);