Array.prototype.indexOfObject = function(obj) {
	for ( var i = 0, len = this.length; i < len; i++) {
		if (angular.equals(this[i], obj))
			return i;
	}
	return -1;
};

Array.prototype.remove = function(obj) {
	var index = this.indexOfObject(obj);

	if (index > -1) {
		this.splice(index, 1);
	}
};

