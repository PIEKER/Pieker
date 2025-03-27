package pieker.common.connection;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

@Slf4j
class SqlTest {

    static final String DB = "fooDB";
    static final String TABLE = "fooTable";
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @BeforeAll
    static void startContainer(){
        postgres.start();
    }

    @AfterAll
    static void stopContainer(){
        postgres.stop();
    }


    @Test
    void testSend(){
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
                (3, 'Schnoor', 'Henning', 'Westring 1', 'Kiel');""";
        assertNotEquals("ERROR ON SQL", send(query));

        query = "SELECT f.LastName FROM " + TABLE + " f WHERE f.City = 'Kiel'";
        result = send(query);
        log.info(result);
        assertNotEquals("ERROR ON SQL", result);

    }

    private String send(String query){
        return Sql.send(DB, postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword(), query);
    }
}