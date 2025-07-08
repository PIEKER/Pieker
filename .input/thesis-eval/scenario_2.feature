Feature:

  Background:
    @def postValidData = :FILE(./request.json, 0)
    @def postValidDataMaxInt = :FILE(./request.json, 1)
    @def getResult = :FILE(./request.json, 4)

    @def assertTableRisk = :FILE(./queries.sql, 1)

  Scenario: Evaluation Scenario 2

      BeforeEach:
        Given:
          @sql 0-clear-tables | db | risk-db | TRUNCATE input_data, risk_data
          @passive @request traffic-valid-data | data-service | $postValidDataMaxInt
          @request 1-post-valid-data | data-service | $postValidData
          @request 2-get-result | web-service | $getResult
        When:
          @duration 5
          @after 1-post-valid-data | 4
          @retry traffic-valid-data | 4


      Step: Low Load
          """ Given a valid input to the Data-Service, when a load of ten inputs per second is present,
              then the Web-Service can access the result of the valid input within one second. """
          When:
            @delay traffic-valid-data | 0.1
          Then:
            Assert: Log
              Arguments: 2-get-result
              Equals: True | 200
                @status @exists
              Assert: Database
                Arguments: db | risk-db | $assertTableRisk
                Equals: True | 1
                  COUNT(*) | risk='-1'


      Step: High Load
          """ Given a valid input to the Data-Service, when a load of 100 inputs per second is present,
              then the Web-Service can access the result of the valid input within one second. """
          When:
            @delay traffic-valid-data | 0.01
          Then:
            Assert: Log
              Arguments: 2-get-result
              Equals: True | 200
                @status @exists
            Assert: Database
              Arguments: db | risk-db | $assertTableRisk
              Equals: True | 1
                COUNT(*) | risk='-1'


      Step: Stress Test
          """ Given a valid input to the Data-Service, when a load of 1000 inputs per second is present,
              then the Data-Service receives and responds to the input within two seconds. """
          When:
            @duration 6
            @delay traffic-valid-data | 0.001
          Then:
            Assert: Log
              Arguments: 1-post-valid-data
              Equals: True | 200
                @status @forall


      Step: Throughput Test
          """ Given any input to the Data-Service, then the Data-Service can handle at least 20 inputs per second. """
          When:
            @duration 1
            @delay traffic-valid-data | 0
          Then:
            Assert: Log
              Arguments: traffic-valid-data
              Bool: True | > 50
                @times @forall