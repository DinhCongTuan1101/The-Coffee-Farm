package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class LichsudonHang_Controller implements Initializable {

    @FXML
    private VBox vboxDanhSachDonHang;

    @FXML
    private void quayLaiAccount(javafx.event.ActionEvent event) {
        try {
            Parent voChinh = FXMLLoader.load(getClass().getResource("ThanhChucNang.fxml"));
            Parent ruotAccount = FXMLLoader.load(getClass().getResource("Account_Screen.fxml"));

            BorderPane khungChinh = (BorderPane) voChinh.lookup("#khungChinh");
            khungChinh.setCenter(ruotAccount);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(voChinh));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int userIdHienTai = TaiKhoan.id;
        loadLichSuMuaHang(userIdHienTai);
    }

    private void loadLichSuMuaHang(int userId) {
        vboxDanhSachDonHang.getChildren().clear();

        String sql = "SELECT o.order_id, p.product_id, p.product_name, p.image_url, "
                + "od.quantity, od.historical_price, o.total_amount "
                + "FROM orders o "
                + "JOIN order_details od ON o.order_id = od.order_id "
                + "JOIN products p ON od.product_id = p.product_id "
                + "WHERE o.user_id = ? "
                + "ORDER BY o.ordered_at DESC";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int productId = rs.getInt("product_id");
                String tenSp = rs.getString("product_name");
                String hinhAnh = rs.getString("image_url");
                int quantity = rs.getInt("quantity");
                int historicalPrice = rs.getInt("historical_price");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemLichSu.fxml"));
                AnchorPane itemCard = loader.load();

                ImageView imgSanPham = (ImageView) itemCard.lookup("#imgSanPham");
                Label txtTenSP = (Label) itemCard.lookup("#txtTenSP");
                Label txtSoLuong = (Label) itemCard.lookup("#txtSoLuong");
                Label txtGiaTien = (Label) itemCard.lookup("#txtGiaTien");
                Label txtTongTien = (Label) itemCard.lookup("#txtTongTien");
                Button btnMuaLai = (Button) itemCard.lookup("#btnMuaLai");

                txtTenSP.setText(tenSp);
                txtSoLuong.setText("x" + quantity);
                txtGiaTien.setText(String.format("%,d", historicalPrice) + "đ");

                int thanhTienTungMon = quantity * historicalPrice;
                txtTongTien.setText(String.format("%,d", thanhTienTungMon) + "đ");

                try {
                    if (hinhAnh != null && !hinhAnh.trim().isEmpty()) {
                        Image img = new Image(getClass().getResourceAsStream(hinhAnh));
                        if (img.isError()) {
                            throw new Exception("Link ảnh hỏng");
                        }
                        imgSanPham.setImage(img);
                    } else {
                        throw new Exception("Không có link ảnh");
                    }
                } catch (Exception e) {
                    try {
                        imgSanPham.setImage(new Image(getClass().getResourceAsStream("images/anh1.png")));
                    } catch (Exception ex) {
                        System.out.println("Lỗi load ảnh");
                    }
                }

                btnMuaLai.setOnAction(event -> {
                    xuLyMuaLaiSanPham(userId, productId, quantity);

                    try {
                        if (TaiKhoan.gioHangChung != null) {
                            TaiKhoan.gioHangChung.clear();
                            TaiKhoan.gioHangChung.putAll(DBConnection.taiGioHangCuTuDB(userId));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Stage mainWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();

                    Stage popupStage = new Stage();
                    popupStage.initOwner(mainWindow);
                    popupStage.initModality(Modality.APPLICATION_MODAL);
                    popupStage.initStyle(StageStyle.TRANSPARENT);

                    javafx.scene.layout.StackPane overlay = new javafx.scene.layout.StackPane();
                    overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");

                    javafx.scene.layout.VBox dialog = new javafx.scene.layout.VBox(15);
                    dialog.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-padding: 30; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 20, 0, 0, 0);");

                    dialog.setMaxWidth(320);
                    dialog.setMaxHeight(javafx.scene.layout.Region.USE_PREF_SIZE);
                    dialog.setAlignment(javafx.geometry.Pos.CENTER);

                    ImageView icon = new ImageView();
                    try {
                        icon.setImage(new Image(getClass().getResourceAsStream("images/check.png")));
                        icon.setFitWidth(60);
                        icon.setFitHeight(60);
                    } catch (Exception e) {
                    }

                    Label message = new Label("Đã thêm " + tenSp + " vào giỏ hàng!");
                    message.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
                    message.setWrapText(true);
                    message.setMaxWidth(280);
                    message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

                    Label hint = new Label("(Chạm vào màn hình để tiếp tục)");
                    hint.setStyle("-fx-font-size: 13px; -fx-text-fill: #888888;");

                    dialog.getChildren().addAll(icon, message, hint);
                    overlay.getChildren().add(dialog);

                    Scene popupScene = new Scene(overlay, mainWindow.getWidth(), mainWindow.getHeight());
                    popupScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
                    popupStage.setScene(popupScene);

                    popupStage.setX(mainWindow.getX());
                    popupStage.setY(mainWindow.getY());
                    popupStage.show();

                    overlay.setOnMouseClicked(mouseEvent -> {
                        popupStage.close();
                        try {
                            Parent voChinh = FXMLLoader.load(getClass().getResource("ThanhChucNang.fxml"));
                            Parent ruotGioHang = FXMLLoader.load(getClass().getResource("Order_Screen.fxml"));

                            BorderPane khungChinh = (BorderPane) voChinh.lookup("#khungChinh");
                            khungChinh.setCenter(ruotGioHang);

                            mainWindow.setScene(new Scene(voChinh));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                });

                vboxDanhSachDonHang.getChildren().add(itemCard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xuLyMuaLaiSanPham(int userId, int productId, int quantity) {
        try (Connection conn = DBConnection.getConnection()) {
            int cartId = -1;
            String sqlCheckCart = "SELECT cart_id FROM carts WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckCart)) {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    cartId = rs.getInt("cart_id");
                }
            }

            if (cartId == -1) {
                String sqlCreateCart = "INSERT INTO carts (user_id, updated_at) VALUES (?, GETDATE())";
                try (PreparedStatement ps = conn.prepareStatement(sqlCreateCart, PreparedStatement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.executeUpdate();
                    ResultSet rsKeys = ps.getGeneratedKeys();
                    if (rsKeys.next()) {
                        cartId = rsKeys.getInt(1);
                    }
                }
            }

            String sqlCheckItem = "SELECT cart_detail_id, quantity FROM cart_details WHERE cart_id = ? AND product_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckItem)) {
                ps.setInt(1, cartId);
                ps.setInt(2, productId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int currentQty = rs.getInt("quantity");
                    int detailId = rs.getInt("cart_detail_id");
                    String sqlUpdateQty = "UPDATE cart_details SET quantity = ? WHERE cart_detail_id = ?";
                    try (PreparedStatement psUp = conn.prepareStatement(sqlUpdateQty)) {
                        psUp.setInt(1, currentQty + quantity);
                        psUp.setInt(2, detailId);
                        psUp.executeUpdate();
                    }
                } else {
                    String sqlInsertItem = "INSERT INTO cart_details (cart_id, product_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement psIns = conn.prepareStatement(sqlInsertItem)) {
                        psIns.setInt(1, cartId);
                        psIns.setInt(2, productId);
                        psIns.setInt(3, quantity);
                        psIns.executeUpdate();
                    }
                }
            }
            String sqlUpdateCartTime = "UPDATE carts SET updated_at = GETDATE() WHERE cart_id = ?";
            try (PreparedStatement psTime = conn.prepareStatement(sqlUpdateCartTime)) {
                psTime.setInt(1, cartId);
                psTime.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
