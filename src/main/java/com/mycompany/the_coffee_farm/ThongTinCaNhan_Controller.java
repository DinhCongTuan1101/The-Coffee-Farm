package com.mycompany.the_coffee_farm;

import database.DBConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ThongTinCaNhan_Controller implements Initializable {

    @FXML
    private Label txtTen;
    @FXML
    private Label txtTieuSu;
    @FXML
    private Label txtGioiTinh;
    @FXML
    private Label txtNgaySinh;
    @FXML
    private Label txtSDT;
    @FXML
    private Label txtEmail;

    @FXML
    private AnchorPane lopPhuMoc;
    @FXML
    private VBox boxTieuSu;
    @FXML
    private VBox boxGioiTinh;
    @FXML
    private VBox boxNgaySinh;

    @FXML
    private TextField inputTieuSu;
    @FXML
    private RadioButton rdoNam;
    @FXML
    private RadioButton rdoNu;
    @FXML
    private RadioButton rdoKhac;
    @FXML
    private DatePicker dpNgaySinh;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadDuLieuTuDatabase();
    }

    private void loadDuLieuTuDatabase() {
        String sql = "SELECT username, phone_number, email, tieu_su, gioi_tinh, ngay_sinh FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, TaiKhoan.id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String ten = rs.getString("username");
                String sdt = rs.getString("phone_number");
                String email = rs.getString("email");
                String tieuSu = rs.getString("tieu_su");
                String gioiTinh = rs.getString("gioi_tinh");
                java.sql.Date ngaySinh = rs.getDate("ngay_sinh");

                txtTen.setText(ten != null ? ten : "Chưa cập nhật");

                if (sdt != null && sdt.length() >= 4) {
                    txtSDT.setText("*".repeat(sdt.length() - 2) + sdt.substring(sdt.length() - 2));
                } else {
                    txtSDT.setText(sdt != null ? sdt : "Chưa cập nhật");
                }

                if (email != null && email.contains("@")) {
                    String[] parts = email.split("@");
                    if (parts[0].length() > 2) {
                        txtEmail.setText(parts[0].charAt(0) + "*".repeat(parts[0].length() - 2) + parts[0].charAt(parts[0].length() - 1) + "@" + parts[1]);
                    } else {
                        txtEmail.setText(email);
                    }
                } else {
                    txtEmail.setText(email != null ? email : "Chưa cập nhật");
                }

                txtTieuSu.setText(tieuSu != null ? tieuSu : "Thiết lập ngay");
                txtGioiTinh.setText(gioiTinh != null ? gioiTinh : "Thiết lập ngay");

                if (ngaySinh != null) {
                    LocalDate date = ngaySinh.toLocalDate();
                    txtNgaySinh.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } else {
                    txtNgaySinh.setText("Thiết lập ngay");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void quayLai(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            if (Account_Screen_Controller.sceneAccountGoc != null) {
                stage.setScene(Account_Screen_Controller.sceneAccountGoc);
                stage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void dongPopup(ActionEvent event) {
        lopPhuMoc.setVisible(false);
    }

    private void hienThiPopup(VBox hopThoaiCanMo) {
        boxTieuSu.setVisible(false);
        boxGioiTinh.setVisible(false);
        boxNgaySinh.setVisible(false);

        hopThoaiCanMo.setVisible(true);
        lopPhuMoc.setVisible(true);
    }

    @FXML
    public void moSuaTieuSu(MouseEvent event) {
        if (!txtTieuSu.getText().equals("Thiết lập ngay")) {
            inputTieuSu.setText(txtTieuSu.getText());
        } else {
            inputTieuSu.clear();
        }
        hienThiPopup(boxTieuSu);
    }

    @FXML
    public void moSuaGioiTinh(MouseEvent event) {
        String gt = txtGioiTinh.getText();
        if (gt.equals("Nam")) {
            rdoNam.setSelected(true);
        } else if (gt.equals("Nữ")) {
            rdoNu.setSelected(true);
        } else if (gt.equals("Khác")) {
            rdoKhac.setSelected(true);
        }

        hienThiPopup(boxGioiTinh);
    }

    @FXML
    public void moSuaNgaySinh(MouseEvent event) {
        hienThiPopup(boxNgaySinh);
    }

    private void capNhatDatabase(String tenCot, Object giaTri) {
        String sql = "UPDATE users SET " + tenCot + " = ? WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setObject(1, giaTri);
            ps.setInt(2, TaiKhoan.id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void luuTieuSu(ActionEvent event) {
        String textMoi = inputTieuSu.getText().trim();
        if (!textMoi.isEmpty()) {
            capNhatDatabase("tieu_su", textMoi);
            txtTieuSu.setText(textMoi);
        }
        dongPopup(event);
    }

    @FXML
    public void luuGioiTinh(ActionEvent event) {
        String gt = "Khác";
        if (rdoNam.isSelected()) {
            gt = "Nam";
        } else if (rdoNu.isSelected()) {
            gt = "Nữ";
        }

        capNhatDatabase("gioi_tinh", gt);
        txtGioiTinh.setText(gt);
        txtGioiTinh.setTextFill(javafx.scene.paint.Color.valueOf("#4a4a4a"));
        dongPopup(event);
    }

    @FXML
    public void luuNgaySinh(ActionEvent event) {
        LocalDate date = dpNgaySinh.getValue();
        if (date != null) {
            capNhatDatabase("ngay_sinh", java.sql.Date.valueOf(date));
            txtNgaySinh.setText(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            txtNgaySinh.setTextFill(javafx.scene.paint.Color.valueOf("#4a4a4a"));
        }
        dongPopup(event);
    }
}
