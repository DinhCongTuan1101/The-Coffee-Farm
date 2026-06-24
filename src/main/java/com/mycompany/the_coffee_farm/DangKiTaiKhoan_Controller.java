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
        lblTenNguoiDung.setVisible(true);
        lblSDT.setVisible(true);
        lblEmail.setVisible(true);
        lblDiaChi.setVisible(true);
        lblPass1.setVisible(true);
        lblPass2.setVisible(true);

        caiDatLangNghe(txtTenNguoiDung, lblTenNguoiDung, "Vui lòng nhập tên người dùng!");
        caiDatLangNghe(txtSDT, lblSDT, "Vui lòng nhập số điện thoại!");
        caiDatLangNghe(txtEmail, lblEmail, "Vui lòng nhập Email!");
        caiDatLangNghe(txtDiaChi, lblDiaChi, "Vui lòng nhập địa chỉ!");
        caiDatLangNghe(txtPass1, lblPass1, "Vui lòng nhập mật khẩu!");
        caiDatLangNghe(txtPass2, lblPass2, "Vui lòng nhập lại mật khẩu!");
    }

    private void caiDatLangNghe(TextField txt, Label lbl, String loiMacDinh) {
        if (txt != null && lbl != null) {
            txt.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.trim().isEmpty()) {
                    lbl.setText(loiMacDinh);
                    lbl.setVisible(true);
                } else {
                    lbl.setVisible(false);
                }
            });
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

        boolean coLoiLocal = false;

        if (tenNguoiDung.isEmpty()) {
            lblTenNguoiDung.setText("Vui lòng nhập tên người dùng!");
            lblTenNguoiDung.setVisible(true);
            coLoiLocal = true;
        }

        if (diaChi.isEmpty()) {
            lblDiaChi.setText("Vui lòng nhập địa chỉ!");
            lblDiaChi.setVisible(true);
            coLoiLocal = true;
        }

        if (sdt.isEmpty()) {
            lblSDT.setText("Vui lòng nhập số điện thoại!");
            lblSDT.setVisible(true);
            coLoiLocal = true;
        } else if (!sdt.matches("^\\d{10}$")) {
            lblSDT.setText("Số điện thoại không hợp lệ!");
            lblSDT.setVisible(true);
            coLoiLocal = true;
        }

        if (email.isEmpty()) {
            lblEmail.setText("Vui lòng nhập Email!");
            lblEmail.setVisible(true);
            coLoiLocal = true;
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            lblEmail.setText("Email không hợp lệ!");
            lblEmail.setVisible(true);
            coLoiLocal = true;
        }

        if (pass1.isEmpty()) {
            lblPass1.setText("Vui lòng nhập mật khẩu!");
            lblPass1.setVisible(true);
            coLoiLocal = true;
        }

        if (pass2.isEmpty()) {
            lblPass2.setText("Vui lòng nhập lại mật khẩu!");
            lblPass2.setVisible(true);
            coLoiLocal = true;
        } else if (!pass1.isEmpty() && !pass1.equals(pass2)) {
            lblPass2.setText("Mật khẩu nhập lại không trùng khớp!");
            lblPass2.setVisible(true);
            coLoiLocal = true;
        }

        if (coLoiLocal) {
            return;
        }

        threadPool.execute(() -> {
            try {
                String passwordHash = hashPasswordSHA256(pass1);
                String resultStatus = registerUserInDB(tenNguoiDung, sdt, email, diaChi, passwordHash);

                Platform.runLater(() -> {
                    if (resultStatus.equals("SUCCESS")) {
                        try {
                            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangKiThanhCong.fxml"));
                            javafx.stage.Stage stage = (javafx.stage.Stage) txtSDT.getScene().getWindow();
                            stage.setScene(new javafx.scene.Scene(root));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (resultStatus.equals("ERROR")) {
                        System.out.println("❌ Lỗi hệ thống khi kết nối Database!");
                    } else {
                        if (resultStatus.contains("PHONE")) {
                            lblSDT.setText("Số điện thoại đã được sử dụng!");
                            lblSDT.setVisible(true);
                        }
                        if (resultStatus.contains("USERNAME")) {
                            lblTenNguoiDung.setText("Tên người dùng đã tồn tại!");
                            lblTenNguoiDung.setVisible(true);
                        }
                        if (resultStatus.contains("EMAIL")) {
                            lblEmail.setText("Email đã được sử dụng!");
                            lblEmail.setVisible(true);
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String registerUserInDB(String username, String phoneNumber, String email, String address, String passwordHash) {
        String loiTrungLap = "";
        String checkSql = "SELECT username, phone_number, email FROM users WHERE username = ? OR phone_number = ? OR email = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setString(1, username);
            checkStmt.setString(2, phoneNumber);
            checkStmt.setString(3, email);
            ResultSet rs = checkStmt.executeQuery();

            while (rs.next()) {
                if (username.equalsIgnoreCase(rs.getString("username")) && !loiTrungLap.contains("USERNAME")) {
                    loiTrungLap += "USERNAME,";
                }
                if (phoneNumber.equals(rs.getString("phone_number")) && !loiTrungLap.contains("PHONE")) {
                    loiTrungLap += "PHONE,";
                }
                if (email.equalsIgnoreCase(rs.getString("email")) && !loiTrungLap.contains("EMAIL")) {
                    loiTrungLap += "EMAIL,";
                }
            }

            if (!loiTrungLap.isEmpty()) {
                return loiTrungLap;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }

        String insertSql = "INSERT INTO users (username, phone_number, email, address, password_hash) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertSql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, phoneNumber);
            pstmt.setString(3, email);
            pstmt.setString(4, address);
            pstmt.setString(5, passwordHash);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0 ? "SUCCESS" : "ERROR";

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
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
