(function() {
    'use strict';
    angular
        .module('nclsindhuApp')
        .factory('Flat', Flat);

    Flat.$inject = ['$resource'];

    function Flat ($resource) {
        var resourceUrl =  'api/flats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });

        this.flatsquery = function(){
            return $resource('/api/flatslist/:id', {
                id: '@id'
            }, {
                'get': {
                    method: 'GET',
                    cache: true
                }
            });
        }
    }
})();
