package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MuaHangThanhCong_Controller {

    @FXML
    private void quayVeTrangChu(ActionEvent event) {
        try {
            TaiKhoan.phuongThucNhan = 0;

            Parent root = FXMLLoader.load(getClass().getResource("primary.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root)); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}