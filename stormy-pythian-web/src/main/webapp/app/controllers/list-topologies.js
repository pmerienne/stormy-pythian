app.controller('ListTopologiesCtrl', function ($scope, $location, TopologiesResource, TopologyResource) {
	
	$scope.topologies = [];
	
	$scope.refreshTopologies = function () {
		$scope.topologies = TopologiesResource.query();
	};
	

    $scope.deleteTopology = function() {
    	var id = this.topology.id;
    	TopologyResource.remove({topologyId: id}, function() {
            $scope.refreshTopologies();
            console.log("Deleted " + id);
		}, function() {
            console.log("Failing deletion of " + id);
		});
    };
    
    $scope.createNewTopology = function() {
    	TopologiesResource.create({}, function(topology) {
    		$location.path("topologies/" + topology.id);
		});
    };
	
	$scope.refreshTopologies();
});