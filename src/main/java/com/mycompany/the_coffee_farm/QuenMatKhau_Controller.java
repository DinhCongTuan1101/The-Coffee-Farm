package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class QuenMatKhau_Controller {

    @FXML private TextField txtSDT;
    @FXML private PasswordField txtPassMoi;
    @FXML private PasswordField txtXacNhanPass;
    
    @FXML private javafx.scene.layout.AnchorPane lopPhuThongBao;
    @FXML private javafx.scene.text.Text txtNoiDungThongBao;

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    
    private int trangThaiThongBao = 0; 

    @FXML
    public void xuLyDatLaiMatKhau(ActionEvent event) {
        String sdt = txtSDT.getText().trim();
        String passMoi = txtPassMoi.getText();
        String xacNhanPass = txtXacNhanPass.getText();

        if (sdt.isEmpty() || passMoi.isEmpty() || xacNhanPass.isEmpty()) {
            hienLopPhu(0, "Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!passMoi.equals(xacNhanPass)) {
            hienLopPhu(2, "Mật khẩu xác nhận không khớp!");
            return;
        }

        threadPool.execute(() -> {
            try {
                String passwordHash = hashPasswordSHA256(passMoi);
                int status = updatePasswordInDB(sdt, passwordHash);

                Platform.runLater(() -> {
                    if (status == 1) {
                   
                        hienLopPhu(1, "Đã đổi mật khẩu thành công!");
                    } else {
                        hienLopPhu(0, "Số điện thoại không tồn tại!");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void hienLopPhu(int trangThai, String loiNhan) {
        trangThaiThongBao = trangThai;
        txtNoiDungThongBao.setText(loiNhan);
        lopPhuThongBao.setVisible(true);
    }
    @FXML
    public void tatLopPhuThongBao(javafx.scene.input.MouseEvent event) {
        lopPhuThongBao.setVisible(false); 
        
        if (trangThaiThongBao == 1) {
            try {
                javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangNhap.fxml"));
                javafx.stage.Stage stage = (javafx.stage.Stage) lopPhuThongBao.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (trangThaiThongBao == 2) {
            txtPassMoi.clear();
            txtXacNhanPass.clear();
        }
    }

    private int updatePasswordInDB(String sdt, String newPasswordHash) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String updateSql = "UPDATE users SET password_hash = ? WHERE phone_number = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setString(1, newPasswordHash);
                psUpdate.setString(2, sdt);
                int rows = psUpdate.executeUpdate();
                return rows > 0 ? 1 : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }

    private String hashPasswordSHA256(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @FXML
    public void veTrangDangNhap(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangNhap.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}