
Feature: Example System Running Example Thesis

  """
    Testkonfiguration fuer ein Beispiel am exampleSystem
  """

  Background:
    @def all-sql = :FILE(/db.sql, 0)
    @def name-sql = :FILE(/db.sql, 1)
    @def message = :FILE(/example-request.json, 0)
    @def get-counter = :FILE(/example-request.json, 1)
    @def increment-counter = :FILE(/example-request.json, 2)

  Scenario: RunEx-A

    BeforeEach:
      Given:

        @passive @request passive-get-counter | service-c | $get-counter
        @passive @request passive-incr-counter | service-c | $increment-counter
        @passive @request passive-message-upload | service-a | $message
        @passive @sql passive-db | db | testdb | $all-sql

      When:
        @times [passive-message-upload, passive-get-counter, passive-incr-counter, passive-db]| 20

      Then:
        LogAll: [passive-message-upload, passive-get-counter, passive-db, passive-incr-counter]

    Step: ProxyTimeout

      Then:
        Assert: Database
          Arguments: db | testdb | SELECT * FROM data_entity
          Null: False
            content
          Bool: True | > 10
            COUNT(*)