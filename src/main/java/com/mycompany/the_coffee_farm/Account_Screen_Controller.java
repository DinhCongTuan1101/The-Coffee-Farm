
package com.mycompany.the_coffee_farm;

import javafx.fxml.FXML;

public class Account_Screen_Controller {
@FXML
    private void moDieuKhoan(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DieuKhoan.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi chuyển sang Điều Khoản!");
            e.printStackTrace();
        }
    }
}
