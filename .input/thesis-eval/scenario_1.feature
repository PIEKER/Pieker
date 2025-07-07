Feature:

    Background:
        @def postValidData = :FILE(./request.json, 0)
        @def postValidDataMaxInt = :FILE(./request.json, 1)
        @def postInvalidData = :FILE(./request.json, 2)
        @def callRisk = :FILE(./request.json, 3)
        @def getResult = :FILE(./request.json, 4)

        @def assertTableInput = :FILE(./queries.sql, 0)
        @def assertTableRisk = :FILE(./queries.sql, 1)

    Scenario: Evaluation Scenario 1

        Step: Invalid Input No Result
            """ Given an invalid input to the Data-Service, then the Web-Service returns no result. """
            Given:
                @request 1-post-invalid | data-service | $postInvalidData
                @request 2-web-result | web-service | $getResult
            When:
                @after 2-web-result | 2
            Then:
                Assert: Log
                    Arguments: 2-web-result
                    Equals: True |  [{"id":1,"content":",2","risk":null}]
                        @response @exists

        Step: Valid Input Valid Result
            """ Given a valid input to the Data-Service, then the Web-Service returns a valid result. """
            Given:
                @sql 0-clear-input-table | db | risk-db | TRUNCATE input_data
                @request 1-post-valid | data-service | $postValidData
                @request 2-web-result | web-service | $getResult
            When:
                @after 2-web-result | 2
            Then:
                Assert: Log
                    Arguments: 2-web-result
                    Equals: True | [{"id":2,"content":"1,2","risk":"-1"}]
                        @response @exists


        Step: Valid Input Risk Call
            """ Given a valid input to the Data-Service, then the Risk-Service is activated via a call. """
            Given:
                @request post-valid | data-service | $postValidData
                @link data-risk | data-service | risk-service
            Then:
                Assert: Log
                    Arguments: data-risk
                    Null: False
                        @status @exists


        Step: Call Risk Read DB
            """ Given a call to the Risk-Service, then the Risk-Service reads from the database. """
            Given:
                @sql 0-clear-tables | db | risk-db | TRUNCATE input_data, risk_data
                @sql 0-input-data | db | risk-db | INSERT INTO input_data (id, content) VALUES(1, '1,2')
                @request 1-call-risk | risk-service | $callRisk
            Then:
                Assert: Log
                    Arguments: 1-call-risk
                    Equals: True | 200
                        @status @forall
                Assert: Database
                    Arguments: db | risk-db | $assertTableRisk
                    Bool: True | == 1
                        COUNT(*)


        Step: Request Web Request DB
            """ Given a request to the Web-Service, then the Web-Service makes a request to the database. """
            Given:
                @request web-request | web-service | $getResult
            Then:
                Assert: Log
                    Arguments: web-request
                    Equals: True | 200
                        @status @forall

        Step: Input Data Request DB
            """ Given an input to the Data-Service, then the Data-Service makes a request to the database. """
            Given:
              @sql 1-clear-input-table | db | risk-db | TRUNCATE input_data
              @request 2-post-valid | data-service | $postValidData
            Then:
              Assert: Log
                Arguments: 2-post-valid
                Equals: True | 200
                  @status @forall
              Assert: Database
                Arguments: db | risk-db | $assertTableInput
                Bool: True | == 1
                  COUNT(*)

        Step: Input Risk No Result
            """ Given an invalid input to the Risk-Service, then the Risk-Service returns no result. """
            Given:
              @sql 0-clear-tables | db | risk-db | TRUNCATE input_data, risk_data
              @request 1-post-invalid | data-service | $postInvalidData
            Then:
                Assert: Database
                    Arguments: db | risk-db | $assertTableRisk
                    Bool: True | == 0
                      COUNT(*)