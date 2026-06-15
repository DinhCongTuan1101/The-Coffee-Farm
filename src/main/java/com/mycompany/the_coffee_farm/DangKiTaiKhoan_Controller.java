package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
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

    @FXML
    private TextField txtSDT;
    @FXML
    private PasswordField txtPass1;
    @FXML
    private PasswordField txtPass2;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtDiaChi;
    @FXML
    private TextField txtTenNguoiDung;

    @FXML
    private Label lblSDT;
    @FXML
    private Label lblPass1;
    @FXML
    private Label lblPass2;
    @FXML
    private Label lblEmail;
    @FXML
    private Label lblDiaChi;
    @FXML
    private Label lblTenNguoiDung;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @FXML
    public void initialize() {
        if (txtSDT != null && lblSDT != null) {
            txtSDT.textProperty().addListener((observable, oldValue, newValue) -> lblSDT.setVisible(newValue.isEmpty()));
        }
        if (txtPass1 != null && lblPass1 != null) {
            txtPass1.textProperty().addListener((observable, oldValue, newValue) -> lblPass1.setVisible(newValue.isEmpty()));
        }
        if (txtPass2 != null && lblPass2 != null) {
            txtPass2.textProperty().addListener((observable, oldValue, newValue) -> lblPass2.setVisible(newValue.isEmpty()));
        }
        if (txtEmail != null && lblEmail != null) {
            txtEmail.textProperty().addListener((observable, oldValue, newValue) -> lblEmail.setVisible(newValue.isEmpty()));
        }
        if (txtDiaChi != null && lblDiaChi != null) {
            txtDiaChi.textProperty().addListener((observable, oldValue, newValue) -> lblDiaChi.setVisible(newValue.isEmpty()));
        }
        if (txtTenNguoiDung != null && lblTenNguoiDung != null) {
            txtTenNguoiDung.textProperty().addListener((observable, oldValue, newValue) -> lblTenNguoiDung.setVisible(newValue.isEmpty()));
        }
    }

    @FXML
    public void xuLyDangKy(ActionEvent event) {
        String sdt = txtSDT.getText().trim();
        String pass1 = txtPass1.getText();
        String pass2 = txtPass2.getText();
        String email = txtEmail.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String tenNguoiDung = txtTenNguoiDung.getText().trim();

        if (sdt.isEmpty() || pass1.isEmpty() || pass2.isEmpty() || email.isEmpty() || diaChi.isEmpty() || tenNguoiDung.isEmpty()) {
            hienThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!sdt.matches("^\\d{10}$")) {
            hienThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", "Số điện thoại phải bao gồm 10 chữ số!");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            hienThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", "Email không đúng định dạng!");
            return;
        }

        if (!pass1.equals(pass2)) {
            hienThongBao(Alert.AlertType.ERROR, "Lỗi mật khẩu", "Mật khẩu nhập lại không trùng khớp!");
            return;
        }

        threadPool.execute(() -> {
            try {
                String passwordHash = hashPasswordSHA256(pass1);

                int resultStatus = registerUserInDB(tenNguoiDung, sdt, email, diaChi, passwordHash);

                Platform.runLater(() -> {
                    if (resultStatus == 1) {
                        try {
                            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangKiThanhCong.fxml"));
                            javafx.stage.Stage stage = (javafx.stage.Stage) txtSDT.getScene().getWindow();
                            stage.setScene(new javafx.scene.Scene(root));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (resultStatus == -1) {
                        hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Số điện thoại này đã được đăng ký trước đó!");
                    } else if (resultStatus == -2) {

                        hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Tên người dùng này đã tồn tại!");
                    } else if (resultStatus == -3) {

                        hienThongBao(Alert.AlertType.ERROR, "Lỗi", "Email này đã được sử dụng!");
                    } else {
                        hienThongBao(Alert.AlertType.ERROR, "Lỗi hệ thống", "Đăng ký thất bại, vui lòng thử lại sau!");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public int registerUserInDB(String username, String phoneNumber, String email, String address, String passwordHash) {
        String sql = "INSERT INTO users (username, phone_number, email, address, password_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setString(5, passwordHash);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0 ? 1 : 0;

        } catch (SQLException e) {
            if (e.getErrorCode() == 2627 || e.getMessage().contains("UQ__users")) {
                if (e.getMessage().contains("phone_number")) {
                    return -1;
                }
                if (e.getMessage().contains("username")) {
                    return -2;
                }
                if (e.getMessage().contains("email")) {
                    return -3;
                }
            }
            e.printStackTrace();
            return 0;
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
            e.printStackTrace();
        }
    }
}
