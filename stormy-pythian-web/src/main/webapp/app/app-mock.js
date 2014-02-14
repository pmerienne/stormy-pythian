app.run(function($httpBackend, DescriptionService, ComponentService) {
	// Don't mock the html views
	$httpBackend.whenGET(/views\/\w+.*/).passThrough();

	// Descriptions
	var regressorDescription = DescriptionService.builder() //
		.id(0) //
		.name("Regressor") //
		.withUserSelectedFeaturesInputStream("update", true) //
		.withUserSelectedFeaturesInputStream("predict", false) //
		.withOutputStream("prediction", "predict", [ {name : "predicted", type : "FLOAT"}]) //
		.withProperty("lambda", false) //
		.withProperty("alpha", true) //
		.build();

	var monitoringLogParserDescription = DescriptionService.builder() //
		.id(1) //
		.name("Monitoring log parser") //
		.withFixedFeaturesInputStream("log line", [ {name : "line", type : "TEXT"}]) //
		.withOutputStream("data", "log line", [{name : "path", type : "TEXT"}, {name : "response time", type : "INTEGER"}]) //
		.build();

	var filterNullDescription = DescriptionService.builder() //
		.id(2) //
		.name("Filter instance with null feature") //
		.withUserSelectedFeaturesInputStream("in") //
		.withOutputStream("out", "in", []) //
		.build();
	
	var tweetSourceDescription = DescriptionService.builder() //
		.id(0) //
		.name("Tweet source") //
		.withOutputStream("tweet", null, [{name : "user", type : "TEXT"}, {name : "date", type : "INTEGER"}, {name : "content", type : "TEXT"}]) //
		.withProperty("query", true) //
		.withProperty("token", true) //
		.build();

	
	var jmsSourceDescription = DescriptionService.builder() //
		.id(1) //
		.name("JMS source") //
		.withOutputStream("message", null, [{name : "uuid", type : "TEXT"}, {name : "date", type : "INTEGER"}, {name : "content", type : "TEXT"}]) //
		.withProperty("port", true) //
		.withProperty("ip", true) //
		.withProperty("queue", true) //
		.build();
	
	var fileSourceDescription = DescriptionService.builder() //
		.id(2) //
		.name("File source") //
		.withOutputStream("lines", null, [{name : "line", type : "TEXT"}]) //
		.withProperty("path", true) //
		.build();
	
	$httpBackend.whenGET('api/descriptions').respond({
		"COMPONENT" : [regressorDescription, monitoringLogParserDescription, filterNullDescription], //
		"STREAM_SOURCE" : [tweetSourceDescription, jmsSourceDescription, fileSourceDescription] //
	});

	// Topologies
	var fileSource = ComponentService.createNewComponent(fileSourceDescription);
	fileSource.x = 100;
	fileSource.y = 100;
//	fileSource.outputStreams[0].mappings = [ {
//		inside : "line",
//		outside : "monitoring.log line"
//	} ];

	var logParser = ComponentService.createNewComponent(monitoringLogParserDescription);
	logParser.x = 400;
	logParser.y = 100;
//	logParser.inputStreams[0].mappings = [ {
//		inside : "line",
//		outside : "monitoring.log line"
//	} ];
//	logParser.outputStreams[0].mappings = [ {
//		inside : "path",
//		outside : "request path"
//	}, {
//		inside : "time",
//		outside : "request time"
//	} ];

	var nullFilter = ComponentService.createNewComponent(filterNullDescription);
	nullFilter.x = 700;
	nullFilter.y = 200;
//	nullFilter.inputStreams[0].mappings = [ {
//		inside : "data",
//		outside : "monitoring data"
//	} ];
//	nullFilter.outputStreams[0].mappings = [ {
//		inside : "not null data",
//		outside : "not null monitoring data"
//	} ];

	var regressor = ComponentService.createNewComponent(regressorDescription);
	regressor.x = 1000;
	regressor.y = 100;
	regressor.setProperty("lambda", 42.43);
//	regressor.inputStreams[0].mappings = [ {
//		inside : "features",
//		outside : "request path"
//	}, {
//		inside : "value",
//		outside : "request time"
//	} ];
//	regressor.inputStreams[1].mappings = [ {
//		inside : "features",
//		outside : ""
//	} ];
//	regressor.outputStreams[0].mappings = [ {
//		inside : "predicted",
//		outside : ""
//	} ];

	var performanceMonitoring = {
		id : 0,
		name : 'Performance monitoring',
		components : [ fileSource, logParser, nullFilter, regressor ],
		connections : [ {
			from : {
				id : fileSource.id,
				stream : "lines"
			},
			to : {
				id : logParser.id,
				stream : "log line"
			}
		}, {
			from : {
				id : logParser.id,
				stream : "data"
			},
			to : {
				id : nullFilter.id,
				stream : "in"
			}
		}, {
			from : {
				id : nullFilter.id,
				stream : "out"
			},
			to : {
				id : regressor.id,
				stream : "update"
			}
		} ]
	};

	var clicksStream = {
		id : 1,
		name : 'Clicks stream'
	};
	var preferences = {
		id : 2,
		name : 'Preferences'
	};

	var topologies = [ performanceMonitoring, clicksStream, preferences ];
	$httpBackend.whenGET('api/topologies').respond(topologies);
	$httpBackend.whenGET('api/topologies/0').respond(performanceMonitoring);
	$httpBackend.whenGET('api/topologies/1').respond(clicksStream);
	$httpBackend.whenGET('api/topologies/2').respond(preferences);
	
	$httpBackend.whenPUT('api/topologies').respond({
		id: 3
	});

	$httpBackend.whenDELETE('api/topologies/0').respond(503);
	$httpBackend.whenDELETE('api/topologies/1').respond(404);
	$httpBackend.whenDELETE('api/topologies/2').respond(200);
});