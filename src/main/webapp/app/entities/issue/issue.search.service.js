(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .factory('IssueSearch', IssueSearch);

    IssueSearch.$inject = ['$resource'];

    function IssueSearch($resource) {
        var resourceUrl =  'api/_search/issues/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
