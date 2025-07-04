Feature
Feature: Sandbox Tutorial

"""
  Configuration of Test Scenarios based on the PIEKER Tutorial
"""

  Scenario: Tutorial-A
  
      Step: Ping Test
      Given:
        @service service-a
        
      When:
        @def ping = {"url": "/", "method": "GET"}  
        @request service-a-test | service-a | $ping        

        
        
      

