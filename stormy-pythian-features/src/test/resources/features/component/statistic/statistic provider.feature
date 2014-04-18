Feature: Statistic provider
  As a user of stormy-pythian
  I want to use the statistic provider
  So that I can compute simple statistic to enrich my features

Scenario: Compute mean grouped by field
	Given a component "stormy.pythian.component.statistic.StatisticProvider" configured with:
		| name				|	Type															|	value			|
		| Operation			|	Enum:stormy.pythian.component.statistic.aggregation.Operation	|	MEAN			|
	And the component has the input "in" mappings:
		|	Group by	|	username		|
		|	Compute on	|	call duration	|
	And the component has the output "out" mappings:
		|	Result		|	call duration mean	|
	And the component has the output "prediction" named mapper empty
	And the component has the state "Statistics' state" configured with "stormy.pythian.state.memory.InMemoryPythianState":
		| name				|	Type										|	value				|
		| Transaction mode	|	Enum:stormy.pythian.state.TransactionMode	|	NONE				|
		| Name				|	String										|	test-grouped-mean	|
	When the component is deployed
	And I emit to the input "in":
		|	username:STRING	|	call duration:INTEGER	|
		|	pierre			|	10						|
		|	pierre			|	20						|
		|	jean			|	20						|
		|	jean			|							|
		|	jean			|	40						|
		|	julie			|	20						|
    Then the output "out" should have emit only:
		|	username:STRING	|	call duration:INTEGER	|	call duration mean:DOUBLE	|
		|	pierre			|	10						|	15.0						|
		|	pierre			|	20						|	15.0						|
		|	jean			|	20						|	30.0						|
		|	jean			|							|	30.0						|
		|	jean			|	40						|	30.0						|
		|	julie			|	20						|	20.0						|
		
Scenario: Compute mean on time window
	Given a component "stormy.pythian.component.statistic.StatisticProvider" configured with:
		| name						|	Type															|	value			|
		| Operation					|	Enum:stormy.pythian.component.statistic.aggregation.Operation	|	MEAN			|
		| Window length (in ms)		|	Long															|	86400000		|
		| Slot precision (in ms)	|	Long															|	3600000			|
	And the component has the input "in" mappings:
		|	Date		|	call date		|
		|	Compute on	|	call duration	|
	And the component has the output "out" mappings:
		|	Result		|	last day call's duration mean	|
	And the component has the output "prediction" named mapper empty
	And the component has the state "Statistics' state" configured with "stormy.pythian.state.memory.InMemoryPythianState":
		| name				|	Type										|	value						|
		| Transaction mode	|	Enum:stormy.pythian.state.TransactionMode	|	NONE						|
		| Name				|	String										|	test-mean-on-time-window	|
	When the component is deployed
	And I emit to the input "in":
		|	call date:DATE		|	call duration:INTEGER	|
		|	2014-04-04T10:00	|	10						|
		|	2014-04-04T18:00	|	30						|
		|	2014-04-04T20:00	|	20						|
		|	2014-04-05T12:00	|	40						|
		|	2014-04-05T15:00	|	20						|
		|	2014-04-05T23:00	|	0						|
    Then the output "out" should have emit only:
		|	call date:DATE		|	call duration:INTEGER	|	last day call's duration mean:DOUBLE	|
		|	2014-04-04T10:00	|	10						|	10.0									|
		|	2014-04-04T18:00	|	30						|	20.0									|
		|	2014-04-04T20:00	|	20						|	20.0									|
		|	2014-04-05T12:00	|	40						|	30.0									|
		|	2014-04-05T15:00	|	20						|	27.5									|
		|	2014-04-05T23:00	|	0						|	20.0									|
				
Scenario: Compute mean grouped by field on time window
	Given a component "stormy.pythian.component.statistic.StatisticProvider" configured with:
		| name						|	Type															|	value			|
		| Operation					|	Enum:stormy.pythian.component.statistic.aggregation.Operation	|	MEAN			|
		| Window length (in ms)		|	Long															|	86400000		|
		| Slot precision (in ms)	|	Long															|	3600000			|
	And the component has the input "in" mappings:
		|	Group by	|	username		|
		|	Date		|	call date		|
		|	Compute on	|	call duration	|
	And the component has the output "out" mappings:
		|	Result		|	last day call's duration mean	|
	And the component has the output "prediction" named mapper empty
	And the component has the state "Statistics' state" configured with "stormy.pythian.state.memory.InMemoryPythianState":
		| name				|	Type										|	value								|
		| Transaction mode	|	Enum:stormy.pythian.state.TransactionMode	|	NONE								|
		| Name				|	String										|	test-mean-grouped-by-on-time-window	|
	When the component is deployed
	And I emit to the input "in":
		|	username:STRING	|	call date:DATE		|	call duration:INTEGER	|
		|	pierre			|	2014-04-04T10:00	|	10						|
		|	julie			|	2014-04-04T18:00	|	30						|
		|	julie			|	2014-04-04T20:00	|	20						|
		|	pierre			|	2014-04-05T12:00	|	40						|
		|	julie			|	2014-04-05T15:00	|	10						|
		|	martin			|	2014-04-05T23:00	|	0						|
    Then the output "out" should have emit only:
		|	username:STRING	|	call date:DATE		|	call duration:INTEGER	|	last day call's duration mean:DOUBLE	|
		|	pierre			|	2014-04-04T10:00	|	10						|	10.0									|
		|	julie			|	2014-04-04T18:00	|	30						|	30.0									|
		|	julie			|	2014-04-04T20:00	|	20						|	25.0									|
		|	pierre			|	2014-04-05T12:00	|	40						|	40.0									|
		|	julie			|	2014-04-05T15:00	|	10						|	20.0									|
		|	martin			|	2014-04-05T23:00	|	0						|	0.0										|