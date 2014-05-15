Feature: Map component's inputs/outputs
	As a user of stormy-pythian editing a topology
	I want to edit component's mappings
	So that I can select components' intput/output features

Scenario: Select listed output features
	Given a topology named "Topology 1" with the components:
		|	type			|	component				|	name				|	x	|	y	|
		|	Stream source	|	Csv source				|	User source			|	50	|	50	|
		|	Learner			|	Perceptron classifier	|	User classifier		|	600	|	50	|
	And "User source" "output" is connected to "User classifier" "update"
	When I add the features username,age,gender to the listed output mapping "output" of "User source"
	Then I should be proposed username,age,gender in the listed input mapping "update" of "User classifier"
	
Scenario: Select named output features
	Given a topology named "Topology 2" with the components:
		|	type			|	component				|	name				|	x	|	y	|
		|	Stream source	|	File source				|	Log source			|	50	|	50	|
		|	Learner			|	Perceptron classifier	|	User classifier		|	600	|	50	|
	And "Log source" "output" is connected to "User classifier" "update"
	When I set the "file line" of the output mapping "output" of component "Log source" to "user activity"
	Then I should be proposed user activity in the listed input mapping "update" of "User classifier"
