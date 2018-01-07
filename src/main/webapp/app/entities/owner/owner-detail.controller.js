(function() {
    'use strict';

    angular
        .module('nclsindhuApp')
        .controller('OwnerDetailController', OwnerDetailController);

    OwnerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Owner', 'Flat'];

    function OwnerDetailController($scope, $rootScope, $stateParams, previousState, entity, Owner, Flat) {
        var vm = this;

        vm.owner = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('nclsindhuApp:ownerUpdate', function(event, result) {
            vm.owner = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
