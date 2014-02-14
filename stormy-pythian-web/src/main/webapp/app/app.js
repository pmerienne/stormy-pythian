'use strict';

var app = angular.module('app', [ 'ngRoute', 'ngResource', 'ui.include', 'ui.bootstrap', 'ngMockE2E'])
	.config(function($routeProvider, $httpProvider) {
		$httpProvider.defaults.headers.common = {
			'Accept' : 'application/json',
			'Content-Type' : 'application/json'
		};
	
		$routeProvider.when('/topologies', {
			templateUrl: 'views/topology/list.html',
			controller: 'ListTopologiesCtrl'
		}).when('/topologies/:topologyId', {
			templateUrl: 'views/topology/edit-topology.html',
			controller: 'EditTopologyCtrl'
		}).otherwise({
			redirectTo : '/topologies'
		});

});