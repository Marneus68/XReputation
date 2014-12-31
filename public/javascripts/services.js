
angular.module('XR.services', ['ngResource'])
.factory('Reputation', function($resource) {
  return $resource("http://localhost:9000" + '/reputation?v' + new Date().getTime());
});
