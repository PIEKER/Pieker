Feature: Container Permutation
  """
  This file contains a list of scenarios testing different coupling possibilities for injected components.
  """

  Background:
    @def postInteger = :FILE(./request.json, 0)
    @def postMaxInt = :FILE(./request.json, 1)
    @def assertTableInput = :FILE(./queries.sql, 0)
    @def assertTableRisk = :FILE(./queries.sql, 1)
    @def calcRisk = :FILE(./request.json, 5)
    @def webResult = :FILE(./request.json, 6)
    @def joinedTable = :FILE(./queries.sql, 2)

  Scenario: Proxy Type Check

    BeforeEach:
      Given:
        @request post-integer | data-service | $postInteger

      When:
        @times post-integer | 5

      Then:
        Assert: Database
          Arguments: db | risk-db | $assertTableInput
          Bool: True | == 25
            COUNT(*)
          Null: False
            content

        Assert: Database
          Arguments: db | risk-db | $assertTableRisk
          Equals: true | -1
            risk

    Step: ServiceProxyTest

      Given:
        @service [data-service, risk-service, web-service]

      Then:
        LogAll: [data-service, risk-service, web-service]

    Step: DatabaseProxyTest

      Given:
        @database db

    Step: ServiceXDatabaseProxyTest

      Given:
        @service [data-service, risk-service, web-service]
        @database db

      Then:
        LogAll: [data-service, risk-service, web-service]

    Step: LinkProxyTest

      Given:
        @link data-risk | data-service | risk-service
        @link data-db | data-service | db
        @link risk-db | risk-service | db
        @link web-db | web-service | db

      Then:
        Assert: Log
          Arguments: data-risk
          Bool: True | < 299
          @status @forall

    Step: LinkXServiceXDatabaseProxyTest

      Given:
        @service [data-service, risk-service, web-service]
        @database db

        @link data-risk | data-service | risk-service
        @link data-db | data-service | db
        @link risk-db | risk-service | db
        @link web-db | web-service | db

      Then:
        LogAll: [data-service, risk-service, web-service]

        Assert: Log
          Arguments: data-risk
          Bool: True | < 299
          @status @forall

  Scenario: Traffic Type Check

    BeforeEach:
      Given:
        @request post-integer | data-service | $postInteger

      When:
        @times post-integer | 5

      Then:
        Assert: Database
          Arguments: db | risk-db | $joinedTable
          Null: False
            content

        Assert: Database
          Arguments: db | risk-db | $joinedTable
          Equals: true | -1
            risk | content='1,2'
          Equals: true | -2
            risk | content='2147483645,2147483647'

    Step: Traffic On Service
      Given:
        @passive @request passive-data-traffic | data-service | $postMaxInt
        @passive @request passive-risk-traffic | risk-service | $calcRisk
        @passive @request passive-web-traffic | web-service | $webResult

      When:
        @duration 10
        @retry [passive-data-traffic, passive-risk-traffic, passive-web-traffic] | 10
        @delay [passive-data-traffic, passive-risk-traffic, passive-web-traffic] | 0.01
        @timeout passive-data-traffic | 4
        @after passive-risk-traffic | 5
        @after passive-web-traffic | 8

      Then:
        LogAll: [passive-data-traffic, passive-risk-traffic, passive-web-traffic]
        Assert: Database
          Arguments: db | risk-db | $assertTableRisk
          Bool: True | > 30
            COUNT(*) | risk='-2'

    Step: Traffic On Database
      Given:
        @passive @sql passive-query | db | risk-db | $joinedTable

      When:
        @duration 10
        @retry passive-query | 10
        @delay passive-query | 0.01

      Then:
        LogAll: passive-query


    Step: Traffic On Both
      Given:
        @passive @sql passive-query | db | risk-db | $joinedTable
        @passive @request passive-data-traffic | data-service | $postMaxInt
        @passive @request passive-risk-traffic | risk-service | $calcRisk
        @passive @request passive-web-traffic | web-service | $webResult

      When:
        @duration 10
        @retry [passive-data-traffic, passive-risk-traffic, passive-web-traffic, passive-query] | 10
        @delay [passive-data-traffic, passive-risk-traffic, passive-web-traffic, passive-query] | 0.01
        @timeout passive-data-traffic | 4
        @after passive-risk-traffic | 5
        @after passive-web-traffic | 8

      Then:
        LogAll: [passive-data-traffic, passive-risk-traffic, passive-web-traffic, passive-query]
        Assert: Database
          Arguments: db | risk-db | $assertTableRisk
          Bool: True | > 30
          COUNT(*) | risk='-2'

    Scenario: All in All

      Step: Traffic Proxy Test
        Given:
          @service [data-service, risk-service, web-service]
          @database db

          @link data-risk | data-service | risk-service
          @link data-db | data-service | db
          @link risk-db | risk-service | db
          @link web-db | web-service | db

          @passive @request passive-data-traffic | data-service | $postMaxInt
          @passive @request passive-risk-traffic | risk-service | $calcRisk
          @passive @request passive-web-traffic | web-service | $webResult

          @request post-integer | data-service | $postInteger

        When:
          @duration 10
          @times post-integer | 5
          @retry [passive-data-traffic, passive-risk-traffic, passive-web-traffic] | 10
          @delay [passive-data-traffic, passive-risk-traffic, passive-web-traffic] | 0.01
          @timeout passive-data-traffic | 4
          @after passive-risk-traffic | 5
          @after passive-web-traffic | 8

        Then:
          LogAll: [data-service, risk-service, web-service, passive-data-traffic, passive-risk-traffic, passive-web-traffic]

          Assert: Log
            Arguments: data-risk
            Bool: True | < 299
              @status @forall

          Assert: Database
            Arguments: db | risk-db | $joinedTable
            Null: False
              content

          Assert: Database
            Arguments: db | risk-db | $joinedTable
            Equals: true | -1
              risk | content='1,2'
            Equals: true | -2
              risk | content='2147483645,2147483647'