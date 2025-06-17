package pieker.dsl.model.assertions;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
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
    static final String DB_SERVER = "fooServer";
    static final String DB = "test";
    static final String TABLE = "fooTable";
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");
    static String jdbcUrl = "";

    @BeforeAll
    static void startContainer(){
        postgres.start();
        jdbcUrl = "jdbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort();
        String query = "CREATE TABLE " + TABLE + " (" +
                "PersonID int, " +
                "LastName varchar(255), " +
                "FirstName varchar(255), " +
                "Address varchar(255), " +
                "City varchar(255));";
        String result = send(query);
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
        DatabaseAssert valid = getDatabaseAssert();
        List<Evaluation> evaluationList = valid.getEvaluation();

        evaluationList.forEach(evaluation -> assertTrue(evaluation.isSuccess()));

        DatabaseAssert invalid;
        //Bool: SQL Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addBoolAssertion("true", " == 3", "COUNT(LastName) City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate();
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));

        //Bool: Expression Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addBoolAssertion("true", " false 3", "COUNT(LastName) | City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate();
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));

        //Equal: SQL Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addEqualsAssertion("true", "Yannick", "FirstName City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate();
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));

        //Null: SQL Syntax
        invalid = getEmptyDatabaseAssert();
        invalid.addNullAssertion("true", "FirstName City = 'Kiel'");
        send(invalid.assertableTableQuery + invalid.tableSelect);
        invalid.evaluate();
        evaluationList = invalid.getEvaluation();
        evaluationList.forEach(evaluation -> assertFalse(evaluation.isSuccess()));
    }

    private @NotNull DatabaseAssert getDatabaseAssert() {
        DatabaseAssert valid = getValidTestObject();
        valid.setConnectionParam(new JSONObject("""
                {
                    "targetUrlEnv": "<?>",
                    "usernameEnv": "<?>",
                    "passwordEnv": "<?>"
                }
                """.replaceFirst("<\\?>", jdbcUrl)
                    .replaceFirst("<\\?>", postgres.getUsername())
                .replaceFirst("<\\?>", postgres.getPassword()))
        );

        // run evaluation
        valid.evaluate();
        return valid;
    }

    DatabaseAssert getEmptyDatabaseAssert(){
        DatabaseAssert empty = new DatabaseAssert(DB_SERVER + "|" + DB + "|" + "SELECT * FROM " + TABLE);
        empty.setConnectionParam(new JSONObject("""
                {
                    "targetUrlEnv": "<?>",
                    "usernameEnv": "<?>",
                    "passwordEnv": "<?>"
                }
                """.replaceFirst("<\\?>", jdbcUrl)
                .replaceFirst("<\\?>", postgres.getUsername())
                .replaceFirst("<\\?>", postgres.getPassword()))
        );
        return empty;
    }

    DatabaseAssert getValidTestObject(){

        DatabaseAssert databaseAssert = new DatabaseAssert(DB_SERVER + "|" + DB + "|" + "SELECT * FROM " + TABLE);
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
        return Sql.send(DB_SERVER, jdbcUrl + "/" + DB, postgres.getUsername(), postgres.getPassword(), query);
    }
}