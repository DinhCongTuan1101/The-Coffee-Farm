package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            String url = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=the_coffee_farm_management;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;";

            String user = "sa";
            String password = "Qaz@123";

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println("❌ Lỗi kết nối Cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
