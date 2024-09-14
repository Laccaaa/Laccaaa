package cr30;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionHelper {

    private Connection connection;
    private static final ConnectionHelper connectionHelper = new ConnectionHelper();

    public static ConnectionHelper getConnectionHelper() {
        return connectionHelper;
    }

    public Connection getConnection () {
        if (connection ==  null) {
            try {
                final String URL = "jdbc:postgresql://localhost:5432/Hotel";
                final String USER = "postgres";
                final String PASSWORD = "cantikitu5";
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection established");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } 
        return connection;

    }
}