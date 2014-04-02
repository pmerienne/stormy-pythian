Feature: File stream source
  As a user of stormy-pythian
  I want to use the file stream source
  So that I can stream instances from a text file

Scenario: Stream text file
	Given a file "/tmp/lorem.txt" containing:
		"""
		Lorem ipsum dolor sit amet, consectetur adipisicing elit
		sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		"""
	And a component "stormy.pythian.component.source.file.FileSource" configured with:
		| name		|	Type	|	value			|
		| File		|	String	|	/tmp/lorem.txt	|
	And the component has the output "lines" named mappings:
		|	file line		|	line			|
	When the component is deployed
    And I wait 2 seconds
    Then the component's output "lines" should have emit only:
    	|	line:STRING															|
    	| 	Lorem ipsum dolor sit amet, consectetur adipisicing elit			|
    	| 	sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.	|
    	
Scenario: File source supports new lines
	Given a file "/tmp/lorem.txt" containing:
		"""
		Lorem ipsum dolor sit amet, consectetur adipisicing elit
		"""
	And a component "stormy.pythian.component.source.file.FileSource" configured with:
		| name		|	Type	|	value			|
		| File		|	String	|	/tmp/lorem.txt	|
	And the component has the output "lines" named mappings:
		|	file line		|	line			|
	When the component is deployed
    And I wait 2 seconds
	And I append to the file "/tmp/lorem.txt":
		"""
		sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.
		"""
    And I wait 1 second
    Then the component's output "lines" should have emit only:
    	|	line:STRING															|
    	| 	Lorem ipsum dolor sit amet, consectetur adipisicing elit			|
    	| 	sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.	|