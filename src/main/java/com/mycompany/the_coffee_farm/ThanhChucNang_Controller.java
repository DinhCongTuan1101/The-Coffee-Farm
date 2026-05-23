
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
    public void bamNutHome(javafx.event.ActionEvent event) {
        try {
            javafx.scene.Parent trangChuTacVu = javafx.fxml.FXMLLoader.load(getClass().getResource("FoodAndDrink.fxml"));
            
            if (khungChinh != null) {
                khungChinh.setCenter(trangChuTacVu);
            } else {
                System.out.println("Lỗi: Không tìm thấy 'khungChinh' để nhét trang Home!");
            }
        } catch (Exception e) {
            System.out.println("Lỗi: Không load được giao diện chính khi bấm Home!");
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