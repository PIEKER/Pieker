Feature:

  Background:
    @def postValidData = :FILE(./request.json, 0)
    @def postValidDataMaxInt = :FILE(./request.json, 1)
    @def postInvalidData = :FILE(./request.json, 2)
    @def callRisk = :FILE(./request.json, 3)
    @def getResult = :FILE(./request.json, 4)

    @def assertTableInput = :FILE(./queries.sql, 0)
    @def assertTableRisk = :FILE(./queries.sql, 1)

    Scenario: Evaluation Scenario 3

      Step: Latency
        """ Given a valid input to the Data-Service, when a latency of 500 ms is added to the call to
            the Risk-Service, then the Web-Service can access the result of the valid input within one second. """
        Given:
          @request 1-post-valid | data-service | $postValidData
          @request 2-get-result | web-service | $getResult
          @link data-risk | data-service | risk-service
        When:
          @duration 1
          @delay data-risk | 0.5
        Then:
          Assert: Log
            Arguments: 2-get-result
            Equals: True | [{"id":1,"content":"1,2","risk":"-1"}]
              @response @exists

      Step: Dropout
        """ Given a valid input to the Data-Service, when a message dropout of 50% is injected to
            calls to the Risk-Service, then the Web-Service can still access valid data. """
        Given:
          @request 0-post-valid | data-service | $postValidData
          @request 1-get-result | web-service | $getResult
          @link data-risk | data-service | risk-service
        When:
          @times 0-post-valid | 10
          @dropout data-risk | 0.5
        Then:
            Assert: Log
              Arguments: 1-get-result
              Equals: True | 200
                @status @forall

      Step: Risk Service Unavailable
        """ Given a valid input to the Data-Service, when the Risk-Service is unavailable, then input
            is still present in the database. """
        Given:
          @sql 0-clear-input-table | db | risk-db | TRUNCATE input_data
          @request post-valid | data-service | $postValidData
          @link data-risk | data-service | risk-service
        When:
          @times post-valid | 10
          @after data-risk | 99999
        Then:
          Assert: Database
            Arguments: db | risk-db | $assertTableInput
            Equals: True | 10
              COUNT(*)
