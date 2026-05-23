
package com.mycompany.the_coffee_farm;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ThanhChucNang_Controller {
    
    @FXML
    private javafx.scene.layout.BorderPane khungChinh;
    
    @FXML
    private javafx.scene.control.Button btnShippingMethod; 

    @FXML
    private javafx.scene.layout.AnchorPane lopPhuShippingMethod;
   
    @FXML
    public void bamNutShippingMethod(javafx.event.ActionEvent event) {
        // Bật lớp phủ lên (hiện bảng chọn)
        if (lopPhuShippingMethod != null) {
            lopPhuShippingMethod.setVisible(true);
        } else {
            System.out.println("Lỗi: Chưa nối ID lớp phủ bên Scene Builder!");
        }
    }
    @FXML
    public void bamNutAccount(javafx.event.ActionEvent event) {
        try {
            // Lôi cái file giao diện Account m vừa làm ra
            javafx.scene.Parent ruotAccount = javafx.fxml.FXMLLoader.load(getClass().getResource("Account_Screen.fxml"));
            
            // Nhét nó vào giữa cái khung chính của màn hình
            if (khungChinh != null) {
                khungChinh.setCenter(ruotAccount);
            } else {
                System.out.println("Lỗi: Chưa đặt fx:id là 'khungChinh' cho BorderPane bên Scene Builder!");
            }
            
        } catch (Exception e) {
            System.out.println("Lỗi: Không tìm thấy hoặc không load được file Account_Screen.fxml!");
            e.printStackTrace(); // In ra lỗi màu đỏ để m biết sai ở đâu
        }
    }
    // ==========================================
    // CÁC HÀM XỬ LÝ LỚP PHỦ SHIPPING 
    // ==========================================

    @FXML
    public void chonGiaoHang(javafx.event.ActionEvent event) {
        System.out.println("Bạn đã chọn: Giao hàng tận nơi");
        if(btnShippingMethod != null) {
            btnShippingMethod.setText("Giao tận nơi");
        }
        if(lopPhuShippingMethod != null) {
            lopPhuShippingMethod.setVisible(false); 
        }
    }

    @FXML
    public void chonMangDi(javafx.event.ActionEvent event) {
        System.out.println("Bạn đã chọn: Đến lấy mang đi");
        if(btnShippingMethod != null) {
            btnShippingMethod.setText("Mang đi");
        }
        if(lopPhuShippingMethod != null) {
            lopPhuShippingMethod.setVisible(false); 
        }
    }

    @FXML
    public void chonTaiQuan(javafx.event.ActionEvent event) {
        System.out.println("Bạn đã chọn: Dùng tại quán");
        if(btnShippingMethod != null) {
            btnShippingMethod.setText("Tại quán");
        }
        if(lopPhuShippingMethod != null) {
            lopPhuShippingMethod.setVisible(false); 
        }
    }

}