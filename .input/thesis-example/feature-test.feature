Feature:
  """
  A document to test relevant features throughout the thesis-example.

    1. Sequential test of posting string-input to the data-service and expecting a risk-value in the end of the test-scenario
    2. Sequential test as presented in (1.) but with invalid inputs.
  """
  Background:
    @def postInteger = :FILE(./request.json, 0)
    @def postLong = :FILE(./request.json, 1)
    @def postInvalidA = :FILE(./request.json, 2)
    @def postInvalidB = :FILE(./request.json, 3)
    @def postInvalidC = :FILE(./request.json, 4)

  Scenario: Valid Input Post
    """
    This test-scenarios covers all successful input-possibilities for the example thesis.
    """

    @def assertTableInput = :FILE(./queries.sql, 0)
    @def assertTableRisk = :FILE(./queries.sql, 1)
    @def joinedTable = :FILE(./queries.sql, 2)

    BeforeEach:
      Given:
        @link data-risk | data-service | risk-service

      Then:
        LogAll: data-risk

        Assert: Database
        Arguments: db | risk-db | $assertTableInput
        Null: False
          content
        Bool: True | == 10
          COUNT(*)

        Assert: Database
        Arguments: db | risk-db | $joinedTable
        Bool: True | == 10
          COUNT(*)
        Equals: true | -1
          risk | content='1,2'
        Equals: true | -2
          risk | content='2147483645,2147483647'

        Assert: Log
        Arguments: data-risk
        Bool: True | < 299
          @status @forall


    Step: Post Integer
      Given:
        @request post-integer | data-service | $postInteger

      When:
        @times post-integer | 5


    Step: Post Long
      Given:
        @request post-long | data-service | $postLong

      When:
        @times post-long | 5

    Scenario: Invalid Input Post
    """
    This test-scenarios covers all invalid input-possibilities for the example thesis.
    """

      @def assertTable = :FILE(./queries.sql, 1)

      BeforeEach:
        Given:
          @link data-risk | data-service | risk-service

      Then:
        LogAll: data-risk

      Assert: Database
        Arguments: db | risk-db | $assertTable
        Bool: True | == 0
          COUNT(*)

      Assert: Database
        Arguments: db | risk-db | $assertTable
        Bool: True | == 0
          COUNT(*)

      Assert: Log
        Arguments: data-risk
        Equals: False | Stored and triggered processing
          @times @forall

    Step: Post Invalid A
      Given:
        @request post-invalid-a | data-service | $postInvalidA

      When:
        @times post-invalid-a | 5

    Step: Post Invalid B
        Given:
          @request post-invalid-b | data-service | $postInvalidB

        When:
          @times post-invalid-b | 5

    Step: Post Invalid C
        Given:
          @request post-invalid-c | data-service | $postInvalidC

        When:
          @times post-invalid-c | 5

