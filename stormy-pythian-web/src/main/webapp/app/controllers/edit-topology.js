app.controller('EditTopologyCtrl', function (
		$scope, $routeParams, $modal,
		JsPlumbService, ComponentService, 
		TopologyResource, DescriptionsResource) {
	
	var REDRAW_TIMEOUT = 50;
	
	$scope.topology = {components: [], connections: []};
	
	$scope.displayTopology = function(topology) {
		$scope.topology = topology;
		
		$scope.diagram = JsPlumbService.createDiagram("jsplumb-container");
		setTimeout(function() {
			$scope.diagram.bindTopologyConnections($scope.topology);
			$scope.redraw();
		}, REDRAW_TIMEOUT);
		
	};
	
	$scope.editComponent = function (component) {
	    var modalInstance = $modal.open({
			templateUrl : 'views/component/edit-component-modal.html',
			controller: 'EditComponentCtrl', 
			resolve : {
				editedComponent : function() {
					return component;
				}
			}
		});

		modalInstance.result.then(
			function(editedComponent) {
				if(!editedComponent) {
					$scope.removeComponent(component);
				}
			}, function(component) {
				// Edition canceled
			}
		);
	};
	
	$scope.removeComponent = function(component) {
		$scope.diagram.clearComponent(component);
		$scope.topology.components.remove(component);
	};

	$scope.save = function () {
		console.log($scope.topology);
		console.log($scope.topology.components);
		$scope.redraw(true);
	};
	
	$scope.revert = function () {
		if($scope.diagram) {
			$scope.diagram.reset();
		}

		TopologyResource.get({topologyId: $routeParams.topologyId}, function(topology) {
			$scope.displayTopology(topology);
		});
	};
	
	$scope.addNewComponent = function(description) {
		var component = ComponentService.createNewComponent(description);
		$scope.topology.components.push(component);
		$scope.redraw(true);
	};

	$scope.redraw = function(deffered) {
		if(!deffered) {
			$scope.diagram.jsPlumbInstance.repaintEverything();
		} else {
			setTimeout(function() {
				$scope.diagram.jsPlumbInstance.repaintEverything();
			}, REDRAW_TIMEOUT);
		}
	};
	
	TopologyResource.get({topologyId: $routeParams.topologyId}, function(topology) {
		$scope.displayTopology(topology);
	});
	
	DescriptionsResource.getDescriptions(function(descriptions) {
		$scope.componentDescriptions = descriptions["COMPONENT"];
		$scope.streamSourceDescriptions = descriptions["STREAM_SOURCE"];
	});

});