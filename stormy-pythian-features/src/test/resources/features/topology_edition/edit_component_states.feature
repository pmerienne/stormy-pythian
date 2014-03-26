Feature: Edit component states
	As a user of stormy-pythian editing a topology
	I want to edit component's state
	So that I can build up a topology containing stateful components

Scenario: List available state
	Given a topology named "Topology 1" with the components:
		|	type	|	component	|	name	|	x	|	y	|
		|	Other	|	Test		|	Test1	|	50	|	50	|
	When I go to the "Test's state" state view of "Test1"
	Then I should see a list of available states

Scenario: Configure in memory state
	Given a topology named "Topology 1" with the components:
		|	type	|	component	|	name	|	x	|	y	|
		|	Other	|	Test		|	Test1	|	50	|	50	|
	When I choose "In memory state" for the "Test's state" state of "Test1" 
	And I set the "Test's state" state of "Test1" properties: 
		| name				|	Type	|	Value		|
		| Transaction mode	|	Enum	|	OPAQUE		|
		| Name				|	String	|	test		|
	Then the "Test's state" state of "Test1" should have the following properties:
		| name				|	Type	|	Value		|
		| Transaction mode	|	Enum	|	OPAQUE		|
		| Name				|	String	|	test		|
