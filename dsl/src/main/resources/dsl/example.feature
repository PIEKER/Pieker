Feature:

  Background:
    """
      Here you can define global variables for the whole test run collection.
    """
    @def x = { "url": "", "method": $method,"content-type": "form-body","body": $body }
    @def method = "POST"
    @def body = "{"payload": $payload }"
    @def payload = "foo"

  Scenario: prototype
    """
      A scenario represents a single test-run containing of multiple request steps.
    """
  @def r1 = {"method": ":RSTART( (GET|POST|PUT|PATCH|DELETE) )REND", "body": ":RSTART( ^Regular expressions are ((odd|hard|stupid), )+but cool!$ )REND", "fooFloatA" : ":FLOATV( 0.0 , 5.0 )", "fooFloatB" : ":FLOATV( 10.0 , 15.0 )", "fooIntA" : ":INTV( 0 , 5)", "fooIntB" : ":INTV( 10 ,15 )"}
  @def r2 = :FILE(/r2.json)
  @def r3 = :FILE(/db1_init.sql)

  BeforeEach:
    Given:
      @request id-sa-1 | s-A | $x
      @request 1-s-C-entry | s-C | $r1
      @request 2-s-C-entry | s-C | $r1
      @passive @request s-A-foo | s-A | :FILE(/r2.json, 2)
      @passive @request s-B-foo | s-B | $r2
      @passive @sql s-A-foo2 | s-A | $r3

    When:
      @delay [s-A-foo, s-A-foo2, s-B-foo] | :FLOATV( 0.0 , 5.0 )
      @times [s-A-foo, s-A-foo2, s-B-foo] | :INTV(500,1000)

    Then:
      Assert:
        Traffic:
          Identifier: s-A-foo
            Bool: True | < 10
              @time
            Equals: true | 200
              @status
            Null: false
              @content

    Step: a-s1
        """
          A step defines the specific conditions of a single request. It contains
            - one Given section
            - an optional when section
            - one Then section
        """
        Given:
          """
            This section provides context data about the request. Keywords are:
              - 'request' -> expects JSON with data about the request specifics. Requires 'service' keyword. Can be loaded from file.
              - 'service' -> target service to send the request to . Requires 'request' keyword.
          """
          @database db-1

        Then:
          """
            This section defines assertions on specific TestComponents, meaning referenced traffic or databases. Depending on the
            TestComponent the DSL block differs, but offers a wide range of test scenarios. However, if a step can be
            used to generate Logs, that can be used for third party testing-scripts.
          """
          Assert:
            Database:
              Identifier: db-1
                Table: :FILE(/db1_init.sql)
                  Bool: True | < 10
                    MAX(column) AS maxCol | product.name == "FOO" OR product.name == "FAA"
                  Equals: true | television
                    product.category | product.name == "FOO" OR product.name == "FAA"
                  Null: false
                    product.id | product.name == "FOO" OR product.name == "FAA"
              Identifier: db-1
                Table: $r3
                  Bool: True | < 10
                    MAX(column) AS maxCol | product.name == "FOO" OR product.name == "FAA"
                  Equals: true | television
                    product.category | product.name == "FOO" OR product.name == "FAA"
                  Null: false
                    product.id | product.name == "FOO" OR product.name == "FAA"


  Step: a-s2
      Given:
        @request id-sc-1 | s-C | $x

      When:
        """
          This section defines test characteristics required in this step. Possible keywords (@) are:
            - 'def'     ->  step specific variables
            - 'times'   ->  how many times the request should be send
            - 'timeout' ->  period of time a request should be retried
            - 'dropout' ->  percentage of package loss
        """
        @timeout id-sa-1 | 30

      Then:
        LogAll: id-sa-1
        Assert:
          Traffic:
            Identifier: id-sa-1
              Bool: True | < 10
                @time
              Equals: true | 200
                @status
              Null: false
                @content

    Step: a-s3
      Given:
        @request id-sb-1 | s-B | $x

      Then:
        LogAll: id-sb-1