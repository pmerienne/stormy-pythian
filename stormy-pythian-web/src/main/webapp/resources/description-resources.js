app.factory('DescriptionsResource', function ($resource) {
    return $resource('api/descriptions/', {}, {
    	getComponentDescriptions: { method: 'GET', url: 'api/descriptions/components', isArray : true},
    	getStateDescriptions: { method: 'GET', url: 'api/descriptions/states', isArray : true},
    });
});
