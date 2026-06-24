package com.mycompany.the_coffee_farm;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FoodAndDrink_Controller implements Initializable {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
    
    @FXML private Button btnBack;
    @FXML private VBox vboxDoAn;
    @FXML private VBox vboxDoUong;
    @FXML private TextField txtTimMon;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        vboxDoAn.setVisible(true);
        vboxDoAn.setManaged(true);
        vboxDoUong.setVisible(false);
        vboxDoUong.setManaged(false);
        
        taiDanhSachTuDB();
    }

    private void taiDanhSachTuDB() {
        new Thread(() -> {
            ObservableList<SanPham> danhSach = database.DBConnection.layDanhSachSanPhamAdmin();
            Platform.runLater(() -> {
                vboxDoAn.getChildren().clear();
                vboxDoUong.getChildren().clear();
                
                for (SanPham sp : danhSach) {
                    if (sp.getIdLoai() == 3) { 
                        if(sp.getTenSanPham().toLowerCase().contains("bánh")) {
                            vboxDoAn.getChildren().add(taoTheMonAn(sp));
                        } else {
                            vboxDoUong.getChildren().add(taoTheMonAn(sp));
                        }
                    }
                }
            });
        }).start();
    }

    private AnchorPane taoTheMonAn(SanPham sp) {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(460, 110);
        pane.setStyle("-fx-background-radius: 15; -fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.06), 10, 0, 2, 4);");

        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(90, 90);
        rect.setLayoutX(8);
        rect.setLayoutY(10);
        rect.setArcWidth(20);
        rect.setArcHeight(20);
        rect.fillProperty().set(javafx.scene.paint.Color.web("#EAEAEA"));
        rect.setStroke(javafx.scene.paint.Color.TRANSPARENT);

        ImageView img = new ImageView();
        img.setFitWidth(90);
        img.setFitHeight(90);
        img.setLayoutX(8);
        img.setLayoutY(10);
        img.setPickOnBounds(true);
        img.setPreserveRatio(true);
        try {
            URL imgUrl = getClass().getResource(sp.getImageUrl());
            if(imgUrl != null) img.setImage(new Image(imgUrl.toExternalForm()));
        } catch(Exception e) {}

        Label lblTen = new Label(sp.getTenSanPham());
        lblTen.setLayoutX(115);
        lblTen.setLayoutY(15);
        lblTen.setPrefSize(290, 45);
        lblTen.setWrapText(true);
        lblTen.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        String chuoiGiaFormat = String.format("%,.0f", sp.getGiaBan()).replace(",", ".") + "đ/" + sp.getDonViTinh();
        Label lblGia = new Label(chuoiGiaFormat);
        lblGia.setLayoutX(115);
        lblGia.setLayoutY(70);
        lblGia.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #888888;");

        Button btnThem = new Button("+");
        btnThem.setLayoutX(415);
        btnThem.setLayoutY(65);
        btnThem.setPrefSize(28, 28);
        btnThem.setStyle("-fx-background-color: #00cec9; -fx-background-radius: 14; -fx-cursor: hand; -fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white; -fx-padding: 0;");
        
        btnThem.setOnAction(e -> {
            themVaoGio(sp.getTenSanPham(), (int)sp.getGiaBan());
        });

        pane.getChildren().addAll(rect, img, lblTen, lblGia, btnThem);
        return pane;
    }

    private void themVaoGio(String tenMonFinal, int giaTienFinal) {
        if (TaiKhoan.gioHangChung.containsKey(tenMonFinal)) {
            TaiKhoan.gioHangChung.get(tenMonFinal)[0] += 1;
        } else {
            TaiKhoan.gioHangChung.put(tenMonFinal, new int[]{1, giaTienFinal});
        }
        if (TaiKhoan.daDangNhap && TaiKhoan.id > 0) {
            threadPool.execute(() -> database.DBConnection.themSanPhamVaoGioHangDB(TaiKhoan.id, tenMonFinal));
        }
    }

    @FXML
    private void showDoAn(ActionEvent event) {
        vboxDoAn.setVisible(true);
        vboxDoAn.setManaged(true);
        vboxDoUong.setVisible(false);
        vboxDoUong.setManaged(false);
    }

    @FXML
    private void showDoUong(ActionEvent event) {
        vboxDoAn.setVisible(false);
        vboxDoAn.setManaged(false);
        vboxDoUong.setVisible(true);
        vboxDoUong.setManaged(true);
    }

    @FXML
    private void timKiem(KeyEvent event) {
        String tuKhoa = txtTimMon.getText().toLowerCase().trim();
        locDanhSach(vboxDoAn, tuKhoa);
        locDanhSach(vboxDoUong, tuKhoa);
    }

    private void locDanhSach(VBox danhSach, String tuKhoa) {
        for (javafx.scene.Node node : danhSach.getChildren()) {
            if (node instanceof AnchorPane) {
                AnchorPane theMon = (AnchorPane) node;
                boolean giuLai = false;
                for (javafx.scene.Node thanhPhan : theMon.getChildren()) {
                    if (thanhPhan instanceof Label) {
                        Label lbl = (Label) thanhPhan;
                        if (lbl.getText().toLowerCase().contains(tuKhoa)) {
                            giuLai = true;
                            break;
                        }
                    }
                }
                theMon.setVisible(giuLai);
                theMon.setManaged(giuLai);
            }
        }
    }

    @FXML
    public void veTrangChu(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rooPrimary = FXMLLoader.load(getClass().getResource("primary.fxml"));
            stage.setScene(new Scene(rooPrimary));
        } catch (Exception e) { e.printStackTrace(); }
    }
}