
package com.mycompany.the_coffee_farm;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FoodAndDrink_Controller {
        @FXML
    private Button btnBack;
    
        @FXML
    public void veTrangChu(ActionEvent event){
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rooPrimary = FXMLLoader.load(getClass().getResource("primary.fxml"));
            Scene sceneMoi = new Scene(rooPrimary);
            stage.setScene(sceneMoi);
        } catch(Exception e){
            System.out.println("Lỗi không quay lại được trang trước!");
            e.printStackTrace();
        }
    }
    @FXML
    private VBox vboxDoAn;
    @FXML
    private VBox vboxDoUong;

    public void initialize(URL url, ResourceBundle rb) {
        vboxDoAn.setVisible(true);
        vboxDoAn.setManaged(true);
        vboxDoUong.setVisible(false);
        vboxDoUong.setManaged(false);
    }
    @FXML
    private void showDoAn(ActionEvent event) {
        vboxDoAn.setVisible(true);
        vboxDoAn.setManaged(true);
        vboxDoUong.setVisible(false);
        vboxDoUong.setManaged(false);
    }
    @FXML
    private void showDoUong(ActionEvent event) {
        vboxDoAn.setVisible(false);
        vboxDoAn.setManaged(false);
        vboxDoUong.setVisible(true);
        vboxDoUong.setManaged(true);
    }
    @FXML
    private TextField txtTimMon; 
    @FXML
    private void timKiem(KeyEvent event) {
        String tuKhoa = txtTimMon.getText().toLowerCase().trim();
        System.out.println("Đang gõ tìm kiếm: " + tuKhoa);
        locDanhSach(vboxDoAn, tuKhoa);
        locDanhSach(vboxDoUong, tuKhoa);
    }

private void locDanhSach(VBox danhSach, String tuKhoa) {
        for (javafx.scene.Node node : danhSach.getChildren()) {
            if (node instanceof javafx.scene.layout.AnchorPane) {
                javafx.scene.layout.AnchorPane theMon = (javafx.scene.layout.AnchorPane) node;
                boolean giuLai = false;
                for (javafx.scene.Node thanhPhan : theMon.getChildren()) {
                    if (thanhPhan instanceof javafx.scene.control.Label) {
                        javafx.scene.control.Label lbl = (javafx.scene.control.Label) thanhPhan;
                        if (lbl.getText().toLowerCase().contains(tuKhoa)) {
                            giuLai = true;
                            break; 
                        }
                    }
                }
                theMon.setVisible(giuLai);
                theMon.setManaged(giuLai);
            }
        }
    }
    
}
