Feature: Deploy topology to a local cluster
	As a user of stormy-pythian
	I want to deploy a topology to the storm local cluster
	So that I can test topologies

Scenario: Access to local cluster
	Given I'm on the root page
	When I click on the navigation link "Clusters"
	Then I should see a navigation link to the local cluster page

Scenario: Deploy a topology
	Given an empty topology named "Topology 1"
	And I'm on the local cluster page
	When I click on the choose topology to deploy button
	And I select the "Topology 1" topology
	Then I should see the "Topology 1" deployed
	
Scenario: Undeploy a topology
	Given a deployed topology "Topology 1"
	And I'm on the local cluster page
	When I click on the undeploy button of "Topology 1"
	Then I should see the "Topology 1" undeploying

Scenario: Display topology state
	Given an undeployed topology "Topology 1"
	And a deployed topology "Topology 2"
	When I visit the local cluster page
	Then I should see the "Topology 2" deployed
	And I should not see the "Topology 1" deploy state