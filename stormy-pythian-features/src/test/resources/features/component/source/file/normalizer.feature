Feature: Normalizer
  As a user of stormy-pythian
  I want to use the normalizer
  So that I can have normalized features

Scenario: Normalize features
	Given a component "stormy.pythian.component.preprocessor.Normalizer"
	And the component has the input "in" listed features: f0, f1, f2
	When the component is deployed
	And I emit to the input "in":
		|	f0:DOUBLE	|	f1:DOUBLE	|	f2:DOUBLE	|
		|	-3.0		|	4.0			|	0.0			|
    Then the output "out" should have emit only:
		|	f0:DOUBLE	|	f1:DOUBLE	|	f2:DOUBLE	|
		|	-0.6		|	0.8			|	0.0			|