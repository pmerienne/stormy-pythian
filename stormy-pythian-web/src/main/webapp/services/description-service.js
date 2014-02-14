app.factory('DescriptionService', function() {
	
	function ComponentDescription(id, name, inputStreams, outputStreams, properties) {
		this.id = id;
		this.name =  name;
		this.inputStreams = inputStreams;
		this.outputStreams = outputStreams;
		this.properties = properties;
	};
	
	function DescriptionBuilder() {
		this._id = Math.random().toString(36).substr(2) + Math.random().toString(36).substr(2);
		this._name =  "";
		this._inputStreams = [];
		this._outputStreams = [];
		this._properties = [];
	};
	
	DescriptionBuilder.prototype.name = function(name) {
		this._name = name;
		return this;
	};
	
	DescriptionBuilder.prototype.id = function(id) {
		this._id = id;
		return this;
	};

	DescriptionBuilder.prototype.withFixedFeaturesInputStream = function(name, values, labeled) {
		var is = {name : name, expectedFeatures : {type : "FIXED", values : values, labeled : labeled}};
		this._inputStreams.push(is);
		return this;
	};

	DescriptionBuilder.prototype.withUserSelectedFeaturesInputStream = function(name, labeled) {
		var is = {name : name, expectedFeatures : {type : "USER_SELECTION", labeled : labeled}};
		this._inputStreams.push(is);
		return this;
	};

	DescriptionBuilder.prototype.withOutputStream = function(name, from, newFeatures) {
		var os = {name : name, from : from, newFeatures : newFeatures ?  newFeatures : []};
		this._outputStreams.push(os);
		return this;
	};
	
	DescriptionBuilder.prototype.withProperty = function(name, mandatory) {
		var property = {name : name, mandatory: mandatory};
		this._properties.push(property);
		return this;
	};
	
	DescriptionBuilder.prototype.build = function() {
		return new ComponentDescription(this._id, this._name, this._inputStreams, this._outputStreams, this._properties);
	};

	return {
		builder	 : function() {
			return new DescriptionBuilder();
		}
	};
});
