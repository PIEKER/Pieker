Feature: Example System Risk Calculation
  Background:
    @def input-entry-a = :FILE(/example-request.json, 0)
    @def input-entry-b = :FILE(/example-request.json, 1)
    @def input-entry-c = :FILE(/example-request.json, 2)

  Scenario: Experiment No Given
    BeforeEach:
      Given:
        @database DB-A
        @database DB-B
        @database DB-C

        @passive @request input-rsa | risk-service-a | $input-entry-a
        @passive @request input-rsb | risk-service-b | $input-entry-b
        @passive @request input-rsc | risk-service-c | $input-entry-c

      When:
        @times [input-rsa, input-rsb, input-rsc] | 10
        @delay [input-rsa, input-rsb, input-rsc] | 1

        @timeout [DB-A, DB-B, DB-C] | 30

    Step: Load Test A disabled input_rsa
      When:
        @times input-rsa | 0
        @timeout DB-A | 0


    Step: Load Test A disabled input_rsb
      When:
        @times input-rsb | 0
        @timeout DB-B | 0


    Step: Load Test A disabled input_rsc
      When:
        @times input-rsc | 0
        @timeout DB-C | 0

