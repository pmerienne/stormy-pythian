app.directive('stateForm', function($compile) {
	return {
		restrict : "E",
		replace : true,
		templateUrl : 'views/state/edit-state.html',
		scope : {
			state : "=model"
		}, controller : function($scope, DescriptionsResource, ComponentService) {
			$scope.availableDescriptions = [];
			
			DescriptionsResource.getStateDescriptions(function(descriptions) {
				$scope.availableDescriptions = descriptions;
			});
			
			$scope.resetState = function() {
				var propertyDescriptions = $scope.state.description.properties;
				$scope.state.properties = propertyDescriptions ? propertyDescriptions.map(ComponentService.createProperty): [];
				console.log($scope.state.properties);
		    };
		}
	};
});