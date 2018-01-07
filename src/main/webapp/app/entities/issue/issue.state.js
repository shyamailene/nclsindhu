(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('issue', {
            parent: 'entity',
            url: '/issue?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'nclsindhuApp.issue.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/issue/issues.html',
                    controller: 'IssueController',
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
                    $translatePartialLoader.addPart('issue');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('issue-detail', {
            parent: 'issue',
            url: '/issue/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'nclsindhuApp.issue.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/issue/issue-detail.html',
                    controller: 'IssueDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('issue');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Issue', function($stateParams, Issue) {
                    return Issue.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'issue',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('issue-detail.edit', {
            parent: 'issue-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/issue/issue-dialog.html',
                    controller: 'IssueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Issue', function(Issue) {
                            return Issue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('issue.new', {
            parent: 'issue',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/issue/issue-dialog.html',
                    controller: 'IssueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                subject: null,
                                type: null,
                                description: null,
                                status: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('issue', null, { reload: 'issue' });
                }, function() {
                    $state.go('issue');
                });
            }]
        })
        .state('issue.edit', {
            parent: 'issue',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/issue/issue-dialog.html',
                    controller: 'IssueDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Issue', function(Issue) {
                            return Issue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('issue', null, { reload: 'issue' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('issue.delete', {
            parent: 'issue',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/issue/issue-delete-dialog.html',
                    controller: 'IssueDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Issue', function(Issue) {
                            return Issue.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('issue', null, { reload: 'issue' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
