/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 *
 * @author admin
 */
public class DangNhap_Controller {

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
        String tk = txtSDT.getText();
        String mk = txtMatKhau.getText();

        if (tk.isEmpty() || mk.isEmpty()) {
            System.out.println("Ê! Chưa nhập đủ thông tin kìa!");
            return;
        }

        if (tk.equals("admin") && mk.equals("123")) {
            System.out.println("Đăng nhập thành công!");

            TaiKhoan.daDangNhap = true;
            TaiKhoan.tenTaiKhoan = tk;

            veTrangChu(event);
        } else {
            System.out.println("Sai tài khoản hoặc mật khẩu!");
        }
    }
}
