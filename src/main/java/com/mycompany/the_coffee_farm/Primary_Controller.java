package com.mycompany.the_coffee_farm;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.shape.Rectangle;

public class Primary_Controller implements Initializable {

    @FXML
    private ImageView bannerImage;

    private int imageIndex = 0;

    private String[] danhSachAnh = {
        "/com/mycompany/the_coffee_farm/images/anh1.png",
        "/com/mycompany/the_coffee_farm/images/anh2.png",
        "/com/mycompany/the_coffee_farm/images/anh3.png",
        "/com/mycompany/the_coffee_farm/images/anh4.png"
    };

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Rectangle khuonBoGoc = new Rectangle(350, 200);
        khuonBoGoc.setArcWidth(30);
        khuonBoGoc.setArcHeight(30);
        bannerImage.setClip(khuonBoGoc);
        try {
            bannerImage.setImage(new Image(getClass().getResourceAsStream(danhSachAnh[0])));

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
                imageIndex++;
                if (imageIndex >= danhSachAnh.length) {
                    imageIndex = 0;
                }
                try {
                    bannerImage.setImage(new Image(getClass().getResourceAsStream(danhSachAnh[imageIndex])));
                } catch (Exception e) {
                }
            }));

            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();
        } catch (Exception e) {
            System.out.println("Lỗi đường dẫn ảnh!");
        }
    }

    @FXML
    public void chuyenTrangThongTin(ActionEvent event) {
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Parent trangThongTin = javafx.fxml.FXMLLoader.load(getClass().getResource("ThongTinDoanhNghiep.fxml"));
            javafx.scene.Scene sceneMoi = new javafx.scene.Scene(trangThongTin);
            stage.setScene(sceneMoi);
        } catch (IOException e) {
            System.out.println("Lỗi không load được file thông tin!");
        }
    }

    @FXML
    public void vaoNguyenLieuTho(javafx.event.ActionEvent event) {
        try {
            ThanhChucNang_Controller.trangChuCuaNhanh = "NguyenLieuTho.fxml";

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.layout.StackPane voChinh = javafx.fxml.FXMLLoader.load(getClass().getResource("ThanhChucNang.fxml"));
            javafx.scene.Parent ruotNLT = javafx.fxml.FXMLLoader.load(getClass().getResource("NguyenLieuTho.fxml"));
            javafx.scene.layout.BorderPane khungChinh = (javafx.scene.layout.BorderPane) voChinh.lookup("#khungChinh");

            khungChinh.setCenter(ruotNLT);
            stage.setScene(new javafx.scene.Scene(voChinh));
        } catch (Exception e) {
            System.out.println("Lỗi vào Nguyên liệu thô!");
            e.printStackTrace();
        }
    }

    @FXML
    public void vaoNguyenLieuSoChe(javafx.event.ActionEvent event) {
        try {
            ThanhChucNang_Controller.trangChuCuaNhanh = "NguyenLieuSoChe.fxml";

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.layout.StackPane voChinh = javafx.fxml.FXMLLoader.load(getClass().getResource("ThanhChucNang.fxml"));
            javafx.scene.Parent ruotNLSC = javafx.fxml.FXMLLoader.load(getClass().getResource("NguyenLieuSoChe.fxml"));
            javafx.scene.layout.BorderPane khungChinh = (javafx.scene.layout.BorderPane) voChinh.lookup("#khungChinh");

            khungChinh.setCenter(ruotNLSC);
            stage.setScene(new javafx.scene.Scene(voChinh, 500, 700));
            stage.setResizable(false);
        } catch (Exception e) {
            System.out.println("Lỗi vào Nguyên liệu sơ chế!");
            e.printStackTrace();
        }
    }

    @FXML
    public void vaoFoodAndDrink(javafx.event.ActionEvent event) {
        try {
            ThanhChucNang_Controller.trangChuCuaNhanh = "FoodAndDrink.fxml";

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.layout.StackPane voChinh = javafx.fxml.FXMLLoader.load(getClass().getResource("ThanhChucNang.fxml"));
            javafx.scene.Parent ruotFood = javafx.fxml.FXMLLoader.load(getClass().getResource("FoodAndDrink.fxml"));
            javafx.scene.layout.BorderPane khungChinh = (javafx.scene.layout.BorderPane) voChinh.lookup("#khungChinh");

            khungChinh.setCenter(ruotFood);
            stage.setScene(new javafx.scene.Scene(voChinh, 500, 700));
            stage.setResizable(false);
        } catch (Exception e) {
            System.out.println("Lỗi vào Food and Drink!");
            e.printStackTrace();
        }
    }

    @FXML
    public void moTrangDangNhap(javafx.event.ActionEvent event) {
        try {

            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("DangNhap.fxml"));
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.out.println("Lỗi không load được trang Đăng Nhập!");
            e.printStackTrace();
        }
    }

    @FXML
    public void clickVaoIconTaiKhoan(javafx.event.ActionEvent event) {
        try {
            String trangCanMo = TaiKhoan.daDangNhap ? "DaDangNhap.fxml" : "DangNhap.fxml";

            javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource(trangCanMo));
            javafx.stage.Stage stage = (javafx.stage.Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
