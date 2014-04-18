Feature: trident-ml Perceptron
  As a user of stormy-pythian
  I want to use the trident-ml perceptron
  So that I can use a fast and simple classifier

Scenario: Learn NAND function
	Given a component "stormy.pythian.component.classifier.tridentml.TridentMLPerceptronClassifier" configured with:
		| name				|	Type	|	value			|
		| Bias				|	Double	|	0.0				|
		| Threshold			|	Double	|	0.5				|
		| Learning rate		|	Double	|	0.1				|
		| Classifier name	|	String	|	Test perceptron	|
	And the component has the input "update" listed features: i0, i1, i2
	And the component has the input "query" listed features: i0, i1, i2
	And the component has the output "prediction" named mapper empty
	And the component has the state "Classifier's model" configured with "stormy.pythian.state.memory.InMemoryPythianState":
		| name				|	Type										|	value			|
		| Transaction mode	|	Enum:stormy.pythian.state.TransactionMode	|	NONE			|
		| Name				|	String										|	test-classifier	|
	When the component is deployed
	And I emit to the input "update":
		|	label:BOOLEAN	|	i0:DOUBLE	|	i1:DOUBLE	|	i2:DOUBLE	|
		|	false			|	1.0			|	1.0			|	1.0			|
		|	true			|	1.0			|	-1.0		|	1.0			|
		|	true			|	1.0			|	1.0			|	-1.0		|
		|	true			|	1.0			|	-1.0		|	-1.0		|
		|	false			|	1.0			|	1.0			|	1.0			|
		|	true			|	1.0			|	-1.0		|	1.0			|
		|	true			|	1.0			|	1.0			|	-1.0		|
		|	true			|	1.0			|	-1.0		|	-1.0		|
		|	false			|	1.0			|	1.0			|	1.0			|
		|	true			|	1.0			|	-1.0		|	1.0			|
		|	true			|	1.0			|	1.0			|	-1.0		|
		|	true			|	1.0			|	-1.0		|	-1.0		|
	And I emit to the input "query":
		|	i0:DOUBLE	|	i1:DOUBLE	|	i2:DOUBLE	|
		|	1.0			|	1.0			|	1.0			|
		|	1.0			|	-1.0		|	1.0			|
		|	1.0			|	1.0			|	-1.0		|
		|	1.0			|	-1.0		|	-1.0		|
    Then the output "prediction" should have emit only:
		|	label:BOOLEAN	|	i0:DOUBLE	|	i1:DOUBLE	|	i2:DOUBLE	|
		|	false			|	1.0			|	1.0			|	1.0			|
		|	true			|	1.0			|	-1.0		|	1.0			|
		|	true			|	1.0			|	1.0			|	-1.0		|
		|	true			|	1.0			|	-1.0		|	-1.0		|
