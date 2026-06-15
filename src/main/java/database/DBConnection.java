package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.User;

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

    public static User checkLoginInDB(String phone, String passwordHash) {
        String sql = "SELECT user_id, username, phone_number, email, address FROM users WHERE phone_number = ? AND password_hash = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            pstmt.setString(2, passwordHash);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setEmail(rs.getString("email"));
                    user.setAddress(rs.getString("address"));

                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void themSanPhamVaoGioHangDB(int userId, String tenMon) {
        String sqlGetProduct = "SELECT product_id FROM products WHERE product_name = ?";
        String sqlGetCart = "SELECT cart_id FROM carts WHERE user_id = ?";
        String sqlInsertCart = "INSERT INTO carts (user_id, updated_at) VALUES (?, GETDATE())";

        String sqlCheckDetail = "SELECT cart_detail_id FROM cart_details WHERE cart_id = ? AND product_id = ?";
        String sqlInsertDetail = "INSERT INTO cart_details (cart_id, product_id, quantity) VALUES (?, ?, 1)";
        String sqlUpdateDetail = "UPDATE cart_details SET quantity = quantity + 1 WHERE cart_detail_id = ?";

        try (Connection conn = getConnection()) {
            if (conn == null) {
                return;
            }

            int productId = -1;
            try (PreparedStatement psProd = conn.prepareStatement(sqlGetProduct)) {
                psProd.setString(1, tenMon);
                try (ResultSet rsProd = psProd.executeQuery()) {
                    if (rsProd.next()) {
                        productId = rsProd.getInt("product_id");
                    }
                }
            }

            if (productId == -1) {
                System.out.println("❌ Không tìm thấy sản phẩm có tên: " + tenMon + " trong DB!");
                return;
            }

            int cartId = -1;
            try (PreparedStatement psCart = conn.prepareStatement(sqlGetCart)) {
                psCart.setInt(1, userId);
                try (ResultSet rsCart = psCart.executeQuery()) {
                    if (rsCart.next()) {
                        cartId = rsCart.getInt("cart_id");
                    }
                }
            }

            if (cartId == -1) {
                try (PreparedStatement psInsCart = conn.prepareStatement(sqlInsertCart, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                    psInsCart.setInt(1, userId);
                    psInsCart.executeUpdate();
                    try (ResultSet generatedKeys = psInsCart.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            cartId = generatedKeys.getInt(1);
                        }
                    }
                }
            }

            int cartDetailId = -1;
            try (PreparedStatement psDetail = conn.prepareStatement(sqlCheckDetail)) {
                psDetail.setInt(1, cartId);
                psDetail.setInt(2, productId);
                try (ResultSet rsDetail = psDetail.executeQuery()) {
                    if (rsDetail.next()) {
                        cartDetailId = rsDetail.getInt("cart_detail_id");
                    }
                }
            }

            if (cartDetailId != -1) {
                try (PreparedStatement psUpDetail = conn.prepareStatement(sqlUpdateDetail)) {
                    psUpDetail.setInt(1, cartDetailId);
                    psUpDetail.executeUpdate();
                }
                String sqlUpdateCartTime = "UPDATE carts SET updated_at = GETDATE() WHERE cart_id = ?";
                try (PreparedStatement psUpCartTime = conn.prepareStatement(sqlUpdateCartTime)) {
                    psUpCartTime.setInt(1, cartId);
                    psUpCartTime.executeUpdate();
                }
            } else {
                try (PreparedStatement psInsDetail = conn.prepareStatement(sqlInsertDetail)) {
                    psInsDetail.setInt(1, cartId);
                    psInsDetail.setInt(2, productId);
                    psInsDetail.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Lỗi đồng bộ dữ liệu giỏ hàng: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static java.util.Map<String, int[]> taiGioHangCuTuDB(int userId) {
        java.util.Map<String, int[]> gioHangTaiLai = new java.util.HashMap<>();

        // Câu lệnh SQL truy vấn: Lấy ra Tên món, Số lượng và Giá từ giỏ hàng cũ của User
        String sql = "SELECT p.product_name, cd.quantity, p.price "
                + "FROM carts c "
                + "JOIN cart_details cd ON c.cart_id = cd.cart_id "
                + "JOIN products p ON cd.product_id = p.product_id "
                + "WHERE c.user_id = ?";

        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String tenMon = rs.getString("product_name").trim();
                    int soLuong = rs.getInt("quantity");
                    int giaTien = rs.getInt("price");

                    gioHangTaiLai.put(tenMon, new int[]{soLuong, giaTien});
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tải giỏ hàng cũ từ DB: " + e.getMessage());
            e.printStackTrace();
        }

        return gioHangTaiLai;
    }

    public static void capNhatSoLuongGioHangDB(int userId, String tenMon, int soLuongMoi) {
        String sql = "UPDATE cd SET cd.quantity = ? FROM cart_details cd "
                + "JOIN carts c ON cd.cart_id = c.cart_id "
                + "JOIN products p ON cd.product_id = p.product_id "
                + "WHERE c.user_id = ? AND p.product_name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, soLuongMoi);
            pstmt.setInt(2, userId);
            pstmt.setString(3, tenMon);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void xoaMonKhoiGioHangDB(int userId, String tenMon) {
        String sql = "DELETE cd FROM cart_details cd "
                + "JOIN carts c ON cd.cart_id = c.cart_id "
                + "JOIN products p ON cd.product_id = p.product_id "
                + "WHERE c.user_id = ? AND p.product_name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, tenMon);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
