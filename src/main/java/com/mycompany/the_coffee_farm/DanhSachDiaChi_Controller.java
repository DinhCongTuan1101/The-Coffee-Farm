package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class DanhSachDiaChi_Controller implements Initializable {

    @FXML
    private VBox vboxDanhSach;
    @FXML
    private Label txtTieuDe;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane paneXacNhan;

    public static DanhSachDiaChi_Controller instance;

    private List<RadioButton> danhSachRadio = new ArrayList<>();
    private String tenDaChon = "";
    private String sdtDaChon = "";
    private String diaChiDaChon = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        instance = this;
        loadDanhSachDiaChi();
    }

    private void loadDanhSachDiaChi() {
        vboxDanhSach.getChildren().clear();
        danhSachRadio.clear();

        boolean isCheckout = ThanhChucNang_Controller.isFromCheckout;

        if (!isCheckout) {
            txtTieuDe.setText("Thông tin địa chỉ");
            paneXacNhan.setVisible(false);
            AnchorPane.setBottomAnchor(scrollPane, 0.0);
        } else {
            txtTieuDe.setText("Chọn địa chỉ nhận hàng");
            paneXacNhan.setVisible(true);
            AnchorPane.setBottomAnchor(scrollPane, 80.0);
        }

        String sql = "SELECT * FROM user_addresses WHERE user_id = ? ORDER BY is_default DESC, address_id DESC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, TaiKhoan.id);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String ten = rs.getString("receiver_name");
                String sdt = rs.getString("receiver_phone");
                String diaChi = rs.getString("detail_address");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("DiaChiDaLuu.fxml"));
                AnchorPane theDiaChi = loader.load();

                DiaChiDaLuu_Controller controller = loader.getController();
                controller.setData(ten, sdt, diaChi);
                controller.setCheDoHienThi(isCheckout);

                danhSachRadio.add(controller.getRadioButton());
                vboxDanhSach.getChildren().add(theDiaChi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDiaChiDangChon(String ten, String sdt, String diaChi, RadioButton rdoDuocChon) {
        for (RadioButton rdo : danhSachRadio) {
            if (rdo != rdoDuocChon) {
                rdo.setSelected(false);
            }
        }
        this.tenDaChon = ten;
        this.sdtDaChon = sdt;
        this.diaChiDaChon = diaChi;
    }

    @FXML
    public void xacNhanChon(ActionEvent event) {
        if (tenDaChon.isEmpty()) {
            System.out.println("Bạn chưa chọn địa chỉ nào!");
            return;
        }
        if (ThanhChucNang_Controller.isFromCheckout) {
            if (ThanhChucNang_Controller.instance != null) {
                ThanhChucNang_Controller.instance.nhanDiaChiDaChon(tenDaChon, sdtDaChon, diaChiDaChon);
            }
        }
    }

    @FXML
    public void quayLai(ActionEvent event) {
        try {
            if (ThanhChucNang_Controller.isFromCheckout) {
                if (ThanhChucNang_Controller.instance != null) {
                    ThanhChucNang_Controller.instance.quayLaiTuDanhSachDiaChi();
                }
            } else {
                javafx.scene.Parent ruotAccount = javafx.fxml.FXMLLoader.load(getClass().getResource("Account_Screen.fxml"));
                javafx.scene.Scene scene = ((javafx.scene.Node) event.getSource()).getScene();
                javafx.scene.layout.BorderPane khungChinh = (javafx.scene.layout.BorderPane) scene.lookup("#khungChinh");
                if (khungChinh != null) {
                    khungChinh.setCenter(ruotAccount);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
