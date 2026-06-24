package com.mycompany.the_coffee_farm;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboThang.getItems().addAll("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                                   "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12");
        cboNam.getItems().addAll("2024", "2025", "2026");
                colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSP"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colDoanhThu.setCellValueFactory(new PropertyValueFactory<>("doanhThu"));

        cboThang.getSelectionModel().select("Tháng 1");
        cboNam.getSelectionModel().select("2026"); 
        
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

    private void loadDuLieuBang(String thang, String nam) {
        ObservableList<ChiTietThongKe> list = FXCollections.observableArrayList();
        
        if (thang.equals("Tháng 1") && nam.equals("2026")) {
            list.add(new ChiTietThongKe("Cà phê sữa đá", 120, "3.600.000"));
            list.add(new ChiTietThongKe("Bạc xỉu", 85, "2.975.000"));
            list.add(new ChiTietThongKe("Trà đen Macchiato", 60, "2.700.000"));
            lblTongDoanhThu.setText("9.275.000 đ");
            lblTongDonHang.setText("265 Đơn");
        } else {
            list.add(new ChiTietThongKe("Trà đào cam sả", 50, "2.250.000"));
            lblTongDoanhThu.setText("2.250.000 đ");
            lblTongDonHang.setText("50 Đơn");
        }
        
        tblThongKe.setItems(list);
    }

    @FXML
    private void xuatBaoCao(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText("Đã xuất báo cáo " + cboThang.getValue() + " năm " + cboNam.getValue() + " ra file Excel!");
        alert.showAndWait();
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