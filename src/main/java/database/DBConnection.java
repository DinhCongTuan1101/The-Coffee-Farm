package database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {

    public static Connection getConnection() {
        try {
            // 1. Thay đổi cấu hình url cho đúng với Microsoft SQL Server (Port 1433)
            // Thêm mã hóa SSL (encrypt=true) và bỏ qua chứng chỉ (trustServerCertificate=true) để chạy local ổn định
            String url = "jdbc:sqlserver://localhost:1433;"
                    + "databaseName=the_coffee_farm_management;"
                    + "encrypt=true;"
                    + "trustServerCertificate=true;";

            // 2. Tài khoản quản trị mặc định của SQL Server là "sa" chứ không phải "root"
            String user = "sa";
            String password = "qaz@123"; // Thay bằng mật khẩu tài khoản 'sa' của bạn

            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            System.err.println("❌ Lỗi kết nối Cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
