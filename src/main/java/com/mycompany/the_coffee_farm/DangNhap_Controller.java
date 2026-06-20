package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.User;

public class DangNhap_Controller {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    @FXML private Button btnBack;
    @FXML private javafx.scene.control.TextField txtSDT;
    @FXML private javafx.scene.control.PasswordField txtMatKhau;
    
    @FXML private Label lblSDT;
    @FXML private Label lblMatKhau;

    @FXML
    public void initialize() {
        caiDatLangNghe(txtSDT, lblSDT, "Vui lòng nhập số điện thoại!");
        caiDatLangNghe(txtMatKhau, lblMatKhau, "Vui lòng nhập mật khẩu!");
    }

    private void caiDatLangNghe(javafx.scene.control.TextField txt, Label lbl, String loiMacDinh) {
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
    public void xuLyDangNhap(javafx.event.ActionEvent event) {
        String tk = txtSDT.getText().trim();
        String mk = txtMatKhau.getText();
        
        boolean coLoi = false;

        // 1. Quét lỗi bỏ trống tại chỗ
        if (tk.isEmpty()) {
            lblSDT.setText("Vui lòng nhập số điện thoại!");
            lblSDT.setVisible(true);
            coLoi = true;
        }
        
        if (mk.isEmpty()) {
            lblMatKhau.setText("Vui lòng nhập mật khẩu!");
            lblMatKhau.setVisible(true);
            coLoi = true;
        }

        if (coLoi) return; 

        // 2. Chọc DB kiểm tra đăng nhập
        threadPool.execute(() -> {
            try {
                String matKhauHash = hashPasswordSHA256(mk);
                User user = DBConnection.checkLoginInDB(tk, matKhauHash);

                if (user != null) {
                    System.out.println("Đăng nhập thành công!");

                    TaiKhoan.daDangNhap = false;
                    TaiKhoan.id = 0;
                    TaiKhoan.tenTaiKhoan = null;
                    TaiKhoan.sdt = null;
                    TaiKhoan.email = null;
                    TaiKhoan.diaChi = null;
                    if (TaiKhoan.gioHangChung != null) {
                        TaiKhoan.gioHangChung.clear();
                    }

                    TaiKhoan.daDangNhap = true;
                    TaiKhoan.id = user.getUserId();
                    TaiKhoan.tenTaiKhoan = user.getUsername();
                    TaiKhoan.sdt = user.getPhoneNumber();
                    TaiKhoan.email = user.getEmail();
                    TaiKhoan.diaChi = user.getAddress();

                    System.out.println("⚡ Đang tải lại giỏ hàng cũ từ cơ sở dữ liệu...");
                    java.util.Map<String, int[]> gioHangCu = DBConnection.taiGioHangCuTuDB(TaiKhoan.id);

                    if (gioHangCu != null && !gioHangCu.isEmpty()) {
                        if (TaiKhoan.gioHangChung == null) {
                            TaiKhoan.gioHangChung = new java.util.HashMap<>();
                        }
                        TaiKhoan.gioHangChung.putAll(gioHangCu);
                        System.out.println("🛒 Đã khôi phục thành công " + gioHangCu.size() + " mặt hàng vào giỏ!");
                    }

                    Platform.runLater(() -> {
                        try {
                            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangNhapThanhCong.fxml"));
                            javafx.stage.Stage stage = (javafx.stage.Stage) txtSDT.getScene().getWindow();
                            stage.setScene(new javafx.scene.Scene(root));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                } else {
                    Platform.runLater(() -> {
                        lblMatKhau.setText("Mật khẩu không chính xác!");
                        lblMatKhau.setVisible(true);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    lblMatKhau.setText("Lỗi kết nối máy chủ!");
                    lblMatKhau.setVisible(true);
                });
            }
        });
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
    public void moTrangQuenMatKhau(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("QuenMatKhau.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}