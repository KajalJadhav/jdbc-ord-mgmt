package kajaljad.jdbc_ord_mgmt.lib;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

import static junit.framework.Assert.assertEquals;

public class OrderManagementTest {
    static Connection connection = null;
    static Statement statement = null;

    @BeforeClass
    public static void createConnection() throws Exception {

        String databasePath = "jdbc:mysql://localhost";
        String user = "kajal";
        String password = "password";
        Class.forName("org.mariadb.jdbc.Driver");
        connection = DriverManager.getConnection(databasePath, user, password);
        statement = connection.createStatement();

        String sql = "CREATE SCHEMA OrderManagement";
        assertEquals(1, statement.executeUpdate(sql));

        try {
            String createProductTable = "CREATE TABLE OrderManagement.Product (\n" +
                    "\tproduct_id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "\tproduct_name VARCHAR(30),\n" +
                    "\tunit_price FLOAT\n" +
                    ");";
            assertEquals(0, statement.executeUpdate(createProductTable));

            String createCustomerTable = "CREATE TABLE OrderManagement.Customer (\n" +
                    "\tcustomer_id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "\tcustomer_name VARCHAR(30),\n" +
                    "\taddress VARCHAR(30),\n" +
                    "\tcity VARCHAR(30),\n" +
                    "\tstate VARCHAR(30),\n" +
                    "\tcontact BIGINT\n" +
                    ")";
            assertEquals(0, statement.executeUpdate(createCustomerTable));

            String createOrderInfoTable = "CREATE TABLE OrderManagement.OrderInfo (\n" +
                    "\torder_id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "\tcustomer_id INT,\n" +
                    "\tdate_of_order DATETIME,\n" +
                    "\tdelivery_date DATETIME \n" +
                    ")";
            assertEquals(0, statement.executeUpdate(createOrderInfoTable));

            String createOrderItemTable = "CREATE TABLE OrderManagement.OrderItems (\n" +
                    "\torder_item_id INT PRIMARY KEY AUTO_INCREMENT,\n" +
                    "\torder_id INT,\n" +
                    "\tproduct_id INT,\n" +
                    "\tquantity INT,\n" +
                    "\titem_price FLOAT\n" +
                    ")";
            assertEquals(0, statement.executeUpdate(createOrderItemTable));


            String addForeignKeyToOrderInfo = "ALTER TABLE OrderManagement.OrderInfo \n" +
                    "\tADD CONSTRAINT cust_id_fk FOREIGN KEY(customer_id)\n" +
                    "\tREFERENCES Customer(customer_id);\n";
            assertEquals(0, statement.executeUpdate(addForeignKeyToOrderInfo));

            String addForeignKeyToOrderItems = "ALTER TABLE OrderManagement.OrderItems \n" +
                    "\tADD CONSTRAINT OrderItems_orderId_fk FOREIGN KEY(order_id)\n" +
                    "\tREFERENCES OrderInfo(order_id)\n";
            assertEquals(0, statement.executeUpdate(addForeignKeyToOrderItems));

            String addForeignKeyToOrderItemsForProduct = "ALTER TABLE OrderManagement.OrderItems \n" +
                    "\tADD CONSTRAINT prod_id_fk FOREIGN KEY(product_id)\n" +
                    "\tREFERENCES Product(product_id);\n";
            assertEquals(0, statement.executeUpdate(addForeignKeyToOrderItemsForProduct));

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void forInsertingSingleRecordInProductTable() throws Exception {
        String firstRecord = "INSERT INTO OrderManagement.Product(product_name,unit_price) VALUES('Pen',10)";
        assertEquals(1, statement.executeUpdate(firstRecord));
    }

    @Test
    public void forInsertingMultipleRecordsIntoProductTable() throws Exception {
        String secondRecord = "INSERT INTO OrderManagement.Product(product_name,unit_price) VALUES('Notebook',30)";
        assertEquals(1, statement.executeUpdate(secondRecord));

        String thirdRecord = "INSERT INTO OrderManagement.Product(product_name,unit_price) VALUES('Pencil',3)";
        assertEquals(1, statement.executeUpdate(thirdRecord));

        String forthRecord = "INSERT INTO OrderManagement.Product(product_name,unit_price) VALUES('Water Colors',25)";
        assertEquals(1, statement.executeUpdate(forthRecord));
    }

    @Test
    public void forSelectingRecordsFromProductTable() throws Exception {
        String query = "SELECT * from OrderManagement.Product;";
        ResultSet rs = statement.executeQuery(query);

        String[] productName = {"Pen", "Notebook", "Pencil", "Water Colors"};
        int[] unitPrice = {10, 30, 3, 25};

        while (rs.next()) {
            assertEquals(productName[rs.getRow() - 1], rs.getString(2));
            assertEquals(unitPrice[rs.getRow() - 1], rs.getInt(3));
        }
    }

    @Test
    public void forInsertingRecordInCustomerTable() throws Exception {
        String query = "INSERT INTO OrderManagement.Customer(customer_name ,address ,city ,state ,contact ) VALUES('Kajal','Fort','Mumbai','Maharashtra',9008952987);";
        assertEquals(1, statement.executeUpdate(query));
    }

    @Test
    public void forInsertingRecordInOrderInfoTable() throws Exception {
        String query = "INSERT INTO OrderManagement.OrderInfo(customer_id,date_of_order,delivery_date) VALUES(1,now(),now())";
        assertEquals(1, statement.executeUpdate(query));
    }

    @Test
    public void forInsertingRecordInOrderItemsUsingSelectSubquery() throws Exception {
        String query = "INSERT INTO OrderManagement.OrderItems(order_id,product_id,quantity,item_price)VALUES((SELECT MAX(order_id) from OrderManagement.OrderInfo where customer_id =1),(SELECT product_id from OrderManagement.Product where product_name='Pen'),10,(SELECT 10 * unit_price from OrderManagement.Product where product_name='Pen'))";
        assertEquals(1, statement.executeUpdate(query));
    }

    @Test
    public void forGeneratingPaymentUsingSelectQuery() throws Exception {
        ResultSet resultSet;
        String gettingPayment = "SELECT OrderManagement.OrderItems.order_id,OrderManagement.Customer.customer_name,OrderManagement.OrderItems.item_price " +
                "FROM OrderManagement.OrderItems INNER JOIN OrderManagement.OrderInfo INNER JOIN OrderManagement.Customer " +
                "ON OrderManagement.OrderItems.order_id = OrderManagement.OrderInfo.order_id AND OrderManagement.OrderInfo.customer_id = OrderManagement.Customer.customer_id WHERE OrderManagement.OrderItems.order_id IN (SELECT MAX(order_id) from OrderManagement.OrderItems)";
        resultSet = statement.executeQuery(gettingPayment);

        while (resultSet.next()) {
            assertEquals(1, resultSet.getInt(1));
            assertEquals("Kajal", (resultSet.getString(2)));
            assertEquals(100, resultSet.getInt(3));
        }
    }

    @Test
    public void forDeletingRecordFromProductTable() throws Exception {
        String query = "DELETE FROM OrderManagement.Product WHERE product_name ='Pen'";
        ResultSet rs = statement.executeQuery(query);
        assertEquals(0, rs.getRow());
    }

    @AfterClass
    public static void tearDown() throws Exception {
        try {
            String query = "DROP SCHEMA OrderManagement";
            statement.executeUpdate(query);
            statement.close();
            connection.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
