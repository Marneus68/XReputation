
angular.module('XR.services', ['ngResource'])
.factory('Reputation', function($resource) {
  return $resource("http://localhost:7000" + '/reputation?v' + new Date().getTime());
})
.factory('User', function($resource) {
  return $resource("http://localhost:7000" + '/reputation/user?v' + new Date().getTime());
})
.factory('peopleGraph', [ '$q', function( $q ){
  var cy;
  var peopleGraph = function(people, data){
    var deferred = $q.defer();

    // put people model in cy.js
    var eles = [];
    var links = people.links.concat(people.redirects);
    console.log(data);

    // set root
    eles.push({ group: 'nodes', data: { id:"0", name: data.firstName, weight: 200, faveShape: 'rectangle' }, classes: 'root' });

    for( var i = 0; i < people.links.length; i++ ){
      eles.push({group: 'nodes', data: { id: links[i].value,  weight: 100, name: links[i].value, faveShape:'rectangle' }, classes:'ko'});
      eles.push({group: 'edges', data: {source:links[i].value, target:"0" }});
    }
    for( var i = 0; i < people.redirects.length; i++ ){
      eles.push({group: 'nodes', data: { id: people.redirects[i].value,  weight: 100, name: people.redirects[i].value, faveShape:'rectangle' }, classes:'ok'});
    }

    var redirects =  people.redirects;
      var target = {"l1":data.twitter, "l2":data.facebook, "l3":data.linkedin, "l4":data.company};
    for(var i = 0; i < redirects.length; i++){
      eles.push({group: 'edges', data: {source:redirects[i].value, target:target[redirects[i].id] }});
    }
    //links to root

/*
      eles.push({group: 'nodes', data: { id: people[1].id,  weight: people[1].weight, name: people[1].name, faveShape:'circle' }, classes:'ok'});
      eles.push({group: 'nodes', data: { id: people[2].id,  weight: people[2].weight, name: people[2].name, faveShape:'circle' }, classes:'ko'});


eles.push({group: 'edges', data: {source:people[2].id,target:people[0].id }});
*/



    $(function(){ // on dom ready

      cy = cytoscape({
        container: $('#cy')[0],
                style: cytoscape.stylesheet()
                  .selector('node')
                    .css({
                      'font-size': '14px',
                      'content': 'data(name)',
                      'text-valign': 'center',
                      'color': 'white',
                      'text-outline-width': 2,
                      'text-outline-color': '#000',
                      'width': 'mapData(weight, 10, 160, 10, 160)',
                      //'shape': 'data(faveShape)'
                    })
                  .selector('.ok')
                    .css({
                      'background-color': '#A1BD2A'
                    })
                  .selector('.middle')
                    .css({
                      'background-color': 'orange'
                    })
                  .selector('.ko')
                    .css({
                      'background-color': '#E30613'
                    })
                  .selector('edge')
                    .css({
                     //'width': 6,
                      'target-arrow-shape': 'triangle'
                      //'target-arrow-shape': 'triangle',
                        //      'line-color': '#ffaaaa',
                          //    'target-arrow-color': '#ffaaaa'
                  })
                  .selector('.root')
                    .css({
                      'color': '#000',
                      'line-color': 'yellow',
                      'target-arrow-color': 'yellow',
                      'source-arrow-color': 'yellow',
                      'text-outline-color': 'yellow'
                    })
                  .selector(':selected')
                    .css({
                      'color': '#000',
                      'line-color': 'yellow',
                      'target-arrow-color': 'yellow',
                      'source-arrow-color': 'yellow',
                      'text-outline-color': 'yellow'
                    }),
                layout: {
                  /*
                  name: 'breadthfirst',
                  fit: true, // whether to fit the viewport to the graph
                  ready: undefined, // callback on layoutready
                  stop: undefined, // callback on layoutstop
                  directed: true, // whether the tree is directed downwards (or edges can point in any direction if false)
                  padding: 5, // padding on fit
                  circle: false, // put depths in concentric circles if true, put depths top down if false
                  roots: undefined, // the roots of the trees
                  maximalAdjustments: 0 // how many times to try to position the nodes in a maximal way (i.e. no backtracking)
                  */
                  /*
                  name: 'concentric',
                  concentric: function(){
                    return this.data('score');
                  },
                  levelWidth: function(nodes){
                    return 0.5;
                  },
                  padding: 10
                  */

                  name: 'cose',
                  liveUpdate: true, // whether to show the layout as it's running
                  ready: undefined, // callback on layoutready
                  stop: undefined, // callback on layoutstop
                  maxSimulationTime: 20000, // max length in ms to run the layout
                  fit: false, // reset viewport to fit default simulationBounds
                  padding: [ 50, 50, 50, 50 ], // top, right, bottom, left
                  simulationBounds: undefined, // [x1, y1, x2, y2]; [0, 0, width, height] by default
                  ungrabifyWhileSimulating: true, // so you can't drag nodes during layout
                  // forces used by arbor (use arbor default on undefined)
                  repulsion: undefined,
                  stiffness: undefined,
                  friction: undefined,
                  gravity: true,
                  fps: undefined,
                  precision: undefined,
                  // static numbers or functions that dynamically return what these
                  // values should be for each element
                  nodeMass: undefined,
                  edgeLength: undefined,
                  stepSize: 1, // size of timestep in simulation
                  // function that returns true if the system is stable to indicate
                  // that the layout can be stopped
                  stableEnergy: function( energy ){
                    var e = energy;
                    return (e.max <= 0.5) || (e.mean <= 0.3);
                  },
                  infinite: false
                },
        elements: eles,

        ready: function(){
          deferred.resolve( this );

          cy.on('cxtdrag', 'node', function(e){
            var node = this;
            var dy = Math.abs( e.cyPosition.x - node.position().x );
            var weight = Math.round( dy*2 );

            node.data('weight', weight);

            fire('onWeightChange', [ node.id(), node.data('weight') ]);
          });
        }
      });
       window.cy = cy;

        cy.load(eles, function() {}, function() {
                     cy.$('node').on('click', function(e){
                       console.log('click')
                       var ele = e.cyTarget;
                       loadLinks(ele.id());
                       //launchFullScreen($('#links-boutique-lacoste-fr .col-md-12')[0]);
                       //$('#links-boutique-lacoste-fr')

                     });
                  });

    }); // on dom ready

    return deferred.promise;
  };

  peopleGraph.listeners = {};

  function fire(e, args){
    var listeners = peopleGraph.listeners[e];

    for( var i = 0; listeners && i < listeners.length; i++ ){
      var fn = listeners[i];

      fn.apply( fn, args );
    }
  }

  function listen(e, fn){
    var listeners = peopleGraph.listeners[e] = peopleGraph.listeners[e] || [];

    listeners.push(fn);
  }

  peopleGraph.setPersonWeight = function(id, weight){
    cy.$('#' + id).data('weight', weight);
  };

  peopleGraph.onWeightChange = function(fn){
    listen('onWeightChange', fn);
  };

  return peopleGraph;


} ]);

