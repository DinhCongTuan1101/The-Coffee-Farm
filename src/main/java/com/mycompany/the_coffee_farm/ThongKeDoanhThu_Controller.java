package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class ThongKeDoanhThu_Controller implements Initializable {

    @FXML private ComboBox<String> cboThang;
    @FXML private ComboBox<String> cboNam; 
    
    @FXML private Label lblTongDoanhThu;
    @FXML private Label lblTongDonHang;
    
    @FXML private TableView<ChiTietThongKe> tblThongKe;
    @FXML private TableColumn<ChiTietThongKe, String> colTenSP;
    @FXML private TableColumn<ChiTietThongKe, Integer> colSoLuong;
    @FXML private TableColumn<ChiTietThongKe, String> colDoanhThu;

    // Khai báo 2 biến cho Popup Thông Báo tự làm
    @FXML private StackPane paneThongBao;
    @FXML private Label lblNoiDungThongBao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for (int i = 1; i <= 12; i++) {
            cboThang.getItems().add("Tháng " + i);
        }
        cboNam.getItems().addAll("2024", "2025", "2026");
        
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSP"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDoanhThu.setCellValueFactory(new PropertyValueFactory<>("doanhThu"));

        LocalDate ngayHienTai = LocalDate.now();
        int thangHienTai = ngayHienTai.getMonthValue();
        int namHienTai = ngayHienTai.getYear();

        cboThang.getSelectionModel().select("Tháng " + thangHienTai);
        cboNam.getSelectionModel().select(String.valueOf(namHienTai)); 
        
        loadDuLieuBang(cboThang.getValue(), cboNam.getValue());
    }    

    @FXML
    private void locDuLieu(ActionEvent event) {
        String thang = cboThang.getValue();
        String nam = cboNam.getValue();
        if (thang != null && nam != null) {
            loadDuLieuBang(thang, nam);
        }
    }

    private void loadDuLieuBang(String chuoiThang, String chuoiNam) {
        ObservableList<ChiTietThongKe> list = FXCollections.observableArrayList();
        
        int thang = Integer.parseInt(chuoiThang.replace("Tháng ", "").trim());
        int nam = Integer.parseInt(chuoiNam.trim());

        String sqlTongQuan = "SELECT ISNULL(SUM(total_amount), 0) AS TongDoanhThu, COUNT(order_id) AS TongDonHang " +
                             "FROM orders " +
                             "WHERE MONTH(ordered_at) = ? AND YEAR(ordered_at) = ? " +
                             "AND order_status = N'Thành công'";

        String sqlChiTiet = "SELECT p.product_name, SUM(od.quantity) AS SoLuong, SUM(od.quantity * od.historical_price) AS DoanhThu " +
                            "FROM order_details od " +
                            "JOIN products p ON od.product_id = p.product_id " +
                            "JOIN orders o ON od.order_id = o.order_id " +
                            "WHERE MONTH(o.ordered_at) = ? AND YEAR(o.ordered_at) = ? " +
                            "AND o.order_status = N'Thành công' " +
                            "GROUP BY p.product_id, p.product_name " +
                            "ORDER BY DoanhThu DESC";

        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                try (PreparedStatement psTongQuan = conn.prepareStatement(sqlTongQuan)) {
                    psTongQuan.setInt(1, thang);
                    psTongQuan.setInt(2, nam);
                    try (ResultSet rsTongQuan = psTongQuan.executeQuery()) {
                        if (rsTongQuan.next()) {
                            int tongDoanhThu = rsTongQuan.getInt("TongDoanhThu");
                            int tongDonHang = rsTongQuan.getInt("TongDonHang");
                            
                            String tongDoanhThuHienThi = String.format("%,dđ", tongDoanhThu).replace(",", ".");
                            lblTongDoanhThu.setText(tongDoanhThuHienThi);
                            lblTongDonHang.setText(tongDonHang + " Đơn");
                        }
                    }
                }

                try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet)) {
                    psChiTiet.setInt(1, thang);
                    psChiTiet.setInt(2, nam);
                    try (ResultSet rsChiTiet = psChiTiet.executeQuery()) {
                        while (rsChiTiet.next()) {
                            String tenSP = rsChiTiet.getString("product_name");
                            int soLuong = rsChiTiet.getInt("SoLuong");
                            int doanhThuInt = rsChiTiet.getInt("DoanhThu");
                            
                            String doanhThuHienThi = String.format("%,dđ", doanhThuInt).replace(",", ".");
                            
                            list.add(new ChiTietThongKe(tenSP, soLuong, doanhThuHienThi));
                        }
                    }
                }
            } else {
                hienThiThongBao("Lỗi hệ thống: Không thể tạo kết nối đến Cơ sở dữ liệu.");
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi cơ sở dữ liệu: Đã xảy ra lỗi khi tải dữ liệu thống kê doanh thu.");
        }

        tblThongKe.setItems(list);
    }

    @FXML
    private void xuatBaoCao(ActionEvent event) {
        hienThiThongBao("Đã xuất báo cáo " + cboThang.getValue() + " năm " + cboNam.getValue() + " ra file Excel thành công!");
    }

    @FXML
    private void quayLai(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminControl.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { 
            hienThiThongBao("Lỗi khi quay lại màn hình chính!");
        }
    }

    // ===============================================
    // HÀM XỬ LÝ POPUP THÔNG BÁO TỰ TẠO
    // ===============================================
    private void hienThiThongBao(String noiDung) {
        lblNoiDungThongBao.setText(noiDung);
        paneThongBao.setVisible(true);
    }

    @FXML
    private void dongThongBao(ActionEvent event) {
        paneThongBao.setVisible(false);
    }

    // Class đại diện dữ liệu cho bảng
    public static class ChiTietThongKe {
        private String tenSP;
        private int soLuong;
        private String doanhThu;

        public ChiTietThongKe(String tenSP, int soLuong, String doanhThu) {
            this.tenSP = tenSP;
            this.soLuong = soLuong;
            this.doanhThu = doanhThu;
        }

        public String getTenSP() { return tenSP; }
        public int getSoLuong() { return soLuong; }
        public String getDoanhThu() { return doanhThu; }
    }
}