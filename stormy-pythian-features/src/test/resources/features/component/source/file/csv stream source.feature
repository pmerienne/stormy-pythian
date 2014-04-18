Feature: Csv stream source
  As a user of stormy-pythian
  I want to use the csv stream source
  So that I can stream instances from a csv file

Scenario: Stream csv file
	Given a file "/tmp/data.csv" containing:
		"""
		Julie,1981,Female
		Pierre,1987,Male
		"""
	And a component "stormy.pythian.component.source.file.CsvSource" configured with:
		| name		|	Type	|	value			|
		| File		|	String	|	/tmp/data.csv	|
	And the component has the output "output" listed features: Name, Birthdate, Gender
	When the component is deployed
    Then the component's output "output" should have emit only:
    	|	Name:STRING	|	Birthdate:STRING	|	Gender:STRING	|
    	| 	Julie		|	1981				|	Female			|
    	| 	Pierre		|	1987				|	Male			|

Scenario: Csv source supports new lines
	Given a file "/tmp/data.csv" containing:
		"""
		Julie,1981,Female
		"""
	And a component "stormy.pythian.component.source.file.CsvSource" configured with:
		| name		|	Type	|	value			|
		| File		|	String	|	/tmp/data.csv	|
		| Tte		|	Integer	|	64				|
	And the component has the output "output" listed features: Name, Birthdate, Gender
	When the component is deployed
	And I append to the file "/tmp/data.csv":
		"""
		Pierre,1987,Male
		"""
    And I wait 1 second
    Then the component's output "output" should have emit only:
    	|	Name:STRING	|	Birthdate:STRING	|	Gender:STRING	|
    	| 	Julie		|	1981				|	Female			|
    	| 	Pierre		|	1987				|	Male			|