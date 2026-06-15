package com.mycompany.the_coffee_farm;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
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

        final String tenMonFinal = tenMon.trim();

        int giaTien = 0;
        try {
            String soNguyen = chuoiGia.replaceAll("[^0-9]", "");
            giaTien = Integer.parseInt(soNguyen);
        } catch (Exception e) {
            System.out.println("Lỗi đọc giá tiền món: " + tenMonFinal);
            return;
        }

        final int giaTienFinal = giaTien;

        if (TaiKhoan.gioHangChung.containsKey(tenMonFinal)) {
            TaiKhoan.gioHangChung.get(tenMonFinal)[0] += 1;
            System.out.println("Đã +1 số lượng món: " + tenMonFinal + " (Tổng: " + TaiKhoan.gioHangChung.get(tenMonFinal)[0] + ")");
        } else {
            TaiKhoan.gioHangChung.put(tenMonFinal, new int[]{1, giaTienFinal});
            System.out.println("Vừa thêm mới vào giỏ: " + tenMonFinal + " | Giá: " + giaTienFinal);
        }

        if (TaiKhoan.daDangNhap && TaiKhoan.id > 0) {
            threadPool.execute(() -> {
                database.DBConnection.themSanPhamVaoGioHangDB(TaiKhoan.id, tenMonFinal);
                System.out.println("⚡ Đã đồng bộ chi tiết món [" + tenMonFinal + "] vào DB thành công!");
            });
        } else {
            System.out.println("⚠️ Tài khoản vãng lai chưa đăng nhập -> Chỉ lưu tạm thời trên RAM ứng dụng.");
        }
    }

}
