(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('block', {
            parent: 'entity',
            url: '/block?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'nclsindhuApp.block.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/block/blocks.html',
                    controller: 'BlockController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('block');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('block-detail', {
            parent: 'block',
            url: '/block/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'nclsindhuApp.block.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/block/block-detail.html',
                    controller: 'BlockDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('block');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Block', function($stateParams, Block) {
                    return Block.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'block',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('block-detail.edit', {
            parent: 'block-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/block/block-dialog.html',
                    controller: 'BlockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Block', function(Block) {
                            return Block.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('block.new', {
            parent: 'block',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/block/block-dialog.html',
                    controller: 'BlockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                blockid: null,
                                blockdesc: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('block', null, { reload: 'block' });
                }, function() {
                    $state.go('block');
                });
            }]
        })
        .state('block.edit', {
            parent: 'block',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/block/block-dialog.html',
                    controller: 'BlockDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Block', function(Block) {
                            return Block.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('block', null, { reload: 'block' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('block.delete', {
            parent: 'block',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/block/block-delete-dialog.html',
                    controller: 'BlockDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Block', function(Block) {
                            return Block.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('block', null, { reload: 'block' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
