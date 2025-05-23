
Feature: All Valid Feature File


  Background:
    @def all-sql = :FILE(/db.sql, 0)
    @def name-sql = :FILE(/db.sql, 1)
    @def get-counter = :FILE(/example-request.json, 1)
    @def increment-counter = :FILE(/example-request.json, 2)

  Scenario: All Proxy Container No Before Each


    Step: No Condition No Log

      Given:
        @service service-a
        @service service-b
        @service service-c
        @link sA-sC | service-a | service-c


    Step: With Condition No Logs

      Given:
        @service service-a
        @service service-b
        @service service-c
        @link sA-sC | service-a | service-c

      When:
        @after [service-a, service-b, service-c, sA-sC] | 5
        @delay [service-a, service-b, service-c, sA-sC] | 10
        @timeout [service-a, service-b, service-c, sA-sC] | 15
        @dropout [service-a, service-b, service-c, sA-sC] | 0.5

    Step: With Condition With Logs

      Given:
        @service service-a
        @service service-b
        @service service-c
        @link sA-sC | service-a | service-c

      When:
        @after [service-a, service-b, service-c, sA-sC] | 5
        @delay [service-a, service-b, service-c, sA-sC] | 10
        @timeout [service-a, service-b, service-c, sA-sC] | 15
        @dropout [service-a, service-b, service-c, sA-sC] | 0.5

      Then:
        LogAll: [service-a, service-b, service-c, sA-sC]

  Scenario: All Proxy Container With Before Each A

    BeforeEach:
      Given:
        @service service-a
        @service service-b
        @service service-c
        @link sA-sC | service-a | service-c

    Step: No Condition No Log

    Step: With Condition No Logs

      When:
        @after [service-a, service-b, service-c, sA-sC] | 5
        @delay [service-a, service-b, service-c, sA-sC] | 10
        @timeout [service-a, service-b, service-c, sA-sC] | 15
        @dropout [service-a, service-b, service-c, sA-sC] | 0.5

    Step: With Condition With Logs

      When:
        @after [service-a, service-b, service-c, sA-sC] | 5
        @delay [service-a, service-b, service-c, sA-sC] | 10
        @timeout [service-a, service-b, service-c, sA-sC] | 15
        @dropout [service-a, service-b, service-c, sA-sC] | 0.5

      Then:
        LogAll: [service-a, service-b, service-c, sA-sC]

  Scenario: All Proxy Container With Before Each B

    BeforeEach:
      Given:
        @service service-a
        @service service-b
        @service service-c
        @link sA-sC | service-a | service-c

      When:
        @after [service-a, service-b, service-c, sA-sC] | 5
        @delay [service-a, service-b, service-c, sA-sC] | 10
        @timeout [service-a, service-b, service-c, sA-sC] | 15
        @dropout [service-a, service-b, service-c, sA-sC] | 0.5

    Step: With Condition No Logs

    Step: With Condition With Logs

      Then:
        LogAll: [service-a, service-b, service-c, sA-sC]

  Scenario: All Proxy Container With Before Each C

    BeforeEach:
      Given:
        @service service-a
        @service service-b
        @service service-c
        @link sA-sC | service-a | service-c

      When:
        @after [service-a, service-b, service-c, sA-sC] | 5
        @delay [service-a, service-b, service-c, sA-sC] | 10
        @timeout [service-a, service-b, service-c, sA-sC] | 15
        @dropout [service-a, service-b, service-c, sA-sC] | 0.5

      Then:
        LogAll: [service-a, service-b, service-c, sA-sC]

    Step: With Condition With Logs

  Scenario: All Traffics No BeforeEach

    Step: No Condition No Log

      Given:
        @passive @request passive-incr-counter | service-c | $increment-counter
        @passive @sql passive-db | db | testdb | $all-sql
        @request get-counter | service-c | $get-counter
        @sql get-table | db | testdb | $all-sql

    Step: With Condition No Log

      Given:
        @passive @request passive-incr-counter | service-c | $increment-counter
        @passive @sql passive-db | db | testdb | $all-sql
        @request get-counter | service-c | $get-counter
        @sql get-table | db | testdb | $all-sql

      When:
        @times [passive-incr-counter, passive-db] | 20
        @retry [get-counter, get-table] | 30

        @dropout [passive-incr-counter, passive-db, get-counter, get-table] | 0.05
        @delay [passive-incr-counter, passive-db, get-counter, get-table] | 0.5
        @after [passive-incr-counter, passive-db, get-counter, get-table] | 5
        @timeout [passive-incr-counter, passive-db, get-counter, get-table] | 25

    Step: With Condition No Log

      Given:
        @passive @request passive-incr-counter | service-c | $increment-counter
        @passive @sql passive-db | db | testdb | $all-sql
        @request get-counter | service-c | $get-counter
        @sql get-table | db | testdb | $all-sql

      When:
        @times [passive-incr-counter, passive-db] | 20
        @retry [get-counter, get-table] | 30

        @dropout [passive-incr-counter, passive-db, get-counter, get-table] | 0.05
        @delay [passive-incr-counter, passive-db, get-counter, get-table] | 0.5
        @after [passive-incr-counter, passive-db, get-counter, get-table] | 5
        @timeout [passive-incr-counter, passive-db, get-counter, get-table] | 25

      Then:
        LogAll: [passive-incr-counter, passive-db, get-counter, get-table]

  Scenario: All Traffics With BeforeEach A

    BeforeEach:
      Given:
        @passive @request passive-incr-counter | service-c | $increment-counter
        @passive @sql passive-db | db | testdb | $all-sql
        @request get-counter | service-c | $get-counter
        @sql get-table | db | testdb | $all-sql

    Step: No Condition No Log

    Step: With Condition No Log

      When:
        @times [passive-incr-counter, passive-db] | 20
        @retry [get-counter, get-table] | 30

        @dropout [passive-incr-counter, passive-db, get-counter, get-table] | 0.05
        @delay [passive-incr-counter, passive-db, get-counter, get-table] | 0.5
        @after [passive-incr-counter, passive-db, get-counter, get-table] | 5
        @timeout [passive-incr-counter, passive-db, get-counter, get-table] | 25

    Step: With Condition No Log

      When:
        @times [passive-incr-counter, passive-db] | 20
        @retry [get-counter, get-table] | 30

        @dropout [passive-incr-counter, passive-db, get-counter, get-table] | 0.05
        @delay [passive-incr-counter, passive-db, get-counter, get-table] | 0.5
        @after [passive-incr-counter, passive-db, get-counter, get-table] | 5
        @timeout [passive-incr-counter, passive-db, get-counter, get-table] | 25

      Then:
        LogAll: [passive-incr-counter, passive-db, get-counter, get-table]

  Scenario: All Traffics With BeforeEach B

  BeforeEach:
    Given:
    @passive @request passive-incr-counter | service-c | $increment-counter
    @passive @sql passive-db | db | testdb | $all-sql
    @request get-counter | service-c | $get-counter
    @sql get-table | db | testdb | $all-sql

    When:
      @times [passive-incr-counter, passive-db] | 20
      @retry [get-counter, get-table] | 30

      @dropout [passive-incr-counter, passive-db, get-counter, get-table] | 0.05
      @delay [passive-incr-counter, passive-db, get-counter, get-table] | 0.5
      @after [passive-incr-counter, passive-db, get-counter, get-table] | 5
      @timeout [passive-incr-counter, passive-db, get-counter, get-table] | 25

  Step: With Condition No Log

  Step: With Condition No Log

  Then:
    LogAll: [passive-incr-counter, passive-db, get-counter, get-table]

  Scenario: All Traffics With BeforeEach B

    BeforeEach:
      Given:
        @passive @request passive-incr-counter | service-c | $increment-counter
        @passive @sql passive-db | db | testdb | $all-sql
        @request get-counter | service-c | $get-counter
        @sql get-table | db | testdb | $all-sql

      When:
        @times [passive-incr-counter, passive-db] | 20
        @retry [get-counter, get-table] | 30

        @dropout [passive-incr-counter, passive-db, get-counter, get-table] | 0.05
        @delay [passive-incr-counter, passive-db, get-counter, get-table] | 0.5
        @after [passive-incr-counter, passive-db, get-counter, get-table] | 5
        @timeout [passive-incr-counter, passive-db, get-counter, get-table] | 25

      Then:
        LogAll: [passive-incr-counter, passive-db, get-counter, get-table]

    Step: With Condition No Log

