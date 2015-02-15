
angular.module('XR.controllers', ['XR.services'])
.controller('MainCtrl', function($scope, Reputation, peopleGraph, User) {
  var cy; // maybe you want a ref to cy
    $scope.reputation = {};
    $scope.people = {};
    $scope.data = {
     "facebook":"",
     "company":"",
     "twitter":"",
     "linkedin":""
    };

    var setLinks = function(link){
        $scope.data[link.source] = link.value;
    };

    var successCallback = function(data){
        console.log("data is ", data);
        if(data.links !== undefined){
            $scope.data.firstName = data.firstName;
            $scope.data.lastName = data.lastName;
            data.links.forEach(setLinks);
            $scope.people.links = data.links;
            $scope.people.redirects = data.redirects;
            console.log("peopleGraph");
            peopleGraph( $scope.people, $scope.data ).then(function( peopleCy ){
                cy = peopleCy;
                $scope.cyLoaded = true;
            });
        }
    };

    var errorCallback = function(error){
            $scope.reputation = JSON.stringify(error);
    };

    $scope.getReputation = function(data){
        Reputation.save(data,successCallback, errorCallback);
     };
     //graph initer at page call
     User.get(successCallback, errorCallback);
});
