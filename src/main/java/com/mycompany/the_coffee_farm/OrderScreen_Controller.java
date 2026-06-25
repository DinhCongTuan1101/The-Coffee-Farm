package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class OrderScreen_Controller implements Initializable {
    private final ExecutorService threadPool = Executors.newFixedThreadPool(5);
    @FXML
    private VBox vboxGioHang;
    @FXML
    private Button btnSuaGioHang;
    @FXML
    private RadioButton rdoOnline;
    @FXML
    private RadioButton rdoTaiQuay;
    @FXML
    private Label lblTongTien;
    @FXML
    private CheckBox chkTatCa;
    @FXML
    private javafx.scene.layout.AnchorPane lopPhuQR;
    @FXML 
    private javafx.scene.layout.AnchorPane lopPhuCanhBao; 

    private boolean dangSuaMode = false;
    private List<Button> danhSachNutXoa = new ArrayList<>();
    private int currentInsertedOrderId = -1;

    class DongGioHang {
        CheckBox chkChonMua;
        Label lblSoLuong;
        int giaTienMotMon;
        HBox uiNode;
        String tenMon;
    }
    private List<DongGioHang> danhSachMonTrongGio = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        if (TaiKhoan.phuongThucNhan == 1) {
            rdoTaiQuay.setDisable(true);
            rdoTaiQuay.setStyle("-fx-opacity: 0.4;");
            rdoOnline.setSelected(true);
        } else {
            rdoOnline.setSelected(true);
        }

        taiDuLieuTuGioHangChung();
        capNhatTongTien();
    }

    private void taiDuLieuTuGioHangChung() {
        vboxGioHang.getChildren().clear();
        danhSachNutXoa.clear();
        danhSachMonTrongGio.clear();

        for (String tenMon : TaiKhoan.gioHangChung.keySet()) {
            int soLuongHienTai = TaiKhoan.gioHangChung.get(tenMon)[0];
            int giaTien = TaiKhoan.gioHangChung.get(tenMon)[1];

            HBox dongMonHang = new HBox(15);
            dongMonHang.setStyle("-fx-padding: 10; -fx-background-color: #f5f5f5; -fx-background-radius: 10; -fx-alignment: center-left;");

            CheckBox chkChonMua = new CheckBox();
            chkChonMua.setStyle("-fx-cursor: hand;");
            chkChonMua.setOnAction(e -> {
                capNhatTongTien();
                kiemTraNutTatCa();
            });

            Label lblTen = new Label(tenMon);
            lblTen.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-min-width: 160px; -fx-pref-width: 240px; -fx-max-width: 240px; -fx-wrap-text: false;");

            HBox khongGianTrong = new HBox();
            HBox.setHgrow(khongGianTrong, Priority.ALWAYS);

            Button btnTru = new Button("-");
            btnTru.setStyle("-fx-background-color: #dddddd; -fx-cursor: hand; -fx-background-radius: 5;");
            Label lblSoLuong = new Label(String.valueOf(soLuongHienTai));
            lblSoLuong.setStyle("-fx-font-weight: bold; -fx-padding: 0 10 0 10;");
            Button btnCong = new Button("+");
            btnCong.setStyle("-fx-background-color: #dddddd; -fx-cursor: hand; -fx-background-radius: 5;");

            btnCong.setOnAction(e -> {
                int sl = Integer.parseInt(lblSoLuong.getText());
                int slMoi = sl + 1;
                lblSoLuong.setText(String.valueOf(slMoi));
                TaiKhoan.gioHangChung.get(tenMon)[0] = slMoi;
                capNhatTongTien();
                if (TaiKhoan.daDangNhap && TaiKhoan.id > 0) {
                    threadPool.execute(() -> {
                        database.DBConnection.capNhatSoLuongGioHangDB(TaiKhoan.id, tenMon, slMoi);
                    });
                }
            });

            btnTru.setOnAction(e -> {
                int sl = Integer.parseInt(lblSoLuong.getText());
                if (sl > 1) {
                    int slMoi = sl - 1;
                    lblSoLuong.setText(String.valueOf(slMoi));
                    TaiKhoan.gioHangChung.get(tenMon)[0] = slMoi;
                    capNhatTongTien();
                    if (TaiKhoan.daDangNhap && TaiKhoan.id > 0) {
                        threadPool.execute(() -> {
                            database.DBConnection.capNhatSoLuongGioHangDB(TaiKhoan.id, tenMon, slMoi);
                        });
                    }
                }
            });
            HBox boxSoLuong = new HBox(5, btnTru, lblSoLuong, btnCong);
            boxSoLuong.setStyle("-fx-alignment: center; -fx-padding: 0 10 0 0;");

            String giaChu = String.format("%,dđ", giaTien).replace(",", ".");
            Label lblGia = new Label(giaChu);
            lblGia.setStyle("-fx-min-width: 80px; -fx-pref-width: 100px; -fx-max-width: 100px; -fx-alignment: center-right;");

            Button btnXoa = new Button("X");
            btnXoa.setStyle("-fx-background-color: #ff3333; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
            btnXoa.setVisible(false);
            danhSachNutXoa.add(btnXoa);

            DongGioHang dongDuLieu = new DongGioHang();
            dongDuLieu.chkChonMua = chkChonMua;
            dongDuLieu.lblSoLuong = lblSoLuong;
            dongDuLieu.giaTienMotMon = giaTien;
            dongDuLieu.uiNode = dongMonHang;
            dongDuLieu.tenMon = tenMon;
            danhSachMonTrongGio.add(dongDuLieu);

            btnXoa.setOnAction(e -> {
                vboxGioHang.getChildren().remove(dongMonHang);
                danhSachNutXoa.remove(btnXoa);
                danhSachMonTrongGio.remove(dongDuLieu);
                TaiKhoan.gioHangChung.remove(tenMon);
                capNhatTongTien();
                if (TaiKhoan.daDangNhap && TaiKhoan.id > 0) {
                    threadPool.execute(() -> {
                        database.DBConnection.xoaMonKhoiGioHangDB(TaiKhoan.id, tenMon);
                    });
                }
            });

            dongMonHang.getChildren().addAll(chkChonMua, lblTen, khongGianTrong, boxSoLuong, lblGia, btnXoa);
            vboxGioHang.getChildren().add(dongMonHang);
        }
    }

    private void capNhatTongTien() {
        int tongCong = 0;
        for (DongGioHang dong : danhSachMonTrongGio) {
            if (dong.chkChonMua.isSelected()) {
                int sl = Integer.parseInt(dong.lblSoLuong.getText());
                tongCong += (sl * dong.giaTienMotMon);
            }
        }
        lblTongTien.setText(String.format("%,dđ", tongCong).replace(",", "."));
    }

    @FXML
    private void xuLyChonTatCa(ActionEvent event) {
        boolean chonHetKhong = chkTatCa.isSelected();
        for (DongGioHang dong : danhSachMonTrongGio) {
            dong.chkChonMua.setSelected(chonHetKhong);
        }
        capNhatTongTien();
    }

    private void kiemTraNutTatCa() {
        boolean tatCaDeuTick = true;
        for (DongGioHang dong : danhSachMonTrongGio) {
            if (!dong.chkChonMua.isSelected()) {
                tatCaDeuTick = false;
                break;
            }
        }
        chkTatCa.setSelected(tatCaDeuTick);
    }

    @FXML
    private void xuLySuaGioHang(ActionEvent event) {
        dangSuaMode = !dangSuaMode;
        if (dangSuaMode) {
            btnSuaGioHang.setText("Xong");
            for (Button nutXoa : danhSachNutXoa) {
                nutXoa.setVisible(true);
            }
        } else {
            btnSuaGioHang.setText("Sửa");
            for (Button nutXoa : danhSachNutXoa) {
                nutXoa.setVisible(false);
            }
        }
    }

    @FXML
    private void xuLyQuayLai(ActionEvent event) {
        if (TaiKhoan.sceneTruocKhiVaoGio != null) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(TaiKhoan.sceneTruocKhiVaoGio);
        }
    }

    @FXML
    private void xuLyMuaHang(ActionEvent event) {
        try {
            int tongTien = Integer.parseInt(lblTongTien.getText().replace(".", "").replace("đ", "").trim());
            if (tongTien == 0) return;

            if (TaiKhoan.phuongThucNhan == 0) {
                lopPhuCanhBao.toFront();
                lopPhuCanhBao.setVisible(true); 
                return; 
            }

            List<DongGioHang> dsMonDuocChon = new ArrayList<>();
            for (DongGioHang dong : danhSachMonTrongGio) {
                if (dong.chkChonMua.isSelected()) {
                    dsMonDuocChon.add(dong);
                }
            }
            
            int userId = TaiKhoan.id; 
            String shippingAddress = (rdoOnline.isSelected()) ? "Giao hàng tận nơi" : "Nhận tại quầy";

            threadPool.execute(() -> {
                System.out.println("[Thread: " + Thread.currentThread().getName() + "] Đang xử lý giao dịch tại DB...");
                
                boolean dbSuccess = executeDatabaseTransaction(userId, tongTien, shippingAddress, dsMonDuocChon);
                
                Platform.runLater(() -> {
                    if (dbSuccess) {
                        xoaCacMonDaMua(); 
                        taiDuLieuTuGioHangChung();
                        
                        if (rdoOnline.isSelected()) {
                            lopPhuQR.toFront(); 
                            lopPhuQR.setVisible(true);
                        } else if (rdoTaiQuay.isSelected()) {
                            capNhatTrangThaiDonHang(currentInsertedOrderId, "Thành công");
                            chuyenTrang(event, "MuaHangThanhCong(Onl).fxml");
                        }
                    } else {                       
                        System.err.println("Thanh toán thất bại do lỗi hệ thống hoặc hết hàng kho!");
                        if (currentInsertedOrderId != -1) {
                            capNhatTrangThaiDonHang(currentInsertedOrderId, "Thất bại");
                        }
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace(); 
        }
    }

    private boolean executeDatabaseTransaction(int userId, int tongTien, String shippingAddress, List<DongGioHang> dsMonDuocChon) {
        java.sql.Connection conn = null;
        currentInsertedOrderId = -1;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            for (DongGioHang dong : dsMonDuocChon) {
                int soLuongMua = Integer.parseInt(dong.lblSoLuong.getText());
                String checkStockSql = "SELECT product_id, stock_quantity FROM products WITH (UPDLOCK) WHERE product_name = ?";
                int productId = -1;
                try (java.sql.PreparedStatement psCheck = conn.prepareStatement(checkStockSql)) {
                    psCheck.setString(1, dong.tenMon);
                    try (java.sql.ResultSet rs = psCheck.executeQuery()) {
                        if (rs.next()) {
                            productId = rs.getInt("product_id");
                            int currentStock = rs.getInt("stock_quantity");
                            if (currentStock < soLuongMua) {
                                System.err.println(dong.tenMon + " không đủ hàng!");
                                conn.rollback();
                                return false;
                            }
                        } else {
                            System.err.println("Không tìm thấy món: " + dong.tenMon);
                            conn.rollback();
                            return false;
                        }
                    }
                }
                String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ?";
                try (java.sql.PreparedStatement psUpdate = conn.prepareStatement(updateStockSql)) {
                    psUpdate.setInt(1, soLuongMua);
                    psUpdate.setInt(2, productId);
                    psUpdate.executeUpdate();
                }
            }

            String insertOrderSql = "INSERT INTO orders (user_id, total_amount, shipping_address, order_status, ordered_at) VALUES (?, ?, ?, ?, GETDATE())";
            try (java.sql.PreparedStatement psOrder = conn.prepareStatement(insertOrderSql, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setInt(1, userId);
                psOrder.setDouble(2, tongTien);
                psOrder.setString(3, shippingAddress);
                psOrder.setString(4, "Chờ duyệt");
                psOrder.executeUpdate();

                try (java.sql.ResultSet generatedKeys = psOrder.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        currentInsertedOrderId = generatedKeys.getInt(1);
                    }
                }
            }

            if (currentInsertedOrderId == -1) {
                conn.rollback();
                return false;
            }

            String insertDetailSql = "INSERT INTO order_details (order_id, product_id, quantity, historical_price) " +
                                     "VALUES (?, (SELECT product_id FROM products WHERE product_name = ?), ?, ?)";
            try (java.sql.PreparedStatement psDetail = conn.prepareStatement(insertDetailSql)) {
                for (DongGioHang dong : dsMonDuocChon) {
                    int soLuongMua = Integer.parseInt(dong.lblSoLuong.getText());
                    psDetail.setInt(1, currentInsertedOrderId);
                    psDetail.setString(2, dong.tenMon);
                    psDetail.setInt(3, soLuongMua);
                    psDetail.setDouble(4, dong.giaTienMotMon);
                    psDetail.addBatch();
                }
                psDetail.executeBatch();
            }

            String deleteCartDetailsSql = "DELETE cd FROM cart_details cd JOIN carts c ON cd.cart_id = c.cart_id " +
                                          "WHERE c.user_id = ? AND cd.product_id = (SELECT product_id FROM products WHERE product_name = ?)";
            try (java.sql.PreparedStatement psDelCart = conn.prepareStatement(deleteCartDetailsSql)) {
                for (DongGioHang dong : dsMonDuocChon) {
                    psDelCart.setInt(1, userId);
                    psDelCart.setString(2, dong.tenMon);
                    psDelCart.addBatch();
                }
                psDelCart.executeBatch();
            }

            conn.commit();
            return true;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (java.sql.SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (java.sql.SQLException e) { e.printStackTrace(); }
            }
        }
    }

    private void capNhatTrangThaiDonHang(int orderId, String trangThai) {
        if (orderId == -1) return;
        threadPool.execute(() -> {
            String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
            try (java.sql.Connection conn = DBConnection.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, trangThai);
                ps.setInt(2, orderId);
                ps.executeUpdate();
            } catch (java.sql.SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void xacNhanDaQuetQR(javafx.scene.input.MouseEvent event) {
        lopPhuQR.setVisible(false);
        xoaCacMonDaMua(); 
        capNhatTrangThaiDonHang(currentInsertedOrderId, "Thành công");
        chuyenTrangSauKhiBamVungNgoai(event, "MuaHangThanhCong(Onl).fxml");
    }

    private void chuyenTrangSauKhiBamVungNgoai(javafx.scene.input.MouseEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chuyenTrang(ActionEvent event, String fxml) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxml));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void xoaCacMonDaMua() {
        java.util.List<String> monCanXoa = new java.util.ArrayList<>();
        for (DongGioHang dong : danhSachMonTrongGio) {
            if (dong.chkChonMua.isSelected()) {
                monCanXoa.add(dong.tenMon);
            }
        }
        for (String tenMon : monCanXoa) {
            TaiKhoan.gioHangChung.remove(tenMon);
        }
    }

    @FXML
    private void anLopPhuCanhBao(javafx.scene.input.MouseEvent event) {
        lopPhuCanhBao.setVisible(false); 
    }
}