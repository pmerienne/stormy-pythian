app.controller('EditTopologyCtrl', function($scope, $location, $route, $routeParams, $modal,
		JsPlumbService, ComponentService, TopologyResource, TopologiesResource,
		DescriptionsResource) {

	var REDRAW_TIMEOUT = 50;

	$scope.topology = {};

	$scope.displayTopology = function(topology) {
		$scope.topology = topology;

		$scope.diagram = JsPlumbService.createDiagram("jsplumb-container");
		setTimeout(function() {
			$scope.diagram.bindTopologyConnections($scope.topology);
			$scope.redraw();
		}, REDRAW_TIMEOUT);
	};

	TopologyResource.get({ topologyId : $routeParams.topologyId}, function(topology) {
		$scope.displayTopology(topology);
	}, function(error) {
		console.log("Unable to load topology");
		$location.path("topologies/");
	});

	$scope.editComponent = function(component) {
		var modalInstance = $modal.open({
			templateUrl : 'views/component/edit-component-modal.html',
			controller : 'EditComponentCtrl',
			resolve : {
				editedComponent : function() {
					return component;
				}
			}
		});

		modalInstance.result.then(function(editedComponent) {
			if (!editedComponent) {
				$scope.removeComponent(component);
			}
		}, function(component) {
			// Edition canceled
		});
	};

	$scope.removeComponent = function(component) {
		$scope.diagram.clearComponent(component);
		$scope.topology.components.remove(component);
	};

	$scope.save = function() {
		TopologiesResource.save($scope.topology, function(data) {
			console.log("Topology saved");
		}, function(error) {
			console.log("Save failed");
		});
	};

	$scope.remove = function() {
		TopologyResource.remove({topologyId : $scope.topology.id}, function() {
			console.log("Topology deleted");
			$location.path("topologies/");
		});
	};

	$scope.revert = function() {
		$route.reload();
	};

	$scope.addNewComponent = function(description) {
		var component = ComponentService.createNewComponent(description);
		$scope.topology.components.push(component);
		$scope.redraw(true);
	};

	$scope.redraw = function(deffered) {
		if (!deffered) {
			$scope.diagram.jsPlumbInstance.repaintEverything();
		} else {
			setTimeout(function() {
				$scope.diagram.jsPlumbInstance.repaintEverything();
			}, REDRAW_TIMEOUT);
		}
	};

	DescriptionsResource.getComponentDescriptions(function(descriptions) {
		$scope.streamSourcesDescriptions = descriptions.filter(function(
				description) {
			return description.type == 'STREAM_SOURCE';
		});
		$scope.learnersDescriptions = descriptions
				.filter(function(description) {
					return description.type == 'LEARNER';
				});
		$scope.analyticsDescriptions = descriptions
				.filter(function(description) {
					return description.type == 'ANALYTICS';
				});
		$scope.othersDescriptions = descriptions.filter(function(description) {
			return description.type == 'NO_TYPE' || description.type == ''
					|| description.type == null;
		});
	});

	DescriptionsResource.getStateDescriptions(function(descriptions) {
		$scope.stateDescriptions = descriptions;
	});

});