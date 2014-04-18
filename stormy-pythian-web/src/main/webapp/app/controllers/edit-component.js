app.controller('EditComponentCtrl', function($scope, $modalInstance, component, topology) {

	$scope.component = component;
	$scope.topology = topology;

	$scope.saveComponent = function() {
		$modalInstance.close($scope.component);
	};

	$scope.deleteComponent = function() {
		$modalInstance.close(null);
	};

	$scope.retrieveAvailableFeatures = function(inputStreamName) {
		var componentId = $scope.component.id;
		return $scope.retrieveRecursivelyAvailableFeatures(componentId, inputStreamName);
	};
	
	$scope.retrieveRecursivelyAvailableFeatures = function(componentId, inputStreamName) {
		var connection = null;
		$scope.topology.connections.forEach(function(candidate) {
			if(candidate.to.id === componentId && candidate.to.stream === inputStreamName) {
				connection = candidate;
			}
		});
		if(connection == null) {
			return [];
		}
		
		var otherComponent = null;
		$scope.topology.components.forEach(function(candidate) {
			if(candidate.id == connection.from.id) {
				otherComponent = candidate;
			}
		});
		if(otherComponent == null) {
			return [];
		}
		
		
		var outputStream = null;
		otherComponent.outputStreams.forEach(function(candidate) {
			if(candidate.description.name == connection.from.stream) {
				outputStream = candidate;
			}
		});
		if(outputStream == null) {
			return [];
		}
		
		var newFeatures = null;
		if(outputStream.description.type == 'LISTED') {
			newFeatures = outputStream.selectedFeatures;
		} else if(outputStream.description.type == 'NAMED') {
			newFeatures = Object.keys(outputStream.mappings).map(function(key){return outputStream.mappings[key];});
		}
		if(newFeatures == null) {
			return [];
		}
		
		if(outputStream.description.from != null) {
			newFeatures = newFeatures.concat($scope.retrieveRecursivelyAvailableFeatures(otherComponent.id, outputStream.description.from));
		}
		
		
		return newFeatures;
	};

});