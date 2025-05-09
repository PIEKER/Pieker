
Feature: Example System Running Example Thesis

  """
    Testkonfiguration fuer ein Beispiel am exampleSystem
  """

  Background:
    @def all-sql = :FILE(/db.sql, 0)
    @def name-sql = :FILE(/db.sql, 1)
    @def get-counter = :FILE(/example-request.json, 1)
    @def increment-counter = :FILE(/example-request.json, 2)

  Scenario: RunEx-A

    BeforeEach:
      Given:
        @service service-c
        @database db

        @passive @request passive-get-counter | service-c | $get-counter
        @passive @request passive-incr-counter | service-c | $increment-counter
        @passive @sql passive-db | db | $all-sql

      When:
            @delay [service-c] | 2
            @delay db | 0.3

            @times [passive-get-counter, passive-incr-counter, passive-db]| 20

      Then:
        LogAll: [service-c, passive-get-counter, passive-db, passive-incr-counter]

    Step: NoDbDelay

      When:
        @delay db | 0

    Step: ProxyTimeout

      When:
        @after service-c | 7
        @times passive-db | 1