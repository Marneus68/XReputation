
angular.module('XR.controllers', ['XR.services'])
.controller('MainCtrl', function($scope, Reputation, peopleGraph) {
  var cy; // maybe you want a ref to cy
    $scope.reputation = {};
    $scope.people = {};

    var successCallback = function(data){
    console.log("data is ", data);
        $scope.reputation = JSON.stringify(data);
        console.log("data is ", data);
        $scope.reputation = JSON.stringify(data);
        $scope.people.links = data.links;
        $scope.people.redirects = data.redirects;
        console.log("peopleGraph");
        peopleGraph( $scope.people ).then(function( peopleCy ){
            cy = peopleCy;
            $scope.cyLoaded = true;
        });
    };

    var errorCallback = function(error){
            $scope.reputation = JSON.stringify(error);
    };

    $scope.getReputation = function(data){
        Reputation.save(data,successCallback, errorCallback);
     };
});
