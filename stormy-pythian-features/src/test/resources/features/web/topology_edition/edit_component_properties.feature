Feature: Edit component properties
	As a user of stormy-pythian editing a topology
	I want to edit component's properties
	So that I can build up a topology

Scenario: Edit properties
	Given a topology named "Topology 1" with the components:
		|	type	|	component	|	name				|	x	|	y	|
		|	Other	|	Test		|	Test1				|	50	|	50	|
	When I set the "Test1" component properties: 
		| name				|	Type	|	Value		|
		| Integer property	|	Integer	|	42			|
		| Double property	|	Double	|	42.42		|
		| String property	|	String	|	The answer	|
		| Enum property		|	Enum	|	VALUE3		|
#		| Boolean property	|	Boolean	|	true		|
	Then the component "Test1" should have the following properties:
		| name				|	Type	|	Value		|
		| Integer property	|	Integer	|	42			|
		| Double property	|	Double	|	42.42		|
		| String property	|	String	|	The answer	|
		| Enum property		|	Enum	|	VALUE3		|
#		| Boolean property	|	Boolean	|	true		|