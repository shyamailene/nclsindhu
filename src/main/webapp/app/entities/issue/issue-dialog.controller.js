(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('IssueDialogController', IssueDialogController);

    IssueDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Issue', 'User'];

    function IssueDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Issue, User) {
        var vm = this;

        vm.issue = entity;
        vm.clear = clear;
        vm.save = save;
        vm.issues = [
            {issue : "Electrical"},
            {issue : "Carpenter"},
            {issue : "CommonArea"},
            {issue : "Plumbing"},
            {issue : "Security"},
            {issue : "HouseKeeping"},
            {issue : "Other"}
        ];
        vm.types = [
            {type : "Open"},
            {type : "Close"}
        ];
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.issue.id !== null) {
                Issue.update(vm.issue, onSaveSuccess, onSaveError);
            } else {
                Issue.save(vm.issue, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('nclsindhuApp:issueUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
