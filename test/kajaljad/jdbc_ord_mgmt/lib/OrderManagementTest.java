package kajaljad.jdbc_ord_mgmt.lib;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

public class OrderManagementTest {
    String url = "org.mariadb.jdbc.Driver";
    String databasePath = "jdbc:mysql://localhost:3306";
    Statement statement = null;
    Connection connection = null;
    ResultSet resultSet;

    @Before
    public void setUp() throws Exception {
        try {
            Class.forName(url);
            connection = DriverManager.getConnection(databasePath,"kajal","password");
            statement = connection.createStatement();
            String query = "CREATE SCHEMA OrderManagement";
            statement.execute(query);
            System.out.println("Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void abc() {
        System.out.println("Schema Created");
    }

    @After
    public void tearDown() throws Exception {
        try {
            String query = "DROP SCHEMA OrderManagement";
            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
