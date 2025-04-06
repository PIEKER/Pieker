
Feature: Example System Running Example Thesis

  """
    Testkonfiguration fuer ein Beispiel am exampleSystem
  """

  Background:
    @def post-message = :FILE(/example-request.json, 0)
    @def get-counter = :FILE(/example-request.json, 1)
    @def get-allMessages = :FILE(/example-request.json, 2)

  Scenario: RunEx-A

    BeforeEach:
      Given:
        @service service-a
        @service service-b
        @service service-c
        @database db
        @passive @request passive-get-counter | service-a | $get-counter
      When:
        @delay [service-a, service-b] | 2
        @delay db | 3

      Then:
        LogAll: [service-a, service-b, service-c]

    Step: Get Counter Passive

      Given:
        @link sA-sC | service-a | service-c

        @passive @request post-message | service-b | $post-message

      When:
        @delay sA-sC | 5

        @times post-message | 100
        @delay post-message | 0.5
      Then:
        Assert:
          Traffic:
            Identifier: post-message
            Bool: True | < 100
              @times
            Equals: True | 200
              @status
            Null: False
              @content

    Step: Get Counter Supervisor

      Given:
        @link sA-sC | service-a | service-s

        @request 1b-get-counter | service-c | $get-counter
        @request 1a-post-message | service-b | $post-message
        @sql  3-db-messages | db | SELECT COUNT(de.content) FROM DataEntity de

      When:
        @delay sA-sC | 10

        @times 1a-post-message | 5
        @delay 1a-post-message | 0.5

      Then:
        LogAll: passive-get-counter
        Assert:
          Traffic:
            Identifier: 1b-get-counter
              Bool: True | < 100
                @times
              Equals: True| 200
                @status
              Null: False
                @content

          Database:
            Identifier: db
            Table: SELECT * FROM DataEntity de
              Null: False
                de.id |
              Equals: True | I am an important message!
                de.content | de.id = 1
              Bool: True | < 100
                COUNT(de.content)|
