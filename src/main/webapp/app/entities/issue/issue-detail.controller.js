(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('IssueDetailController', IssueDetailController);

    IssueDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Issue', 'User'];

    function IssueDetailController($scope, $rootScope, $stateParams, previousState, entity, Issue, User) {
        var vm = this;

        vm.issue = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('nclsindhuApp:issueUpdate', function(event, result) {
            vm.issue = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
