package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class ThanhChucNang_Controller {

    @FXML
    private javafx.scene.layout.BorderPane khungChinh;

    @FXML
    private javafx.scene.control.Button btnShippingMethod;

    @FXML
    private javafx.scene.layout.AnchorPane lopPhuShippingMethod;

    @FXML
    private javafx.scene.layout.VBox vboxChonPhuongThuc;
    @FXML
    private javafx.scene.layout.VBox vboxNhapThongTin;

    @FXML
    private javafx.scene.control.RadioButton rdoGiaoTanNoi;
    @FXML
    private javafx.scene.control.RadioButton rdoLayMangDi;
    @FXML
    private javafx.scene.control.RadioButton rdoDungTaiQuan;

    @FXML
    private javafx.scene.control.TextField txtHoTenNhan;
    @FXML
    private javafx.scene.control.TextField txtSDTNhan;
    @FXML
    private javafx.scene.control.TextField txtDiaChiNhan;

    public static String trangChuCuaNhanh = "FoodAndDrink.fxml";

    @FXML
    private void bamNutHome(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent ruotNhanh = javafx.fxml.FXMLLoader.load(getClass().getResource(trangChuCuaNhanh));
            if (khungChinh != null) {
                khungChinh.setCenter(ruotNhanh);
            } else {
                System.out.println("Lỗi: Chưa đặt fx:id là 'khungChinh' cho BorderPane bên Scene Builder!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void bamNutShippingMethod(javafx.event.ActionEvent event) {
        if (lopPhuShippingMethod != null) {
            lopPhuShippingMethod.setVisible(true);
        } else {
            return;
        }
    }

    @FXML
    public void bamNutAccount(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent ruotAccount = javafx.fxml.FXMLLoader.load(getClass().getResource("Account_Screen.fxml"));
            if (khungChinh != null) {
                khungChinh.setCenter(ruotAccount);
            } else {
                System.out.println("Lỗi: Chưa đặt fx:id là 'khungChinh' cho BorderPane bên Scene Builder!");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            e.printStackTrace();
        }
    }

    @FXML
    public void dongPopupShipping(ActionEvent event) {
        if (lopPhuShippingMethod != null) {
            lopPhuShippingMethod.setVisible(false);
        }
    }

    @FXML
    public void chonGiaoHang(javafx.event.ActionEvent event) {
        if (rdoGiaoTanNoi.isSelected()) {
            TaiKhoan.phuongThucNhan = 1;
            if (vboxChonPhuongThuc != null) {
                vboxChonPhuongThuc.setVisible(false);
            }
            if (vboxNhapThongTin != null) {
                vboxNhapThongTin.setVisible(true);
            }
        }
    }

    @FXML
    public void chonMangDi(javafx.event.ActionEvent event) {
        if (rdoLayMangDi.isSelected()) {
            TaiKhoan.phuongThucNhan = 2;
            if (btnShippingMethod != null) {
                btnShippingMethod.setText("Mang đi");
            }
            dongPopupShipping(event);
        }
    }

    @FXML
    public void chonTaiQuan(javafx.event.ActionEvent event) {
        if (rdoDungTaiQuan.isSelected()) {
            TaiKhoan.phuongThucNhan = 3;
            if (btnShippingMethod != null) {
                btnShippingMethod.setText("Tại quán");
            }
            dongPopupShipping(event);
        }
    }

    @FXML
    public void huyNhapThongTin(javafx.event.ActionEvent event) {
        if (vboxNhapThongTin != null) {
            vboxNhapThongTin.setVisible(false);
        }
        if (vboxChonPhuongThuc != null) {
            vboxChonPhuongThuc.setVisible(true);
        }
        if (rdoGiaoTanNoi != null) {
            rdoGiaoTanNoi.setSelected(false);
        }
    }

    @FXML
    public void xacNhanGiaoHang(javafx.event.ActionEvent event) {
        String ten = txtHoTenNhan.getText().trim();
        String sdt = txtSDTNhan.getText().trim();
        String diaChi = txtDiaChiNhan.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty() || diaChi.isEmpty()) {
            return;
        }

        tuDongLuuDiaChi(TaiKhoan.id, ten, sdt, diaChi);

        if (btnShippingMethod != null) {
            btnShippingMethod.setText("Giao tận nơi");
        }
        dongPopupShipping(event);
    }

    private void tuDongLuuDiaChi(int userId, String tenNguoiNhan, String sdt, String diaChi) {
        String sqlCheck = "SELECT COUNT(*) FROM user_addresses WHERE user_id = ? AND detail_address = ?";
        String sqlInsert = "INSERT INTO user_addresses (user_id, receiver_name, receiver_phone, detail_address) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
            psCheck.setInt(1, userId);
            psCheck.setString(2, diaChi);
            ResultSet rs = psCheck.executeQuery();

            if (rs.next() && rs.getInt(1) == 0) {
                PreparedStatement psInsert = conn.prepareStatement(sqlInsert);
                psInsert.setInt(1, userId);
                psInsert.setString(2, tenNguoiNhan);
                psInsert.setString(3, sdt);
                psInsert.setString(4, diaChi);
                psInsert.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isFromCheckout = false;

    @FXML
    public void moTrangChonDiaChi(ActionEvent event) {
        try {
            isFromCheckout = true;
            dongPopupShipping(event);
            Parent ruotChonDiaChi = FXMLLoader.load(getClass().getResource("DanhSachDiaChi.fxml"));
            if (khungChinh != null) {
                khungChinh.setCenter(ruotChonDiaChi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void moManHinhGioHang(javafx.event.ActionEvent event) {
        try {
            TaiKhoan.sceneTruocKhiVaoGio = ((javafx.scene.Node) event.getSource()).getScene();
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("Order_Screen.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ThanhChucNang_Controller instance;

    @FXML
    public void initialize() {
        instance = this;
    }

    public void nhanDiaChiDaChon(String ten, String sdt, String diaChi) {
        try {

            javafx.scene.Parent ruotNhanh = javafx.fxml.FXMLLoader.load(getClass().getResource(trangChuCuaNhanh));
            if (khungChinh != null) {
                khungChinh.setCenter(ruotNhanh);
            }

            lopPhuShippingMethod.setVisible(true);
            vboxChonPhuongThuc.setVisible(false);
            vboxNhapThongTin.setVisible(true);

            txtHoTenNhan.setText(ten);
            txtSDTNhan.setText(sdt);
            txtDiaChiNhan.setText(diaChi);

            rdoGiaoTanNoi.setSelected(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void quayLaiTuDanhSachDiaChi() {
        try {
            javafx.scene.Parent ruotNhanh = javafx.fxml.FXMLLoader.load(getClass().getResource(trangChuCuaNhanh));
            if (khungChinh != null) {
                khungChinh.setCenter(ruotNhanh);
            }
            lopPhuShippingMethod.setVisible(true);
            vboxChonPhuongThuc.setVisible(false);
            vboxNhapThongTin.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
