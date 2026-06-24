package com.mycompany.the_coffee_farm;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import database.DBConnection;

public class QuanLyTaiKhoan_Controller implements Initializable {

    @FXML
    private TableView<TaiKhoanItem> tblTaiKhoan;
    @FXML
    private TableColumn<TaiKhoanItem, String> colId;
    @FXML
    private TableColumn<TaiKhoanItem, String> colSdt;
    @FXML
    private TableColumn<TaiKhoanItem, String> colTen;
    @FXML
    private TableColumn<TaiKhoanItem, String> colTrangThai;

    @FXML
    private TextField txtDisplayTen;
    @FXML
    private TextField txtDisplaySdt;
    @FXML
    private Button btnKhoa;

    @FXML
    private StackPane paneThemTK;
    @FXML
    private TextField txtTenMoi;
    @FXML
    private TextField txtSdtMoi;
    @FXML
    private TextField txtEmailMoi;
    @FXML
    private TextField txtMatKhauMoi;
    @FXML
    private TextField txtDiaChiMoi;

    @FXML
    private StackPane paneThongBao;
    @FXML
    private Label lblNoiDungThongBao;

    private TaiKhoanItem taiKhoanDangChon = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        colId.setCellValueFactory(cellData -> {
            int rawId = Integer.parseInt(cellData.getValue().getId().trim());
            return new javafx.beans.property.SimpleStringProperty(String.format("TCF-TK%04d", rawId));
        });

        colSdt.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSdt()));
        colTen.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTen()));
        colTrangThai.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTrangThai()));

        loadDataLenBang();

        tblTaiKhoan.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && tblTaiKhoan.getSelectionModel().getSelectedItem() != null) {
                taiKhoanDangChon = tblTaiKhoan.getSelectionModel().getSelectedItem();

                txtDisplayTen.setText(taiKhoanDangChon.getTen());
                txtDisplaySdt.setText(taiKhoanDangChon.getSdt());

                if (taiKhoanDangChon.getTrangThai().equals("Đã khóa")) {
                    btnKhoa.setText("Mở Khóa Tài Khoản");
                    btnKhoa.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8; -fx-cursor: hand;");
                } else {
                    btnKhoa.setText("Khóa Tài Khoản");
                    btnKhoa.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14; -fx-background-radius: 8; -fx-cursor: hand;");
                }
            }
        });
    }

    private void loadDataLenBang() {
        new Thread(() -> {
            ObservableList<TaiKhoanItem> danhSach = DBConnection.layDanhSachTaiKhoanAdmin();
            javafx.application.Platform.runLater(() -> tblTaiKhoan.setItems(danhSach));
        }).start();
    }

    @FXML
    private void themTaiKhoan(ActionEvent event) {
        paneThemTK.setVisible(true);
    }

    @FXML
    private void dongPopupThem(ActionEvent event) {
        paneThemTK.setVisible(false);
        txtTenMoi.clear();
        txtSdtMoi.clear();
        txtEmailMoi.clear();
        txtMatKhauMoi.clear();
        txtDiaChiMoi.clear();
    }

    @FXML
    private void luuTaiKhoanMoi(ActionEvent event) {
        String ten = txtTenMoi.getText().trim();
        String sdt = txtSdtMoi.getText().trim();
        String email = txtEmailMoi.getText().trim();
        String pass = txtMatKhauMoi.getText().trim();
        String diaChi = txtDiaChiMoi.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty() || email.isEmpty() || pass.isEmpty() || diaChi.isEmpty()) {
            hienThiThongBao("Vui lòng điền ĐỦ các thông tin!");
            return;
        }

        try {
            String matKhauHash = hashPasswordSHA256(pass);
            if (DBConnection.taoTaiKhoanAdmin(ten, sdt, email, matKhauHash, diaChi)) {
                dongPopupThem(null);
                hienThiThongBao("Thành công: Đã tạo tài khoản mới!");
                loadDataLenBang();
            } else {
                hienThiThongBao("Lỗi: SĐT hoặc Email đã được sử dụng!");
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi hệ thống!");
        }
    }

    @FXML
    private void khoaTaiKhoan(ActionEvent event) {
        if (taiKhoanDangChon == null) {
            return;
        }
        boolean dangBiKhoa = taiKhoanDangChon.getTrangThai().equals("Đã khóa");
        if (DBConnection.khoaHoacMoTaiKhoanAdmin(taiKhoanDangChon.getId(), !dangBiKhoa)) {
            hienThiThongBao(dangBiKhoa ? "Đã MỞ KHÓA tài khoản!" : "Đã KHÓA tài khoản thành công!");
            loadDataLenBang();
            lamSachDisplay();
        }
    }

    @FXML
    private void xoaTaiKhoan(ActionEvent event) {
        if (taiKhoanDangChon == null) {
            return;
        }
        if (DBConnection.xoaTaiKhoanAdmin(taiKhoanDangChon.getId())) {
            hienThiThongBao("Thành công: Đã xóa tài khoản vĩnh viễn!");
            loadDataLenBang();
            lamSachDisplay();
        }
    }

    @FXML
    private void quayLai(ActionEvent event) throws Exception {
        Stage s = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        s.setScene(new Scene(FXMLLoader.load(getClass().getResource("AdminControl.fxml"))));
    }

    private void hienThiThongBao(String noiDung) {
        lblNoiDungThongBao.setText(noiDung);
        paneThongBao.setVisible(true);
    }

    @FXML
    private void dongThongBao(ActionEvent event) {
        paneThongBao.setVisible(false);
    }

    private void lamSachDisplay() {
        txtDisplayTen.clear();
        txtDisplaySdt.clear();
        taiKhoanDangChon = null;
        btnKhoa.setText("🔒 Khóa Tài Khoản");
    }

    private String hashPasswordSHA256(String password) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static class TaiKhoanItem {

        private String id, sdt, ten, trangThai;

        public TaiKhoanItem(String id, String sdt, String ten, String trangThai) {
            this.id = id;
            this.sdt = sdt;
            this.ten = ten;
            this.trangThai = trangThai;
        }

        public String getId() {
            return id;
        }

        public String getSdt() {
            return sdt;
        }

        public String getTen() {
            return ten;
        }

        public String getTrangThai() {
            return trangThai;
        }
    }
}
