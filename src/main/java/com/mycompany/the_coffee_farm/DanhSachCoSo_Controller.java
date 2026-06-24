package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class DanhSachCoSo_Controller {

    @FXML private RadioButton rdoHK;
    @FXML private RadioButton rdoHBT;
    @FXML private RadioButton rdoCG;
    @FXML private RadioButton rdoLB;
    @FXML private RadioButton rdoTX;
    @FXML private RadioButton rdoHD;

    @FXML
    private void xacNhanCoSo(ActionEvent event) {
        String coSoNhanDon = "";

        if (rdoHK.isSelected()) {
            coSoNhanDon = "The Coffee Farm - Hoàn Kiếm";
        } else if (rdoHBT.isSelected()) {
            coSoNhanDon = "The Coffee Farm - Hai Bà Trưng";
        } else if (rdoCG.isSelected()) {
            coSoNhanDon = "The Coffee Farm - Cầu Giấy";
        } else if (rdoLB.isSelected()) {
            coSoNhanDon = "The Coffee Farm - Long Biên";
        } else if (rdoTX.isSelected()) {
            coSoNhanDon = "The Coffee Farm - Thanh Xuân";
        } else if (rdoHD.isSelected()) {
            coSoNhanDon = "The Coffee Farm - Hà Đông";
        }

        if (coSoNhanDon.equals("")) {
            System.out.println("Quên tick chọn cơ sở!");
            return; 
        }

        System.out.println("Có đơn hàng mới tại quán.");
        System.out.println("Cơ sở nhận lệnh: " + coSoNhanDon);
        
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MuaHangThanhCong(Off).fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void quayLaiGioHang(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("Order_Screen.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}