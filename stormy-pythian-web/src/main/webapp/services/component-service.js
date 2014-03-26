app.factory('ComponentService', function() {

	var Component = function(description) {
		this.description = description;
		
		this.id = this.randomId();
		this.name = description.name;
		
		this.inputStreams = description.inputStreams ? description.inputStreams.map(this.createInputStream): [];
		this.outputStreams = description.outputStreams ? description.outputStreams.map(this.createOutputStream): [];
		this.properties = description.properties ? description.properties.map(this.createProperty): [];
		this.states = description.states ? description.states.map(this.createState): [];
		this.x = 50;
		this.y = 100;
	};
	
	var InputStream = function(description) {
		this.description = description;

		if("USER_SELECTION" == description.type) {
			this.selectedFeatures = [];
		} else if("FIXED_FEATURES" == description.type) {
			this.mappings = {};
			if(description.expectedFeatures) {
				for(var i = 0; i < description.expectedFeatures.length; i++) {
					this.mappings[description.expectedFeatures[i].name] = "";
				}
			}
		}
	};

	var OutputStream = function(description) {
		this.description = description;
		
		if("USER_SELECTION" == description.type) {
			this.selectedFeatures = [];
		} else if("FIXED_FEATURES" == description.type) {
			this.mappings = {};
			if(description.newFeatures) {
				for(var i = 0; i < description.newFeatures.length; i++) {
					this.mappings[description.newFeatures[i].name] = "New feature : " + description.newFeatures[i].name;
				}
			}
		}
	};
	
	var Property = function(description) {
		this.name = description.name;
		this.value = null;
		this.description = description;
	};

	var State = function(description) {
		this.name = description.name;
		this.description = description;
	};
	
	Component.prototype.createInputStream = function(description) {
		return new InputStream(description);
	};

	Component.prototype.createOutputStream = function(description) {
		return new OutputStream(description);
	};

	Component.prototype.createProperty = function(description) {
		return new Property(description);
	};

	Component.prototype.createState = function(description) {
		return new State(description);
	};

	Component.prototype.randomId = function() {
	    return Math.random().toString(36).substr(2) + Math.random().toString(36).substr(2) ; 
	};
	
	return {
		createNewComponent : function(description) {
			return new Component(description);
		},
		
		createProperty : function(description) {
			return new Property(description);
		}
	};
});
