import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    // IMPORTANT: Update these with your MySQL details
    private static final String url = "jdbc:mysql://localhost:3306/ATM_DB";
    private static final String user = "root";
    private static final String password = "88023@";

    public static Connection getConnection() throws SQLException {
        try {
            // Register the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found.");
        }
        return DriverManager.getConnection(url, user, password);
    }
    
    public static void main(String[] args) {
        // This is a test method to check the database connection
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Success! Connection to the ATM_DB established.");
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
            System.out.println("SQLState: " + e.getSQLState());
            System.out.println("VendorError: " + e.getErrorCode());
        }
    }
}