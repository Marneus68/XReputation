
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

    $scope.getReputation = function(data){
        Reputation.save(data,successCallback, errorCallback);
     };
})
.controller('PeopleCtrl', [ '$scope', 'peopleGraph', function( $scope, peopleGraph ){
  var cy; // maybe you want a ref to cy
  // (usually better to have the srv as intermediary)

  $scope.people = [
    { id: '1', name: 'toto.facebook.fr', weight: 100 },
    { id: '2', name: 'toto.fr', weight: 100 },
    { id: '3', name: 'toto.twitter.fr', weight: 100 }

  ];

  var peopleById = {};
  for( var i = 0; i < $scope.people.length; i++ ){
    var p = $scope.people[i];

    peopleById[ p.id ] = p;
  }

  // you would probably want some ui to prevent use of PeopleCtrl until cy is loaded
  peopleGraph( $scope.people ).then(function( peopleCy ){
    cy = peopleCy;

    // use this variable to hide ui until cy loaded if you want
    $scope.cyLoaded = true;
  });

  $scope.onWeightChange = function(person){
     peopleGraph.setPersonWeight( person.id, person.weight );
  };

  peopleGraph.onWeightChange(function(id, weight){
    peopleById[id].weight = weight;

    $scope.$apply();
  });

} ]);