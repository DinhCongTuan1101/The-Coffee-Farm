package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;

public class DiaChiDaLuu_Controller {
    @FXML private Label txtTen;
    @FXML private Label txtDauGach;
    @FXML private Label txtSDT;
    @FXML private Label txtDiaChi;
    @FXML private RadioButton rdoChon;

    private String tenNguoiNhan;
    private String sdtNguoiNhan;
    private String diaChiNhan;

    public void setData(String ten, String sdt, String diaChi) {
        this.tenNguoiNhan = ten;
        this.sdtNguoiNhan = sdt;
        this.diaChiNhan = diaChi;
        txtTen.setText(ten);
        txtSDT.setText(sdt);
        txtDiaChi.setText(diaChi);
    }
    
    public void setCheDoHienThi(boolean isCheckout) {
        if (!isCheckout) {
            rdoChon.setVisible(false); 
            
            txtTen.setLayoutX(txtTen.getLayoutX() - 25);
            txtDauGach.setLayoutX(txtDauGach.getLayoutX() - 25);
            txtSDT.setLayoutX(txtSDT.getLayoutX() - 25);
            txtDiaChi.setLayoutX(txtDiaChi.getLayoutX() - 25);
        }
    }

    public RadioButton getRadioButton() {
        return rdoChon;
    }

    @FXML
    public void chonDiaChiNay(ActionEvent event) {
        if (DanhSachDiaChi_Controller.instance != null) {
            DanhSachDiaChi_Controller.instance.setDiaChiDangChon(tenNguoiNhan, sdtNguoiNhan, diaChiNhan, rdoChon);
        }
    }
}