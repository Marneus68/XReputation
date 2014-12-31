
angular.module('XR.controllers', ['XR.services'])
.controller('MainCtrl', function($scope, Reputation) {
    $scope.reputation = {};

    var successCallback = function(data){
    console.log("data is ", data);
        $scope.reputation = JSON.stringify(data);
    };

    var errorCallback = function(error){
            $scope.reputation = JSON.stringify(error);
    };

    $scope.getReputation = function(infos){

        Reputation.save({toto:'toto'},successCallback, errorCallback);
     };
});