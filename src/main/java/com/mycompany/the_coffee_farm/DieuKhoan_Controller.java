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
public class DieuKhoan_Controller {
    @FXML
    private void quayLaiAccount(javafx.event.ActionEvent event) {
        try {
            // Nhớ thay chữ "Account_Screen.fxml" bằng đúng cái tên file giao diện Account của m
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("Account_Screen.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi quay về Account!");
            e.printStackTrace();
        }
    }
    
}
