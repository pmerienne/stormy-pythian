app.controller('EditComponentCtrl', function($scope, $modalInstance, editedComponent) {

	$scope.saveComponent = function() {
		$modalInstance.close($scope.component);
	};

	$scope.deleteComponent = function() {
		$modalInstance.close(null);
	};

	$scope.isUserSelectionMapping = function(inputStreamDescription) {
		return inputStreamDescription.mappingType == "USER_SELECTION"; 
	};

	$scope.isFixedFeaturesMapping = function(inputStreamDescription) {
		return inputStreamDescription.mappingType == "FIXED"; 
	};
	
	$scope.component = editedComponent;

});