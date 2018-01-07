(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('BlockDetailController', BlockDetailController);

    BlockDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Block'];

    function BlockDetailController($scope, $rootScope, $stateParams, previousState, entity, Block) {
        var vm = this;

        vm.block = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('nclsindhuApp:blockUpdate', function(event, result) {
            vm.block = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
