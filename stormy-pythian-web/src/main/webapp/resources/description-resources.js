app.factory('DescriptionsResource', function ($resource) {
    return $resource('api/descriptions', {}, {
        getDescriptions: { method: 'GET'},
    });
});
