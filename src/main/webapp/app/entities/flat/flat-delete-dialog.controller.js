(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('FlatDeleteController',FlatDeleteController);

    FlatDeleteController.$inject = ['$uibModalInstance', 'entity', 'Flat'];

    function FlatDeleteController($uibModalInstance, entity, Flat) {
        var vm = this;

        vm.flat = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Flat.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
