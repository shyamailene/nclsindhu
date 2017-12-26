(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('IssueDeleteController',IssueDeleteController);

    IssueDeleteController.$inject = ['$uibModalInstance', 'entity', 'Issue'];

    function IssueDeleteController($uibModalInstance, entity, Issue) {
        var vm = this;

        vm.issue = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Issue.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
