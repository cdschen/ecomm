angular.module('ecommApp')

.factory('Utils', [function() {

    return {
        initList: function(page, query) {
            query.list = [];
            if (page.totalPages > 0) {
                if (page.number > 0) {
                    query.list.push({
                        label: '上一页',
                        value: page.number - 1
                    });
                }
                if (page.number > 4) {
                    query.list.push({
                        label: 1,
                        value: 0
                    },{
                        label: '...',
                        value: page.number - 10 < 0 ? 0 : page.number - 10
                    });
                }
                var start = page.number - 4 < 0 ? 0 : page.number - 4;
                var end = page.number + 5 > page.totalPages ? page.totalPages : page.number + 5;
                for (var i = start, len = end; i < len; i++) {
                    query.list.push({
                        label: i + 1,
                        value: i
                    });
                }
                if (page.number + 4 < page.totalPages - 1) {
                    query.list.push({
                        label: '...',
                        value: page.number + 10 >= page.totalPages ? page.totalPages - 1 : page.number + 10
                    });
                }
                if (page.number < page.totalPages - 5) {
                    query.list.push({
                        label: page.totalPages,
                        value: page.totalPages - 1
                    });
                }
                if (page.number < page.totalPages - 1) {
                    query.list.push({
                        label: '下一页',
                        value: page.number + 1
                    });
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
