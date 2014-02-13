app.factory('ComponentService', function() {

	var Component = function(description) {
		this.description = description;
		
		this.id = this.randomId();
		this.name = description.name;
		this.shortDescription = description.shortDescription;
		
		this.inputStreams = description.inputStreams ? description.inputStreams.map(this.createInputStream): [];
		this.outputStreams = description.outputStreams ? description.outputStreams.map(this.createOutputStream): [];
		this.properties = description.properties ? description.properties.slice(0) : [];
		this.x = 50;
		this.y = 50;
	};
	
	Component.prototype.randomId = function() {
	    return Math.random().toString(36).substr(2) + Math.random().toString(36).substr(2) ; 
	};
	
	Component.prototype.createInputStream = function(description) {
		var configuration = { //
			name : description.name, //
			type : description.mappingType, //
			labeled : description.labeled
		};
		
		if("USER_SELECTION" == description.mappingType) {
			configuration.selectedFeatures = [];
		} else if("FIXED" == description.mappingType) {
			configuration.mappings = description.expectedFeatures ? description.expectedFeatures.slice(0) : [];
		}
		
		return configuration;
	};

	Component.prototype.createOutputStream = function(description) {
		var configuration = { //
			name : description.name, //
			labeled : description.labeled, //
			mappings : description.newFeatures ? description.newFeatures.slice(0) : []
		};
		
		return configuration;
	};

	Component.prototype.setProperty = function(name, value) {
		this.properties.forEach(function(property) {
		    if(property.name == name) {
		    	property.value = value;
		    }
		});
	};
	
	return {
		createNewComponent : function(description) {
			return new Component(description);
		}
	};
});
