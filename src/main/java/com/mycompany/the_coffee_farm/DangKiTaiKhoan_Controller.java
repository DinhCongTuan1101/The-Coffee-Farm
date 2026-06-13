package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class DangKiTaiKhoan_Controller {

    @FXML private TextField txtSDT;
    @FXML private PasswordField txtPass1;
    @FXML private PasswordField txtPass2;

    @FXML private Label lblSDT;
    @FXML private Label lblPass1;
    @FXML private Label lblPass2;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @FXML
    public void initialize() {
        txtSDT.textProperty().addListener((observable, oldValue, newValue) -> {
            lblSDT.setVisible(newValue.isEmpty());
        });

        txtPass1.textProperty().addListener((observable, oldValue, newValue) -> {
            lblPass1.setVisible(newValue.isEmpty());
        });

        txtPass2.textProperty().addListener((observable, oldValue, newValue) -> {
            lblPass2.setVisible(newValue.isEmpty());
        });
    }
    @FXML
    public void xuLyDangKy(ActionEvent event) {
        String sdt = txtSDT.getText().trim();
        String pass1 = txtPass1.getText();
        String pass2 = txtPass2.getText();

        if (sdt.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!sdt.matches("^\\d{10}$")) { 
            hienThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", "Số điện thoại phải bao gồm 10 chữ số!");
            return;
        }

        if (!pass1.equals(pass2)) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi mật khẩu", "Mật khẩu nhập lại không trùng khớp!");
            return;
        }

        if (pass1.length() < 6) {
            hienThongBao(Alert.AlertType.WARNING, "Mật khẩu yếu", "Mật khẩu phải từ 6 ký tự trở lên!");
            return;
        }
        threadPool.execute(() -> {
            try {
                String passwordHash = hashPasswordSHA256(pass1);
                int resultStatus = registerUserInDB(sdt, passwordHash);
                Platform.runLater(() -> {
                    if (resultStatus == 1) {
                        hienThongBao(Alert.AlertType.INFORMATION, "Thành công", "Đăng ký tài khoản thành công!");
                        veTrangDangNhap(event);
                    } else if (resultStatus == -1) {
                        hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại này đã được đăng ký trước đó!");
                    } else {
                        hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đăng ký thất bại, vui lòng thử lại sau!");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private int registerUserInDB(String sdt, String passwordHash) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String checkUserSql = "SELECT user_id FROM users WHERE phone_number = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(checkUserSql)) {
                psCheck.setString(1, sdt);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        return -1;
                    }
                }
            }
            String insertUserSql = "INSERT INTO users (phone_number, password_hash, created_at) VALUES (?, ?, GETDATE())";
            try (PreparedStatement psInsert = conn.prepareStatement(insertUserSql)) {
                psInsert.setString(1, sdt);
                psInsert.setString(2, passwordHash);
                int rows = psInsert.executeUpdate();
                return rows > 0 ? 1 : 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
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

    @FXML
    public void veTrangDangNhap(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangNhap.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi không quay lại được trang Đăng Nhập!");
            e.printStackTrace();
        }
    }
}