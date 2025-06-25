Feature: Container Permutation
  """
  This file contains a list of scenarios testing different coupling possibilities for injected components.
  """

  Background:
    @def postInteger = :FILE(./request.json, 0)
    @def postLong = :FILE(./request.json, 1)
    @def assertTableInput = :FILE(./queries.sql, 0)
    @def assertTableRisk = :FILE(./queries.sql, 1)

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


