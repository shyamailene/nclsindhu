(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('BlockDeleteController',BlockDeleteController);

    BlockDeleteController.$inject = ['$uibModalInstance', 'entity', 'Block'];

    function BlockDeleteController($uibModalInstance, entity, Block) {
        var vm = this;

        vm.block = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Block.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
