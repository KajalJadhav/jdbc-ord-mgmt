package kajaljad.jdbc_ord_mgmt.lib;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.assertEquals;

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
            connection = DriverManager.getConnection(databasePath, "kajal", "password");
            statement = connection.createStatement();
            String query1 = "CREATE SCHEMA OrderManagement";
            statement.execute(query1);

            String query2 = "CREATE TABLE OrderManagement.Customer(customer_id varchar(20) primary key,customer_name varchar(20),address varchar(30),contact bigint)";
            statement.execute(query2);

            System.out.println("Created Successfully");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void insertDataInCustomer() throws SQLException {
        statement.execute("INSERT INTO OrderManagement.Customer VALUES(101,'Kajal','Mumbai,Maharashtra',9008952987)");
        resultSet = statement.executeQuery("SELECT * FROM OrderManagement.Customer");
        int id = 0;
        String name = "";
        String city = "";
        while (resultSet.next()) {
            id = resultSet.getInt(1);
            name = resultSet.getString(2);
            city = resultSet.getString(3);
        }
        assertEquals(101, id);
        assertEquals("Kajal", name);
        assertEquals("Mumbai,Maharashtra", city);
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
