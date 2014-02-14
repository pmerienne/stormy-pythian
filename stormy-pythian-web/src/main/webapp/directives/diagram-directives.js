app.directive('diagramComponent', function() {
	var COMPONENT_BASE_SIZE = 20;
	var ENDPOINT_SIZE = 20;
	
	return {
		replace: true,
		link: function (scope, element, attrs) {
			var component = JSON.parse(attrs.diagramComponent);
			element.attr("id", component.id);

			scope.diagram.jsPlumbInstance.draggable(element);

			var inputStreamsCount = component.description.inputStreams.length;
			var outputStreamsCount = component.description.outputStreams.length;
			element.css("left", component.x);
			element.css("top", component.y);
			element.css("height", COMPONENT_BASE_SIZE + Math.max(inputStreamsCount, outputStreamsCount) * ENDPOINT_SIZE);

			// Create endpoints for input streams
			for(var i = 0; i < inputStreamsCount; i++) {
				var inputStream = component.description.inputStreams[i];
				var yOffset =  (i + 1) / (inputStreamsCount + 1);
				scope.diagram.jsPlumbInstance.addEndpoint(element, {
					uuid: scope.diagram.getInputStreamId(component.id, inputStream.name),
					isTarget: true,
					maxConnections: 1,
					overlays:[ [ "Label", { label: inputStream.name, location: [2, 0.5], cssClass: "diagram-input-label"}]],
					anchor : [ [ 0, yOffset, -1, 0] ]
				});
			}
			
			// Create endpoints for output streams
			for(var i = 0; i < outputStreamsCount; i++) {
				var outputStream = component.description.outputStreams[i];
				var yOffset =  (i + 1) / (outputStreamsCount + 1);
				scope.diagram.jsPlumbInstance.addEndpoint(element, {
					uuid:  scope.diagram.getOutputStreamId(component.id, outputStream.name),
					isSource: true,
					maxConnections: -1,
					overlays:[ [ "Label", { label: outputStream.name, location: [-1, 0.5], cssClass: "diagram-output-label"}]],
					anchor : [ [ 1, yOffset, 1, 0] ]
				});
			}
			
		}
	};
});
