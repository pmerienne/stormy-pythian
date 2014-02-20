app.factory('TopologiesResource', function ($resource) {
    return $resource('api/topologies', {}, {
        save: { method: 'PUT' }
    });
});

app.factory('TopologyResource', function ($resource) {
    return $resource('api/topologies/:topologyId', {topologyId:'@id'});
});