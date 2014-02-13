app.factory('TopologiesResource', function ($resource) {
    return $resource('api/topologies', {}, {
        findAll: { method: 'GET', isArray: true },
        create: { method: 'PUT' }
    });
});

app.factory('TopologyResource', function ($resource) {
    return $resource('api/topologies/:id', {}, {
        findById: { method: 'GET'},
        remove: { method: 'DELETE', params: {id: '@id'} }
    });
});