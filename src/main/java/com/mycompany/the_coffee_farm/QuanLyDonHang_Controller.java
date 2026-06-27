package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class QuanLyDonHang_Controller implements Initializable {

    @FXML private TableView<DonHangItem> tblDonHang;
    @FXML private TableColumn<DonHangItem, String> colIdDon;
    @FXML private TableColumn<DonHangItem, String> colSdtKhach;
    @FXML private TableColumn<DonHangItem, String> colTongTien;
    @FXML private TableColumn<DonHangItem, String> colTrangThai;

    @FXML private TextField txtIdDon;
    @FXML private TextField txtTrangThai;

    // Các biến cho Popup thông báo tự làm
    @FXML private StackPane paneThongBao;
    @FXML private Label lblNoiDungThongBao;

    private ObservableList<DonHangItem> danhSachDonHang = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colIdDon.setCellValueFactory(new PropertyValueFactory<>("idDon"));
        colSdtKhach.setCellValueFactory(new PropertyValueFactory<>("sdtKhach"));
        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));

        loadDataLenBang();

        // Giữ nguyên hiển thị full TCF-DH ở Textbox cho đẹp
        tblDonHang.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && tblDonHang.getSelectionModel().getSelectedItem() != null) {
                DonHangItem selected = tblDonHang.getSelectionModel().getSelectedItem();
                txtIdDon.setText(selected.getIdDon()); 
                txtTrangThai.setText(selected.getTrangThai());
            }
        });
    }

    private void loadDataLenBang() {
        danhSachDonHang.clear();
        String sql = "SELECT o.order_id, u.phone_number, o.total_amount, o.order_status " +
                     "FROM orders o JOIN users u ON o.user_id = u.user_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String idHienThi = String.format("TCF-DH%04d", rs.getInt("order_id"));
                String sdt = rs.getString("phone_number");
                int tongTienRaw = rs.getInt("total_amount");
                String trangThai = rs.getString("order_status");

                String tongTienHienThi = String.format("%,dđ", tongTienRaw).replace(",", ".");

                danhSachDonHang.add(new DonHangItem(idHienThi, sdt, tongTienHienThi, trangThai));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tblDonHang.setItems(danhSachDonHang);
    }

    // Hàm lấy ID nguyên gốc từ chuỗi hiển thị
    private int getRawId(String idHienThi) {
        String raw = idHienThi.replaceAll("[^0-9]", ""); // Lọc bỏ mọi thứ không phải là số
        return Integer.parseInt(raw);
    }

    @FXML
    private void capNhatTrangThai(ActionEvent event) {
        String id = txtIdDon.getText().trim();
        String trangThai = txtTrangThai.getText().trim();

        if (id.isEmpty() || trangThai.isEmpty()) {
            hienThiThongBao("Vui lòng nhập ID Đơn và Trạng thái mới!");
            return;
        }

        String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, trangThai);
            ps.setInt(2, getRawId(id)); // Lấy ID số

            if (ps.executeUpdate() > 0) {
                hienThiThongBao("Đã cập nhật trạng thái đơn hàng thành công!");
                loadDataLenBang();
                txtIdDon.clear();
                txtTrangThai.clear();
            } else {
                hienThiThongBao("Lỗi: Không tìm thấy mã đơn hàng này!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi hệ thống: Cập nhật dữ liệu thất bại!");
        }
    }

    @FXML
    private void huyDon(ActionEvent event) {
        String id = txtIdDon.getText().trim();

        if (id.isEmpty()) {
            hienThiThongBao("Vui lòng nhập hoặc chọn ID Đơn cần hủy!");
            return;
        }

        String sql = "UPDATE orders SET order_status = N'Đã hủy' WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, getRawId(id)); // Lấy ID số

            if (ps.executeUpdate() > 0) {
                hienThiThongBao("Đã hủy đơn hàng thành công!");
                loadDataLenBang();
                txtIdDon.clear();
                txtTrangThai.clear();
            } else {
                hienThiThongBao("Lỗi: Không tìm thấy mã đơn hàng này!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi hệ thống: Hủy đơn hàng thất bại!");
        }
    }

    @FXML
    private void xoaDon(ActionEvent event) {
        String id = txtIdDon.getText().trim();

        if (id.isEmpty()) {
            hienThiThongBao("Vui lòng nhập hoặc chọn ID Đơn cần xóa!");
            return;
        }

        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, getRawId(id)); // Lấy ID số

            if (ps.executeUpdate() > 0) {
                hienThiThongBao("Đã xóa vĩnh viễn đơn hàng thành công!");
                loadDataLenBang();
                txtIdDon.clear();
                txtTrangThai.clear();
            } else {
                hienThiThongBao("Lỗi: Không tìm thấy mã đơn hàng này!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            hienThiThongBao("Lỗi hệ thống: Không thể xóa đơn do ràng buộc dữ liệu!");
        }
    }

    @FXML
    private void quayLai(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminControl.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Các hàm xử lý Popup thông báo tự làm
    private void hienThiThongBao(String noiDung) {
        lblNoiDungThongBao.setText(noiDung);
        paneThongBao.setVisible(true);
    }

    @FXML
    private void dongThongBao(ActionEvent event) {
        paneThongBao.setVisible(false);
    }

    public static class DonHangItem {
        private String idDon;
        private String sdtKhach;
        private String tongTien;
        private String trangThai;

        public DonHangItem(String idDon, String sdtKhach, String tongTien, String trangThai) {
            this.idDon = idDon;
            this.sdtKhach = sdtKhach;
            this.tongTien = tongTien;
            this.trangThai = trangThai;
        }

        public String getIdDon() { return idDon; }
        public String getSdtKhach() { return sdtKhach; }
        public String getTongTien() { return tongTien; }
        public String getTrangThai() { return trangThai; }
    }
}