package com.mycompany.the_coffee_farm;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import database.DBConnection;

public class QuanLySanPham_Controller implements Initializable {

    @FXML
    private TableView<SanPham> tblSanPham;
    @FXML
    private TableColumn<SanPham, String> colId;
    @FXML
    private TableColumn<SanPham, String> colTen;
    @FXML
    private TableColumn<SanPham, Double> colGia;
    @FXML
    private TextField txtTenSP;
    @FXML
    private TextField txtGiaSP;
    @FXML
    private ImageView imgPreview;
    @FXML
    private Label lblTenAnh;
    @FXML
    private javafx.scene.layout.StackPane paneThongBao;
    @FXML
    private Label lblNoiDungThongBao;

    private File fileAnhDaChon;
    private SanPham sanPhamDangChon = null;

    private ObservableList<SanPham> danhSachGoc = FXCollections.observableArrayList();
    private FilteredList<SanPham> danhSachLoc;

    private int idDanhMucHienTai = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colId.setCellValueFactory(cellData -> {
            int rawId = Integer.parseInt(cellData.getValue().getIdSanPham().trim());
            return new javafx.beans.property.SimpleStringProperty(String.format("TCF-SP%04d", rawId));
        });

        colTen.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTenSanPham()));
        colGia.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getGiaBan()));

        danhSachLoc = new FilteredList<>(danhSachGoc, p -> p.getIdLoai() == 1);
        tblSanPham.setItems(danhSachLoc);

        loadDataLenBang();

        tblSanPham.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1 && tblSanPham.getSelectionModel().getSelectedItem() != null) {
                sanPhamDangChon = tblSanPham.getSelectionModel().getSelectedItem();

                txtTenSP.setText(sanPhamDangChon.getTenSanPham());
                txtGiaSP.setText(String.format("%.0f", sanPhamDangChon.getGiaBan()));

                hienThiAnh(sanPhamDangChon.getImageUrl());
                fileAnhDaChon = null;
            }
        });
    }

    @FXML
    private void locTho(ActionEvent event) {
        idDanhMucHienTai = 1;
        danhSachLoc.setPredicate(sanPham -> sanPham.getIdLoai() == 1);
    }

    @FXML
    private void locSoChe(ActionEvent event) {
        idDanhMucHienTai = 2;
        danhSachLoc.setPredicate(sanPham -> sanPham.getIdLoai() == 2);
    }

    @FXML
    private void locDoAn(ActionEvent event) {
        idDanhMucHienTai = 3;
        danhSachLoc.setPredicate(sanPham -> sanPham.getIdLoai() == 3
                && sanPham.getTenSanPham().toLowerCase().contains("bánh"));
    }

    @FXML
    private void locDoUong(ActionEvent event) {
        idDanhMucHienTai = 3;
        danhSachLoc.setPredicate(sanPham -> sanPham.getIdLoai() == 3
                && !sanPham.getTenSanPham().toLowerCase().contains("bánh"));
    }

    private void loadDataLenBang() {
        new Thread(() -> {
            ObservableList<SanPham> danhSach = DBConnection.layDanhSachSanPhamAdmin();
            javafx.application.Platform.runLater(() -> {
                danhSachGoc.setAll(danhSach);
            });
        }).start();
    }

    @FXML
    private void chonAnh(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh cho Sản Phẩm");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("File ảnh (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(filter);

        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            fileAnhDaChon = file;
            lblTenAnh.setText(file.getName());
            Image image = new Image(file.toURI().toString());
            imgPreview.setImage(image);
        }
    }

    @FXML
    private void themSP(ActionEvent event) {
        String ten = txtTenSP.getText();
        String giaStr = txtGiaSP.getText();

        if (ten.isEmpty() || giaStr.isEmpty() || fileAnhDaChon == null) {
            hienThiThongBao("Lỗi", "Sản phẩm thiếu thông tin!");
            return;
        }

        try {
            double gia = Double.parseDouble(giaStr);
            String tenAnhLuuDB = "images/" + fileAnhDaChon.getName();

            File thuMucSrc = new File("src/main/resources/com/mycompany/the_coffee_farm/images/" + fileAnhDaChon.getName());
            File thuMucTarget = new File("target/classes/com/mycompany/the_coffee_farm/images/" + fileAnhDaChon.getName());
            if (!thuMucSrc.exists()) {
                java.nio.file.Files.copy(fileAnhDaChon.toPath(), thuMucSrc.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            if (!thuMucTarget.exists()) {
                java.nio.file.Files.copy(fileAnhDaChon.toPath(), thuMucTarget.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            if (DBConnection.themSanPhamAdmin(idDanhMucHienTai, ten, gia, tenAnhLuuDB)) {
                hienThiThongBao("Thành công", "Đã thêm sản phẩm mới!");
                loadDataLenBang();
                lamSachO();
            } else {
                hienThiThongBao("Lỗi", "Thêm thất bại!");
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi", "Xử lý file ảnh thất bại!");
            e.printStackTrace();
        }
    }

    @FXML
    private void suaSP(ActionEvent event) {
        if (sanPhamDangChon == null) {
            hienThiThongBao("Chú ý", "Bạn chưa chọn sản phẩm!");
            return;
        }

        String ten = txtTenSP.getText();
        String giaStr = txtGiaSP.getText();

        if (ten.isEmpty() || giaStr.isEmpty()) {
            hienThiThongBao("Lỗi", "Không được để trống Tên và Giá!");
            return;
        }

        try {
            double gia = Double.parseDouble(giaStr);
            String tenAnhLuuDB = null;

            if (fileAnhDaChon != null) {
                tenAnhLuuDB = "images/" + fileAnhDaChon.getName();

                File thuMucSrc = new File("src/main/resources/com/mycompany/the_coffee_farm/images/" + fileAnhDaChon.getName());
                File thuMucTarget = new File("target/classes/com/mycompany/the_coffee_farm/images/" + fileAnhDaChon.getName());
                if (!thuMucSrc.exists()) {
                    java.nio.file.Files.copy(fileAnhDaChon.toPath(), thuMucSrc.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
                if (!thuMucTarget.exists()) {
                    java.nio.file.Files.copy(fileAnhDaChon.toPath(), thuMucTarget.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            }

            if (DBConnection.suaSanPhamAdmin(sanPhamDangChon.getIdSanPham(), sanPhamDangChon.getIdLoai(), ten, gia, tenAnhLuuDB)) {
                hienThiThongBao("Thành công", "Đã cập nhật sản phẩm!");
                loadDataLenBang();
                lamSachO();
            }
        } catch (Exception e) {
            hienThiThongBao("Lỗi", "Không thể cập nhật sản phẩm!");
            e.printStackTrace();
        }
    }

    @FXML
    private void xoaSP(ActionEvent event) {
        if (sanPhamDangChon == null) {
            hienThiThongBao("Chú ý", "Bạn chưa chọn sản phẩm!");
            return;
        }

        if (DBConnection.xoaSanPhamAdmin(sanPhamDangChon.getIdSanPham())) {
            hienThiThongBao("Thành công", "Đã xóa sản phẩm!");
            loadDataLenBang();
            lamSachO();
        }
    }

    @FXML
    private void quayLai(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AdminControl.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hienThiThongBao(String tieuDe, String noiDung) {
        lblNoiDungThongBao.setText(tieuDe + ": " + noiDung);
        paneThongBao.setVisible(true);
    }

    @FXML
    private void dongThongBao(ActionEvent event) {
        paneThongBao.setVisible(false);
    }

    private void lamSachO() {
        txtTenSP.clear();
        txtGiaSP.clear();
        fileAnhDaChon = null;
        lblTenAnh.setText("Chưa chọn ảnh nào");
        imgPreview.setImage(null);
        sanPhamDangChon = null;
    }

    private void hienThiAnh(String urlAnh) {
        try {
            if (urlAnh != null && !urlAnh.trim().isEmpty()) {
                URL imgUrl = getClass().getResource(urlAnh);

                if (imgUrl != null) {
                    Image image = new Image(imgUrl.toExternalForm());
                    imgPreview.setImage(image);
                    lblTenAnh.setText("Đã tải: " + urlAnh);
                } else {
                    File fileNgoai = new File(urlAnh);
                    if (fileNgoai.exists()) {
                        Image image = new Image(fileNgoai.toURI().toString());
                        imgPreview.setImage(image);
                        lblTenAnh.setText("Đã tải từ máy: " + fileNgoai.getName());
                    } else {
                        imgPreview.setImage(null);
                        lblTenAnh.setText("Không tìm thấy ảnh!");
                    }
                }
            } else {
                imgPreview.setImage(null);
                lblTenAnh.setText("Sản phẩm không có ảnh");
            }
        } catch (Exception e) {
            imgPreview.setImage(null);
            lblTenAnh.setText("Lỗi load ảnh");
            e.printStackTrace();
        }
    }
}
