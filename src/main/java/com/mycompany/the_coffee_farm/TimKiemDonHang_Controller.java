package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TimKiemDonHang_Controller implements Initializable {

    @FXML private TextField txtSdtTraCuu;
    @FXML private VBox vboxDanhSachDon; // Nơi hiển thị các "card" đơn hàng
    
    // Các biến cho Popup thông báo tự làm
    @FXML private StackPane paneThongBao;
    @FXML private Label lblNoiDungThongBao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Khởi tạo màn hình, có thể add thêm hiệu ứng nếu cần
    }

    @FXML
    private void xuLyTimKiem(ActionEvent event) {
        String sdt = txtSdtTraCuu.getText().trim();
        if (sdt.isEmpty()) {
            hienThiThongBao("Vui lòng nhập số điện thoại để tra cứu!");
            return;
        }

        vboxDanhSachDon.getChildren().clear();

        String sql = "SELECT o.ordered_at, o.total_amount, o.order_status " +
                     "FROM orders o JOIN users u ON o.user_id = u.user_id " +
                     "WHERE u.phone_number = ? ORDER BY o.ordered_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            
            boolean timThay = false;
            while (rs.next()) {
                timThay = true;
                taoCardDonHang(
                    rs.getString("ordered_at"),
                    rs.getInt("total_amount"),
                    rs.getString("order_status")
                );
            }
            
            if (!timThay) {
                hienThiThongBao("Không tìm thấy đơn hàng nào với SĐT này!");
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi kết nối CSDL!");
            e.printStackTrace();
        }
    }

    // Hàm tạo "card" đơn hàng giống phong cách The Coffee House
    private void taoCardDonHang(String ngay, int tongTien, String trangThai) {
        VBox card = new VBox(8);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 12; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 8, 0, 0, 2);");

        Label lblNgay = new Label("Ngày đặt: " + ngay);
        lblNgay.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

        String tienFormat = String.format("%,dđ", tongTien).replace(",", ".");
        Label lblTien = new Label("Tổng tiền: " + tienFormat);
        lblTien.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblTrangThai = new Label("Trạng thái: " + trangThai);
        lblTrangThai.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #d35400;");

        card.getChildren().addAll(lblNgay, lblTien, lblTrangThai);
        vboxDanhSachDon.getChildren().add(card);
    }

    // Các hàm xử lý Popup thông báo
    private void hienThiThongBao(String noiDung) {
        Platform.runLater(() -> {
            lblNoiDungThongBao.setText(noiDung);
            paneThongBao.setVisible(true);
        });
    }

    @FXML
    private void dongThongBao(ActionEvent event) {
        paneThongBao.setVisible(false);
    }

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