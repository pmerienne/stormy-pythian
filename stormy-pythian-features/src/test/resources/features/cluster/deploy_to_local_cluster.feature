Feature: Deploy topology to a local cluster
	As a user of stormy-pythian
	I want to deploy a topology to the storm local cluster
	So that I can test topologies

Scenario : Access to local cluster
	Given I"m on the root page
	When I click on the navigation link "Clusters"
	Then I should see a navigation link to the local cluster page

Scenario : Deploy a topology
	Given an empty topology named "Topology 1"
	And I"m on the local cluster page
	When I click on the deploy topology button
	And I select the "Topology 1"
	Then I should see the "Topology 1" running
	
Scenario : Stop a topology
	Given a running topology "Topology 1"
	And I"m on the local cluster page
	When I click on the stop button of "Topology 1"
	Then I should see the "Topology 1" stopping

Scenario : Display topology state
	Given a stopped topology "Topology 1"
	And a running topology "Topology 2"
	When I visit the local cluster page
	Then I should see the "Topology 2" running
	And I should see the "Topology 1" stopped