
angular.module('XR.controllers', ['XR.services'])
.controller('MainCtrl', function($scope, Reputation, peopleGraph) {
  var cy; // maybe you want a ref to cy
    $scope.reputation = {};
    $scope.people = {};

    var successCallback = function(data){
    console.log("data is ", data);
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
        data.twitter = data.twitter === undefined? " ": data.twitter;
        data.facebook = data.facebook === undefined? " ": data.facebook;
        data.linkedin = data.linkedin === undefined? " ": data.linkedin;
        Reputation.save(data,successCallback, errorCallback);
     };
});
