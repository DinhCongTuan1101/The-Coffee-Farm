package com.mycompany.the_coffee_farm;

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
    // -----------------------------------

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
            System.out.println("Lỗi đéo về được nhánh chính: " + trangChuCuaNhanh);
            e.printStackTrace();
        }
    }

    @FXML
    public void bamNutShippingMethod(javafx.event.ActionEvent event) {
        if (lopPhuShippingMethod != null) {
            lopPhuShippingMethod.setVisible(true);
        } else {
            System.out.println("Lỗi: Chưa nối ID lớp phủ bên Scene Builder!");
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
            }

        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy hoặc không load được file Account_Screen.fxml!");
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
            System.out.println("Lỗi không quay lại được trang trước!");
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
            System.out.println("Bạn đã chọn: Đến lấy mang đi");
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
            System.out.println("Bạn đã chọn: Dùng tại quán");
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
        System.out.println("Giao đến: " + txtHoTenNhan.getText() + " - " + txtDiaChiNhan.getText());
        if (btnShippingMethod != null) {
            btnShippingMethod.setText("Giao tận nơi");
        }
        dongPopupShipping(event);
    }
    // ----------------------------------------------

    @FXML
    private void moManHinhGioHang(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("Order_Screen.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi không chuyển được trang!");
            e.printStackTrace();
        }
    }

}
