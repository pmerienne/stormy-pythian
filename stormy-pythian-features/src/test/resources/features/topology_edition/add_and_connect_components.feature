Feature: Add and connect components
	As a user of stormy-pythian editing a topology
	I want to add components and connect them together
	So that I can build up a topology

Scenario: Add a new component
	Given an empty topology named "Topology 1"
	And I'm on the "Topology 1" edition page
	When I add a new "Stream source" component : "Csv stream source"
	Then I should see a component named "Csv stream source"
	
Scenario: Connect components
	Given a topology named "Topology 1" with the components:
		|	type			|	name					|	x	|	y	|
		|	Stream source	|	Csv stream source		|	50	|	50	|
		|	Learner			|	Perceptron classifier	|	250	|	50	|
	And I'm on the "Topology 1" edition page
	When I connect "Csv stream source" "lines" to "Perceptron classifier" "update"
	Then I should see a link between "Csv stream source" "lines" and "Perceptron classifier" "update"