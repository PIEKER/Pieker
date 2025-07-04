Feature
Feature: Sandbox Tutorial

"""
  Configuration of Test Scenarios based on the PIEKER Tutorial
"""

  Scenario: Tutorial-A
  
      Step: Ping Test
      Given:
        @service service-a
        @service service-b
        @service service-c
        @service db
        @request service-a-test | service-a | $ping
        @request service-b-test | service-b | $ping
        @request service-c-test | service-c | $ping
        @request db-test | db | $ping
        
      When:
        @def ping = {"url": "/", "method": "GET"}
        @after [service-a-test, db-test] | 1
        @after [service-b-test, service-c-test] | 5
        
      Then:
        LogAll: [service-a, service-b, service-c, service-a-test]
        
      Step: Increment Counter Test        
      Given:
        @service service-a
        @service service-b
        @link network-a-b | service-a | service-b
        
      Then:
        LogAll: [network-a-b]
        


        
        
      

