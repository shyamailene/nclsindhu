(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('FlatDetailController', FlatDetailController);

    FlatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Flat', 'Block'];

    function FlatDetailController($scope, $rootScope, $stateParams, previousState, entity, Flat, Block) {
        var vm = this;

        vm.flat = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('nclsindhuApp:flatUpdate', function(event, result) {
            vm.flat = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
