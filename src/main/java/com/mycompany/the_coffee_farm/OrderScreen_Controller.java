package com.mycompany.the_coffee_farm; // GIỮ NGUYÊN DÒNG NÀY CỦA M NHÉ, ĐỪNG XÓA

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class OrderScreen_Controller {
           @FXML
    private Button btnBack;
    
        @FXML
    public void veTrangChu(ActionEvent event){
        try{
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent rooPrimary = FXMLLoader.load(getClass().getResource("primary.fxml"));
            Scene sceneMoi = new Scene(rooPrimary);
            stage.setScene(sceneMoi);
        } catch(Exception e){
            System.out.println("Lỗi không quay lại được trang trước!");
            e.printStackTrace();
        }
    }

    @FXML
    private VBox vboxGioHang;
    @FXML
    private Label lblTongTien;
    @FXML
    private CheckBox chkTatCa;
    @FXML
    private Button btnMuaHang;

    private List<CheckBox> listTickBox = new ArrayList<>();

    @FXML
    public void initialize() {
        vboxGioHang.setSpacing(10);
        vboxGioHang.setStyle("-fx-padding: 10;");
        hienThiDanhSachGioHang();
    }

    private void hienThiDanhSachGioHang() {
        vboxGioHang.getChildren().clear();
        listTickBox.clear();

        for (GioHang.MonHang mh : GioHang.danhSachMua) {
            HBox theMon = new HBox(20);
            theMon.setAlignment(Pos.CENTER_LEFT);
            theMon.setPrefHeight(60);
            theMon.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 5; -fx-padding: 0 15 0 15; -fx-border-color: #F0F0F0; -fx-border-width: 0 0 1 0;");

            CheckBox chkItem = new CheckBox();
            chkItem.setUserData(mh);
            chkItem.setOnAction(e -> {
                tinhTienRealTime();
                kiemTraNutTatCa();
            });
            listTickBox.add(chkItem);

            Label lblTen = new Label(mh.tenMon);
            lblTen.setPrefWidth(220);
            lblTen.setWrapText(true);
            lblTen.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

            Label lblSL = new Label("x" + mh.soLuong);
            lblSL.setPrefWidth(50);
            lblSL.setAlignment(Pos.CENTER);
            lblSL.setStyle("-fx-text-fill: #777777; -fx-font-size: 13px;");

            int thanhTien = mh.giaTien * mh.soLuong;
            Label lblTien = new Label(thanhTien + "đ");
            lblTien.setPrefWidth(100);
            lblTien.setAlignment(Pos.CENTER_RIGHT);
            lblTien.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 14px;");

            theMon.getChildren().addAll(chkItem, lblTen, lblSL, lblTien);
            vboxGioHang.getChildren().add(theMon);
        }
        tinhTienRealTime();
    }

    private void tinhTienRealTime() {
        int tong = 0;
        int soMonDaChon = 0;
        for (CheckBox chk : listTickBox) {
            if (chk.isSelected()) {
                GioHang.MonHang mh = (GioHang.MonHang) chk.getUserData();
                tong += (mh.giaTien * mh.soLuong);
                soMonDaChon++;
            }
        }
        lblTongTien.setText(tong + "đ");
        btnMuaHang.setText("Mua hàng (" + soMonDaChon + ")");
    }

    @FXML
    private void handleChonTatCa(ActionEvent event) {
        boolean trangThaiTatCa = chkTatCa.isSelected();
        for (CheckBox chk : listTickBox) {
            chk.setSelected(trangThaiTatCa);
        }
        tinhTienRealTime();
    }

    private void kiemTraNutTatCa() {
        boolean fullTick = true;
        for (CheckBox chk : listTickBox) {
            if (!chk.isSelected()) {
                fullTick = false;
                break;
            }
        }
        chkTatCa.setSelected(fullTick && !listTickBox.isEmpty());
    }
}