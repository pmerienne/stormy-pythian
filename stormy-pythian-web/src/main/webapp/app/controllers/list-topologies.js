app.controller('ListTopologiesCtrl', function ($scope, $location, TopologiesResource) {
	
	$scope.topologies = [];
	
	$scope.refreshTopologies = function () {
		$scope.topologies = TopologiesResource.findAll();
	};
	

    $scope.deleteTopology = function() {
    	var id = this.topology.id;
        console.log(id);
    };
    
    $scope.createNewTopology = function() {
    	TopologiesResource.create(function(topology) {
    		$location.path("topologies/" + topology.id);
		});
    };
	
	$scope.refreshTopologies();
});