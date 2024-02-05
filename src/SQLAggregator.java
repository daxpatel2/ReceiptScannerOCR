import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLAggregator {

    //consider using database connection pool for better performance later when we refactor

    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a new SQLAggregator with the specified database connection parameters.
     *
     * @param url      the URL of the database.
     * @param username the username for connecting to the database.
     * @param password the password for connecting to the database.
     */
    public SQLAggregator(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Establishes a database connection using the provided URL, username, and password.
     *
     * @return a Statement object associated with the established database connection for sending sql statements to the database.
     * @throws SQLException if a database access error occurs or the URL is {@code null},
     *                      or if the DriverManager cannot establish a connection,
     *                      or if the username and password are invalid.
     */
    private Statement setConnection() throws SQLException {
        Connection con = DriverManager.getConnection(url, username, password);
        return con.createStatement();
    }

    /**
     * Executes an update, delete, or insert SQL query on the database.
     *
     * @param sqlQuery the SQL query to be executed, typically for update, delete, or insert operations.
     * @return the number of rows affected by the SQL query.
     * @throws SQLException if a database access error occurs or the SQL query is invalid.
     */
    public void updateDeleteInsertQuery(String sqlQuery) throws SQLException {
        try (Statement st = setConnection()) {
            st.executeUpdate(sqlQuery);
        } catch (SQLException sqle) {
            throw new SQLException("An error occurred trying to fetch the query: ",sqle);
        }
    }

    /**
     * Executes a SELECT SQL query on the database and retrieves the value in the specified column.
     *
     * @param sqlQuery the SELECT SQL query to be executed.
     * @param columnIndex the index of the column from which to retrieve the value.
     * @return the value in the specified column of the first row returned by the query aka our result.
     * @throws SQLException if a database access error occurs,
     *                      the SQL query is invalid, or the columnIndex is out of bounds.
     */
    public String readQuery(String sqlQuery, int columnIndex) throws SQLException {
        ResultSet rs = null;
        try (Statement st = setConnection()) {
            rs = st.executeQuery(sqlQuery);
            if (rs.next()) {
                return rs.getString(columnIndex);
            } else {
                throw new SQLException("No results found in the query");
            }
        } catch (SQLException sqle) {
            throw new SQLException("An error occurred trying to fetch the query: ",sqle);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println("Error closing the ResultSet interface:" + e);
                }
            }
        }
    }

    /**
     * Retrieves a list of String values from the database based on the provided SQL query
     * and column index.
     *
     * @param sqlQuery      the SQL query to be executed.
     * @param columnIndex   the index of the column whose values will be retrieved.
     * @return a List of String values obtained from the specified column in the result set.
     * @throws SQLException if a database access error occurs or the SQL query is invalid.
     */
    public List<String> getDatabaseValues(String sqlQuery, int columnIndex) throws SQLException {
        List<String> items = new ArrayList<>();
        try (Statement st = setConnection()) {
            try (ResultSet rs = st.executeQuery(sqlQuery)) {
                while (rs.next()) {
                    items.add(rs.getString(columnIndex));
                }
            }
        } catch (SQLException sqle) {
            throw new SQLException("An error occurred trying to fetch the query: ", sqle);
        }
        return items;
    }


}
