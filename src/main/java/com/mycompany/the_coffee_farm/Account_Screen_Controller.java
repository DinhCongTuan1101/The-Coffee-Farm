package com.mycompany.the_coffee_farm;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class Account_Screen_Controller implements Initializable {

    @FXML
    private javafx.scene.Node btnLichSu;
    @FXML
    private javafx.scene.Node btnTimKiem;
    @FXML
    private javafx.scene.Node btnUuDai;
    @FXML
    private javafx.scene.Node btnDanhGia;
    @FXML
    private javafx.scene.Node btnThongTin;
    @FXML
    private javafx.scene.Node btnDiaChi;
    @FXML
    private javafx.scene.Node btnDangXuat;

    @FXML
    private javafx.scene.layout.AnchorPane lopPhuDangXuat;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        boolean chuaLogin = !TaiKhoan.daDangNhap;

        if (btnLichSu != null) {
            btnLichSu.setDisable(chuaLogin);
        }
        if (btnTimKiem != null) {
            btnTimKiem.setDisable(chuaLogin);
        }
        if (btnUuDai != null) {
            btnUuDai.setDisable(chuaLogin);
        }
        if (btnDanhGia != null) {
            btnDanhGia.setDisable(chuaLogin);
        }
        if (btnThongTin != null) {
            btnThongTin.setDisable(chuaLogin);
        }
        if (btnDiaChi != null) {
            btnDiaChi.setDisable(chuaLogin);
        }
        if (btnDangXuat != null) {
            btnDangXuat.setDisable(chuaLogin);
        }

        if (lopPhuDangXuat != null) {
            lopPhuDangXuat.setVisible(false);
        }
    }

    @FXML
    public void xuLyDangXuat(javafx.event.ActionEvent event) {
        if (!TaiKhoan.daDangNhap) {
            return;
        }

        TaiKhoan.daDangNhap = false;
        TaiKhoan.id = 0;
        TaiKhoan.tenTaiKhoan = "";
        TaiKhoan.gioHangChung.clear();
        TaiKhoan.phuongThucNhan = 0;

        if (lopPhuDangXuat != null) {
            lopPhuDangXuat.setVisible(true);
        }
    }

    @FXML
    public void chuyenVePrimary(javafx.scene.input.MouseEvent event) {
        try {
            if (lopPhuDangXuat != null) {
                lopPhuDangXuat.setVisible(false);
            }

            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("primary.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void moDieuKhoan(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DieuKhoan.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void moLienHeVaGopY(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("LienHeVaGopY.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
            TaiKhoan.trangTruocDo = "Account_Screen.fxml";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void moLichSuDonHang(javafx.event.ActionEvent event) {

        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("LichsudonHang.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void moVeChungToi(javafx.event.ActionEvent event) {

        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("VeChungToi.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void moTimKiemDonHang(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("TimKiemDonHang.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void moUuDaiCuaBan(javafx.event.ActionEvent event) {
    }

    @FXML
    public void quayLaiAccount(javafx.event.ActionEvent event) {
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

    @FXML
    public void moDiaChiDaLuu(javafx.event.ActionEvent event) {
        try {
            ThanhChucNang_Controller.isFromCheckout = false;
            javafx.scene.Parent ruotDiaChi = javafx.fxml.FXMLLoader.load(getClass().getResource("DanhSachDiaChi.fxml"));
            javafx.scene.Scene scene = ((javafx.scene.Node) event.getSource()).getScene();
            javafx.scene.layout.BorderPane khungChinh = (javafx.scene.layout.BorderPane) scene.lookup("#khungChinh");
            if (khungChinh != null) {
                khungChinh.setCenter(ruotDiaChi);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static javafx.scene.Scene sceneAccountGoc;

    @FXML
    public void moThongTinCaNhan(javafx.event.ActionEvent event) {
        try {
            sceneAccountGoc = ((javafx.scene.Node) event.getSource()).getScene();
            javafx.scene.Parent ruotThongTin = javafx.fxml.FXMLLoader.load(getClass().getResource("ThongTinCaNhan.fxml"));
            javafx.stage.Stage stage = (javafx.stage.Stage) sceneAccountGoc.getWindow();
            stage.setScene(new javafx.scene.Scene(ruotThongTin));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
