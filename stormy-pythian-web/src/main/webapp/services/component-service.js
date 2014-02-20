app.factory('ComponentService', function() {

	var Component = function(description) {
		this.description = description;
		
		this.id = this.randomId();
		
		this.inputStreams = description.inputStreams ? description.inputStreams.map(this.createInputStream): [];
		this.outputStreams = description.outputStreams ? description.outputStreams.map(this.createOutputStream): [];
		this.properties = description.properties ? description.properties.slice(0) : [];
		this.x = 50;
		this.y = 100;
	};
	
	var InputStream = function(description) {
		this.description = description;

		if("USER_SELECTION" == description.type) {
			this.selectedFeatures = [];
		} else if("FIXED" == description.mappingType) {
			this.mappings = description.expectedFeatures ? description.expectedFeatures.slice(0) : [];
		}
	};

	var OutputStream = function(description) {
		this.description = description;
		
		if("USER_SELECTION" == description.type) {
			this.selectedFeatures = [];
		} else if("FIXED" == description.mappingType) {
			this.mappings = description.newFeatures ? description.newFeatures.slice(0) : [];
		}
		
		this.mappings = {};
		this.selectedFeatures = [];
	};
	
	Component.prototype.createInputStream = function(description) {
		return new InputStream(description);
	};

	Component.prototype.createOutputStream = function(description) {
		return new OutputStream(description);
	};

	Component.prototype.setProperty = function(name, value) {
		this.properties.forEach(function(property) {
		    if(property.name == name) {
		    	property.value = value;
		    }
		});
	};
	
	Component.prototype.randomId = function() {
	    return Math.random().toString(36).substr(2) + Math.random().toString(36).substr(2) ; 
	};
	
	return {
		createNewComponent : function(description) {
			return new Component(description);
		}
	};
});
