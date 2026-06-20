/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.the_coffee_farm;

import javafx.fxml.FXML;

/**
 *
 * @author admin
 */
public class VeChungToi_Controller {
    
    @FXML
    private void quayLaiAccount(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent voChinh = javafx.fxml.FXMLLoader.load(getClass().getResource("ThanhChucNang.fxml"));
            javafx.scene.Parent ruotAccount = javafx.fxml.FXMLLoader.load(getClass().getResource("Account_Screen.fxml"));

            javafx.scene.layout.BorderPane khungChinh = (javafx.scene.layout.BorderPane) voChinh.lookup("#khungChinh");
            khungChinh.setCenter(ruotAccount);

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(voChinh));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
