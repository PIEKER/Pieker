Feature: Sandbox Tutorial
  """
    Configuration of Test Scenarios based on the PIEKER Tutorial
  """

  Background:
    @def ping = {"url": "/", "method": "GET"}
    @def msg = {"url": "/message", "method": "POST", "content":  "I am an important message!"}

  Scenario: Tutorial-A

    BeforeEach:
      Given:
        @service service-a
        @service service-b
      Then:
        LogAll: [service-a, service-b]

    Step: Ping Test
      Given:
        @service service-c

        @request a-service-ping | service-a | $ping
        @request b-service-ping | service-b | $ping
        @request c-service-ping | service-c | $ping

      When:
        @after [a-service-ping, b-service-ping, c-service-ping ] | 1

      Then:
        LogAll: [service-c]

    Step: Increment Counter Test
      Given:
        @link con-sa-sc | service-a | service-c

        @request a-service-msg | service-a | $msg

      Then:
        LogAll: con-sa-sc

  Scenario: Crash
    @def counter = {"url": "/counter", "method": "GET"}

    Step: Performance Race
      Given:
        @link con-sa-sc | service-a | service-c

        @request 1-ping | service-c | $ping
        @request 2-msg | service-a | $msg
        @request 3-counter | service-c | $counter

      When:
        @timeout con-sa-sc | 30
        @times 2-msg | 14
        @delay 2-msg | 2

      Then:
        LogAll: [2-msg, 3-counter]
