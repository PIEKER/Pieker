package pieker.common.connection;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class Sql {

    private Sql(){}

    public static String send(String database, String dbUrl, String dbUser, String dbPassword, String sqlQuery) {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement()) {

            boolean hasResultSet = statement.execute(sqlQuery);

            if (hasResultSet) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    return createResultString(resultSet);
                }
            } else {
                int affectedRows = statement.getUpdateCount();
                return "Query executed successfully, affected rows: " + affectedRows;
            }
        } catch (SQLException e) {
            log.error("exception occurred while sending SQL to database {}. Exception: {}", database, e.getMessage());
            return "ERROR ON SQL";
        }
    }

    private static String createResultString(ResultSet resultSet) throws SQLException {
        StringBuilder resultString = new StringBuilder();
        int columnCount = resultSet.getMetaData().getColumnCount();

        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                resultString.append(resultSet.getString(i));
                if (i < columnCount) {
                    resultString.append(", "); // Separator for columns
                }
            }
            resultString.append("|"); // New line for each row
        }
        return resultString.toString();
    }
}
