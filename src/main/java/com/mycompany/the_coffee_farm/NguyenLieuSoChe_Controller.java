package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class NguyenLieuSoChe_Controller {

    @FXML
    private Button btnBack;

    @FXML
    public void veTrangChu(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rooPrimary = FXMLLoader.load(getClass().getResource("primary.fxml"));
            Scene sceneMoi = new Scene(rooPrimary);
            stage.setScene(sceneMoi);
        } catch (Exception e) {
            System.out.println("Lỗi không quay lại được trang trước!");
            e.printStackTrace();
        }
    }

    @FXML
    private void ThemGioHang(ActionEvent event) {
        javafx.scene.control.Button btnClicked = (javafx.scene.control.Button) event.getSource();

        javafx.scene.layout.AnchorPane theMon = (javafx.scene.layout.AnchorPane) btnClicked.getParent();

        String tenMon = "";
        String chuoiGia = "";

        for (javafx.scene.Node node : theMon.getChildren()) {
            if (node instanceof javafx.scene.control.Label) {
                javafx.scene.control.Label lbl = (javafx.scene.control.Label) node;

                if (lbl.getText().contains("đ")) {
                    chuoiGia = lbl.getText();
                } else {
                    tenMon = lbl.getText();
                }
            }
        }

        int giaTien = 0;
        try {
            String soNguyen = chuoiGia.replaceAll("[^0-9]", "");
            giaTien = Integer.parseInt(soNguyen);
        } catch (Exception e) {
            System.out.println("Lỗi đọc giá tiền món: " + tenMon);
        }

        boolean daCoTrongGio = false;
        int soLuongHienTai = 1;

        for (GioHang.MonHang mh : GioHang.danhSachMua) {
            if (mh.tenMon.equals(tenMon)) {
                mh.soLuong += 1;
                soLuongHienTai = mh.soLuong;
                daCoTrongGio = true;
                break;
            }
        }

        if (!daCoTrongGio) {
            GioHang.danhSachMua.add(new GioHang.MonHang(tenMon, giaTien, 1));
        }

        System.out.println(">>> Vừa thêm: " + tenMon + " | Giá: " + giaTien + " | Số lượng: " + soLuongHienTai);
    }
}
