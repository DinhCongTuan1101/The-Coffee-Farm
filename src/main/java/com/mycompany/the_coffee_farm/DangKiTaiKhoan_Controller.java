package com.mycompany.the_coffee_farm;

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
    private Label lblSDT;
    @FXML
    private Label lblPass1;
    @FXML
    private Label lblPass2;

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
    public void veTrangDangNhap(javafx.event.ActionEvent event) {
        try {
            // Khúc này là load thẳng về file Đăng Nhập nè m
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangNhap.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            // Thay màn hình
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi không quay lại được trang Đăng Nhập!");
            e.printStackTrace();
        }
    }
}
