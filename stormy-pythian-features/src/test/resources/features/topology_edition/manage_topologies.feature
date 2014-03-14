Feature: Manage topologies
	As a user of stormy-pythian
	I want to list, create and delete topologies
	So that I can manage topologies

Scenario: Create topology
	Given I'm on the topologies page
	When I click on the create new topology button
	Then I should be redirected to the edit topology page

Scenario: Edit topology
	Given an empty topology named "Topology 1"
	And I'm on the topologies page
	When I click on the edit topology button of "Topology 1"
	Then I should be redirected to the edit topology page

Scenario: Delete topology
	Given an empty topology named "Topology 1"
	And I'm on the topologies page
	When I click on the delete topology button of "Topology 1"
	Then I should not see "Topology 1" in the topologies list

Scenario: List topologies
	Given an empty topology named "Topology 1"
	When I visit the topologies page
	Then I should see "Topology 1" in the topologies list
		
