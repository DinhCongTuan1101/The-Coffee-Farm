package com.mycompany.the_coffee_farm;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class PrimaryController implements Initializable {

    @FXML
    private StackPane contentArea;
    @FXML
    private Button btnHome;
    @FXML
    private Button btnMenu;
    @FXML
    private Button btnOrder;
    @FXML
    private Button btnAccount;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mới mở app lên thì tự động load màn hình Home đầu tiên
        loadSubView("home_screen.fxml");
    }

    @FXML
    private void handleNavigation(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        
        // Reset toàn bộ màu chữ các nút về màu xám mặc định
        btnHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d;");
        btnMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d;");
        btnOrder.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d;");
        btnAccount.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d;");

        // Nhấn nút nào thì tô màu cam nổi bật nút đó và gọi view tương ứng
        if (clickedButton == btnHome) {
            btnHome.setStyle("-fx-background-color: transparent; -fx-text-fill: #d35400;");
            loadSubView("home_screen.fxml");
        } else if (clickedButton == btnMenu) {
            btnMenu.setStyle("-fx-background-color: transparent; -fx-text-fill: #d35400;");
            loadSubView("menu_screen.fxml");
        } else if (clickedButton == btnOrder) {
            btnOrder.setStyle("-fx-background-color: transparent; -fx-text-fill: #d35400;");
            loadSubView("order_screen.fxml");
        } else if (clickedButton == btnAccount) {
            btnAccount.setStyle("-fx-background-color: transparent; -fx-text-fill: #d35400;");
            loadSubView("account_screen.fxml");
        }
    }

    // Hàm phụ trách bốc file FXML con ném vào vùng trung tâm màn hình chính
    private void loadSubView(String fxmlFile) {
        try {
            Parent fxml = FXMLLoader.load(getClass().getResource(fxmlFile));
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(fxml);
        } catch (IOException ex) {
            System.err.println("Không tìm thấy file giao diện con: " + fxmlFile);
            ex.printStackTrace();
        }
    }
}