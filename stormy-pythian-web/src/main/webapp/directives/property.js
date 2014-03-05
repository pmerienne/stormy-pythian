app.directive('property', function($compile) {
	return {
		restrict : "E",
		replace : true,
		templateUrl : 'views/component/edit-property.html',
		scope : {
			property : "=model"
		}
	};
});