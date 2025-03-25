Feature:

  Scenario: prototype

    @def r1 = {"method": ":RSTART( (GET|POST|PUT|PATCH|DELETE) )REND", "body": ":RSTART( ^Regular expressions are ((odd|hard|stupid), )+but cool!$ )REND", "fooFloatA" : ":FLOATV( 0.0 , 5.0 )", "fooFloatB" : ":FLOATV( 10.0 , 15.0 )", "fooIntA" : ":INTV( 0 , 5)", "fooIntB" : ":INTV( 10 ,15 )"}
    @def r2 = :FILE(/r2.json)
    @def r3 = :FILE(/db1_init.sql)

  BeforeEach:
      Given:
        @request 1-s-C-entry | s-C | $r1
        @request 2-s-C-entry | s-C | $r1
        @passive @request s-A-foo | s-A | :FILE(/r2.json, 2)
        @passive @request s-B-foo | s-B | $r2
        @passive @sql s-A-foo2 | s-A | $r3
        @link scdb | s-C | db-1

      When:
        @delay [s-A-foo, s-A-foo2, s-B-foo] | :FLOATV( 0.0 , 5.0 )
        @times [s-A-foo, s-A-foo2, s-B-foo] | :INTV(500,1000)
        @timeout [scdb] | 60

      Then:
        LogAll: [s-A-foo, s-A-foo2, s-B-foo]
        Assert:
          Traffic:
            Identifier: s-A-foo
              Bool: True | < 10
                @time
              Equals: 200
                @status
              Null: false
                @content

    Step: test property x
        Given:
          @request 3-s-C-entry | s-C | $r1
          @request 4-s-C-entry | s-C | $r1

        When:
          @after [s-A-foo, s-A-foo2, s-B-foo] | :INTV(5,10)
          @delay [s-A-foo, s-A-foo2, s-B-foo] | :FLOATV( 0.0 , 5.0 )
          @times [s-A-foo, s-A-foo2, s-B-foo] | :INTV(500,1000)
          @timeout [scdb] | 30

        Then:
          LogAll: scdb
          Assert:
            Traffic:
              Identifier: s-A-foo
                Bool: True | < 10
                  @time
                Equals: 200
                  @status
                Null: false
                  @content

    Step: test throughput B
      Given:
        @request 3-s-C-entry | s-C | $r1
        @url sa-scoped | s-a | [/api/v1/foo1, /api/v1/foo2]
        @service s-B

      When:
        @times 3-s-C-entry | 1000
        @delay s-B | 2
        @timeout scdb | 90
        @delay scdb | 5

      Then:
        Assert:
          Traffic:
            Identifier: s-A-foo
              Bool: True | < 10
                @time
              Equals: 200
                @status
              Null: false
                @content