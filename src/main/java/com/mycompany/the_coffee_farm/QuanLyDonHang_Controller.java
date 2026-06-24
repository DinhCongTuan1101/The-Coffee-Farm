package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class QuanLyDonHang_Controller {

    @FXML private TextField txtIdDon;
    @FXML private TextField txtTrangThai;

    @FXML
    private void capNhatTrangThai(ActionEvent event) {
        String id = txtIdDon.getText();
        String trangThai = txtTrangThai.getText();
        if (id.isEmpty() || trangThai.isEmpty()) {
            thongBaoChung("Thiếu thông tin", "Vui lòng nhập ID Đơn và Trạng thái mới!", Alert.AlertType.WARNING);
            return;
        }
        System.out.println("Cập nhật Đơn " + id + " thành: " + trangThai);
        thongBaoChung("Thành công", "Đã cập nhật trạng thái đơn hàng!", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void huyDon(ActionEvent event) {
        System.out.println("Đã hủy đơn hàng!");
    }

    @FXML
    private void xoaDon(ActionEvent event) {
        System.out.println("Đã xóa vĩnh viễn đơn hàng!");
    }

    @FXML
    private void quayLai(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminControl.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void thongBaoChung(String tieuDe, String noiDung, Alert.AlertType kieuIcon) {
        Alert alert = new Alert(kieuIcon);
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
}