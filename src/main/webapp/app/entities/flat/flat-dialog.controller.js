(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('FlatDialogController', FlatDialogController);

    FlatDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Flat', 'Block'];

    function FlatDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Flat, Block) {
        var vm = this;

        vm.flat = entity;
        vm.clear = clear;
        vm.save = save;
        vm.blocks = Block.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.flat.id !== null) {
                Flat.update(vm.flat, onSaveSuccess, onSaveError);
            } else {
                Flat.save(vm.flat, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('nclsindhuApp:flatUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
