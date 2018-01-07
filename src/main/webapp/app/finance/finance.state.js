(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider.state('finance', {
            parent: 'app',
            url: '/finance',
            data: {
                authorities: []
            },
            views: {
                'content@': {
                    templateUrl: 'app/finance/home.html',
                    controller: 'FinanceController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate,$translatePartialLoader) {
                    $translatePartialLoader.addPart('finance');
                    return $translate.refresh();
                }]
            }
        });
    }
})();
