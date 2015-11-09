angular.module('ecommApp')

.factory('Utils', [function() {

    return {
        initList: function(page, query) {
            query.list = [];
            if (page.totalPages > 0) {
                // if (page.number < 5 || page.number > page.totalPages  - 5) {
                //     if (page.totalPages < 5) {
                //         for (var i = 0, len = page.totalPages; i < len; i++) {
                //             query.list.push(i);
                //         }
                //     } else if (page.totalPages >= 5) {
                //         for (var i = page.number, len = page.totalPages; i < len; i++) {
                //             query
                //         }
                //     }
                // } else {

                // }
                for (var i = 0, len = page.totalPages; i < len; i++) {
                    query.list.push(i);
                }
            }
        },
        setTotalPagesList: function(page) {
            var totalPagesList = [];
            if (page.totalPages > 0) {
                for (var i = 0, len = page.totalPages; i < len; i++) {
                    totalPagesList.push(i);
                }
            }
            return totalPagesList;
        },
        convertLocaleDateToServer: function(date) {
            if (date) {
                var utcDate = new Date();
                utcDate.setUTCDate(date.getDate());
                utcDate.setUTCMonth(date.getMonth());
                utcDate.setUTCFullYear(date.getFullYear());
                return utcDate;
            } else {
                return null;
            }
        },
        convertLocaleDateFromServer: function(date) {
            if (date) {
                var dateString = date.split('-');
                return new Date(dateString[0], dateString[1] - 1, dateString[2]);
            }
            return null;
        },
        convertDateTimeFromServer: function(date) {
            if (date) {
                return new Date(date);
            } else {
                return null;
            }
        }
    };

}]);
