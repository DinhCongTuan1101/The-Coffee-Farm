package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DangNhap_Controller {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    
    @FXML
    private Button btnBack;

    @FXML
    public void veTrangChu(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rooPrimary = FXMLLoader.load(getClass().getResource("primary.fxml"));
            Scene sceneMoi = new Scene(rooPrimary);
            stage.setScene(sceneMoi);
        } catch (Exception e) {
            System.out.println("Lỗi không quay lại được trang trước!");
            e.printStackTrace();
        }
    }
    @FXML
    public void moTrangDangKi(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangKiTaiKhoan.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi không load được trang Đăng Ký!");
            e.printStackTrace();
        }
    }
    @FXML
    private javafx.scene.control.TextField txtSDT;

    @FXML
    private javafx.scene.control.PasswordField txtMatKhau;

    @FXML
    public void xuLyDangNhap(javafx.event.ActionEvent event) {
        String tk = txtSDT.getText().trim();
        String mk = txtMatKhau.getText();
        if (tk.isEmpty() || mk.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ số điện thoại và mật khẩu!");
            return;
        }
        threadPool.execute(() -> {
            try {
                String matKhauHash = hashPasswordSHA256(mk);               
                int userId = checkLoginInDB(tk, matKhauHash);
                Platform.runLater(() -> {
                    if (userId != -1) {
                        System.out.println("Đăng nhập thành công!");
                        TaiKhoan.daDangNhap = true;
                        TaiKhoan.id = userId; 
                        TaiKhoan.tenTaiKhoan = tk;                         
                        veTrangChu(event);
                    } else {
                        System.out.println("Sai tài khoản hoặc mật khẩu!");
                        hienThongBao(Alert.AlertType.ERROR, "Lỗi đăng nhập", "Số điện thoại hoặc mật khẩu không chính xác!");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Không thể kết nối đến cơ sở dữ liệu!");
                });
            }
        });
    }
    private int checkLoginInDB(String sdt, String passwordHash) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();           
            String loginSql = "SELECT user_id FROM users WHERE phone_number = ? AND password_hash = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(loginSql)) {
                ps.setString(1, sdt);
                ps.setString(2, passwordHash);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("user_id"); 
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
        return -1;
    }    
    private String hashPasswordSHA256(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    private void hienThongBao(Alert.AlertType type, String tieuDe, String noiDung) {
        Alert alert = new Alert(type);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}
