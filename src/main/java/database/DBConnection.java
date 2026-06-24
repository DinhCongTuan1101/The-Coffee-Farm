package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.User;
import com.mycompany.the_coffee_farm.SanPham;

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
            if (conn == null) return;

            int productId = -1;
            try (PreparedStatement psProd = conn.prepareStatement(sqlGetProduct)) {
                psProd.setString(1, tenMon);
                try (ResultSet rsProd = psProd.executeQuery()) {
                    if (rsProd.next()) productId = rsProd.getInt("product_id");
                }
            }
            if (productId == -1) return;

            int cartId = -1;
            try (PreparedStatement psCart = conn.prepareStatement(sqlGetCart)) {
                psCart.setInt(1, userId);
                try (ResultSet rsCart = psCart.executeQuery()) {
                    if (rsCart.next()) cartId = rsCart.getInt("cart_id");
                }
            }

            if (cartId == -1) {
                try (PreparedStatement psInsCart = conn.prepareStatement(sqlInsertCart, Statement.RETURN_GENERATED_KEYS)) {
                    psInsCart.setInt(1, userId);
                    psInsCart.executeUpdate();
                    try (ResultSet generatedKeys = psInsCart.getGeneratedKeys()) {
                        if (generatedKeys.next()) cartId = generatedKeys.getInt(1);
                    }
                }
            }

            int cartDetailId = -1;
            try (PreparedStatement psDetail = conn.prepareStatement(sqlCheckDetail)) {
                psDetail.setInt(1, cartId);
                psDetail.setInt(2, productId);
                try (ResultSet rsDetail = psDetail.executeQuery()) {
                    if (rsDetail.next()) cartDetailId = rsDetail.getInt("cart_detail_id");
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
            e.printStackTrace();
        }
    }

    public static Map<String, int[]> taiGioHangCuTuDB(int userId) {
        Map<String, int[]> gioHangTaiLai = new HashMap<>();
        String sql = "SELECT p.product_name, cd.quantity, p.price FROM carts c JOIN cart_details cd ON c.cart_id = cd.cart_id JOIN products p ON cd.product_id = p.product_id WHERE c.user_id = ?";
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
            e.printStackTrace();
        }
        return gioHangTaiLai;
    }

    public static void capNhatSoLuongGioHangDB(int userId, String tenMon, int soLuongMoi) {
        String sql = "UPDATE cd SET cd.quantity = ? FROM cart_details cd JOIN carts c ON cd.cart_id = c.cart_id JOIN products p ON cd.product_id = p.product_id WHERE c.user_id = ? AND p.product_name = ?";
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
        String sql = "DELETE cd FROM cart_details cd JOIN carts c ON cd.cart_id = c.cart_id JOIN products p ON cd.product_id = p.product_id WHERE c.user_id = ? AND p.product_name = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, tenMon);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<SanPham> layDanhSachSanPhamAdmin() {
        ObservableList<SanPham> list = FXCollections.observableArrayList();
        String sql = "SELECT [product_id], [category_id], [product_name], [price], [unit], [image_url] FROM [products] WHERE [status] = 1";
        
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("product_id"));
                String ten = rs.getString("product_name");
                double gia = rs.getDouble("price");
                String donVi = rs.getString("unit");
                int idLoai = rs.getInt("category_id");
                String hinhAnh = rs.getString("image_url"); 
                
                list.add(new SanPham(id, ten, gia, donVi, idLoai, hinhAnh));
            }
        } catch (SQLException e) {
            System.err.println("❌ Lỗi khi tải danh sách sản phẩm admin từ DB: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public static boolean themSanPhamAdmin(int categoryId, String ten, double gia, String hinhAnh) {
        String sql = "INSERT INTO products (category_id, product_name, price, unit, image_url, status) VALUES (?, ?, ?, N'ly', ?, 1)";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, ten);
            ps.setDouble(3, gia);
            ps.setString(4, hinhAnh);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean suaSanPhamAdmin(String id, int categoryId, String tenMoi, double giaMoi, String anhMoi) {
        String sql;
        if (anhMoi != null && !anhMoi.isEmpty()) {
            sql = "UPDATE products SET category_id = ?, product_name = ?, price = ?, image_url = ? WHERE product_id = ?";
        } else {
            sql = "UPDATE products SET category_id = ?, product_name = ?, price = ? WHERE product_id = ?";
        }

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ps.setString(2, tenMoi);
            ps.setDouble(3, giaMoi);
            if (anhMoi != null && !anhMoi.isEmpty()) {
                ps.setString(4, anhMoi);
                ps.setString(5, id);
            } else {
                ps.setString(4, id);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean xoaSanPhamAdmin(String id) {
        String sql = "UPDATE products SET status = 0 WHERE product_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static ObservableList<com.mycompany.the_coffee_farm.QuanLyTaiKhoan_Controller.TaiKhoanItem> layDanhSachTaiKhoanAdmin() {
        ObservableList<com.mycompany.the_coffee_farm.QuanLyTaiKhoan_Controller.TaiKhoanItem> list = FXCollections.observableArrayList();
        String sql = "SELECT user_id, phone_number, username, ISNULL(status, 1) as status FROM users";
        
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String id = String.valueOf(rs.getInt("user_id"));
                String sdt = rs.getString("phone_number");
                String ten = rs.getString("username");
                if(ten == null || ten.isEmpty()) ten = "Khách hàng";
                int status = rs.getInt("status");
                String trangThai = (status == 1) ? "Hoạt động" : "Đã khóa";
                
                list.add(new com.mycompany.the_coffee_farm.QuanLyTaiKhoan_Controller.TaiKhoanItem(id, sdt, ten, trangThai));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

public static boolean taoTaiKhoanAdmin(String ten, String sdt, String email, String matKhau, String diaChi) {
        String sql = "INSERT INTO users (username, phone_number, email, password_hash, address, status) VALUES (?, ?, ?, ?, ?, 1)";
        try (java.sql.Connection conn = getConnection(); java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ten);
            ps.setString(2, sdt);
            ps.setString(3, email);
            ps.setString(4, matKhau);
            ps.setString(5, diaChi);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean capNhatMatKhauAdmin(String id, String passMoi) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passMoi);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean khoaHoacMoTaiKhoanAdmin(String id, boolean laKhoa) {
        int statusMoi = laKhoa ? 0 : 1; 
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, statusMoi);
            ps.setString(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public static boolean xoaTaiKhoanAdmin(String id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}