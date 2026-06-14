package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class DaDangNhap_Controller {

    @FXML private javafx.scene.layout.AnchorPane lopPhuXacNhan;
    @FXML private javafx.scene.text.Text txtNoiDungXacNhan;
    @FXML private javafx.scene.text.Text txtHienThiTen;
    @FXML private javafx.scene.text.Text txtHienThiSDT;
    @FXML private javafx.scene.text.Text txtHienThiEmail;
    @FXML private javafx.scene.text.Text txtHienThiDiaChi;
    
    private int loaiHanhDong = 0; 
    @FXML
    public void initialize() {
        if (txtHienThiTen != null) txtHienThiTen.setText(TaiKhoan.tenNguoiDung);
        if (txtHienThiSDT != null) txtHienThiSDT.setText(TaiKhoan.tenTaiKhoan); 
        if (txtHienThiEmail != null) txtHienThiEmail.setText(TaiKhoan.email);
        if (txtHienThiDiaChi != null) txtHienThiDiaChi.setText(TaiKhoan.diaChi);
    }

    @FXML
    public void bamChuyenDoiTaiKhoan(ActionEvent event) {
        loaiHanhDong = 1;
        txtNoiDungXacNhan.setText("Bạn có chắc chắn muốn đăng xuất để chuyển sang tài khoản khác không?");
        lopPhuXacNhan.setVisible(true); 
    }

    @FXML
    public void xuLyDangXuat(ActionEvent event) {
        loaiHanhDong = 2;
        txtNoiDungXacNhan.setText("Bạn có chắc chắn muốn thoát khỏi tài khoản này không?");
        lopPhuXacNhan.setVisible(true);
    }

    @FXML
    public void bamNutHuyXacNhan(ActionEvent event) {
        lopPhuXacNhan.setVisible(false); 
    }

    @FXML
    public void bamNutDongYXacNhan(ActionEvent event) {
        TaiKhoan.daDangNhap = false;
        TaiKhoan.id = 0;
        TaiKhoan.tenTaiKhoan = "";
        TaiKhoan.gioHangChung.clear();
        TaiKhoan.phuongThucNhan = 0;

        try {
            String trangCanMo = (loaiHanhDong == 1) ? "DangNhap.fxml" : "primary.fxml";
            
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(trangCanMo));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
    
    @FXML
    public void bamNutBack(ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("primary.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }
}