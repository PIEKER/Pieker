package pieker.dsl.model.assertions;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import pieker.api.Evaluation;
import pieker.common.connection.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DatabaseAssertTest {

    static final String DB = "fooDB";
    static final String TABLE = "fooTable";
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeAll
    static void startContainer(){
        postgres.start();
        String query = "CREATE DATABASE " + DB + ";";
        String result = send(query);
        log.info(result);
        assertNotEquals("ERROR ON SQL", result);
        query = "CREATE TABLE " + TABLE + " (" +
                "PersonID int, " +
                "LastName varchar(255), " +
                "FirstName varchar(255), " +
                "Address varchar(255), " +
                "City varchar(255));";
        result = send(query);
        log.info(result);
        assertNotEquals("ERROR ON SQL", result);

        query = "INSERT INTO " + TABLE + " (PersonID, LastName, FirstName, Address, City)\n";
        query += """ 
                VALUES (1, 'Ohlsen', 'Simon', 'Westring 1', 'Kiel'),
                (2, 'Illmann', 'Yannick', 'Westring 1', 'Kiel'),
                (3, 'Schnoor', 'Henning', 'Westring 1', 'Kiel'),
                (4, '', 'Peter', 'Westring 1', 'Hamburg');""";
        assertNotEquals("ERROR ON SQL", send(query));
    }

    @AfterAll
    static void stopContainer(){
        postgres.stop();
    }

    @Test
    void testDatabaseAssert(){
        DatabaseAssert valid = getValidTestObject();

        // simulate supervisor step
        send(valid.assertableTableQuery + valid.tableSelect);

        // run evaluation
        valid.evaluate(new String[]{DB, postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()});
        List<Evaluation> evaluationList = valid.getEvaluation();

        evaluationList.forEach(evaluation -> assertTrue(evaluation.isSuccess()));

        DatabaseAssert invalid;
        //Bool: SQL Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addBoolAssertion("true", " == 3", "COUNT(LastName) City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate(new String[]{DB, postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()});
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));

        //Bool: Expression Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addBoolAssertion("true", " false 3", "COUNT(LastName) | City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate(new String[]{DB, postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()});
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));

        //Equal: SQL Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addEqualsAssertion("true", "Yannick", "FirstName City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate(new String[]{DB, postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()});
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));

        //Null: SQL Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addNullAssertion("true", "FirstName City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate(new String[]{DB, postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword()});
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));
    }

    DatabaseAssert getEmptyDatabaseAssert(){
        DatabaseAssert databaseAssert = new DatabaseAssert(DB);
        databaseAssert.setTableSelect("SELECT * FROM " + TABLE);
        return databaseAssert;
    }

    DatabaseAssert getValidTestObject(){

        DatabaseAssert databaseAssert = new DatabaseAssert(DB);
        databaseAssert.setTableSelect("SELECT * FROM " + TABLE);
        databaseAssert.addBoolAssertion("true", " == 4", "COUNT(LastName)");
        databaseAssert.addBoolAssertion("true", " < 2", "COUNT(LastName) | LastName = 'Ohlsen'");
        databaseAssert.addBoolAssertion("false", " > 2", "COUNT(FirstName) | FirstName = 'Yannick'");

        databaseAssert.addEqualsAssertion("true", "Westring 1","Address" );
        databaseAssert.addEqualsAssertion("true", "Simon","FirstName | LastName = 'Ohlsen'" );
        databaseAssert.addEqualsAssertion("false", "Yannick","FirstName | LastName = 'Ohlsen'" );

        databaseAssert.addNullAssertion("true", "LastName | City = 'Hamburg'");
        databaseAssert.addNullAssertion("false", "LastName | City = 'Kiel'");


        return databaseAssert;
    }

    private static String send(String query){
        return Sql.send(DB, postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword(), query);
    }
}