(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('OwnerDialogController', OwnerDialogController);

    OwnerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Owner', 'Flat','Flats'];

    function OwnerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Owner, Flat,Flats) {
        var vm = this;

        vm.owner = entity;
        vm.clear = clear;
        vm.save = save;
        vm.flats = Flats.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.owner.id !== null) {
                Owner.update(vm.owner, onSaveSuccess, onSaveError);
            } else {
                Owner.save(vm.owner, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('nclsindhuApp:ownerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
