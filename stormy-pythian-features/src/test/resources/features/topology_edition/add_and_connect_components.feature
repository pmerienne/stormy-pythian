Feature: Add and connect components
	As a user of stormy-pythian editing a topology
	I want to add components and connect them together
	So that I can build up a topology

Scenario: Add a new component
	Given an empty topology named "Topology 1"
	And I'm on the "Topology 1" edition page
	When I add a new "Stream source" "Csv stream source" component named "Test source"
	Then I should see a component named "Test source"

Scenario: Connect components
	Given a topology named "Topology 1" with the components:
		|	type			|	component				|	name				|	x	|	y	|
		|	Stream source	|	Csv stream source		|	Test Csv			|	50	|	50	|
		|	Learner			|	Perceptron classifier	|	Test classifier		|	400	|	50	|
	And I'm on the "Topology 1" edition page
	When I connect "Test Csv" "lines" to "Test classifier" "update"
#	Then I should see a link between "Test Csv" "lines" and "Test classifier" "update"
