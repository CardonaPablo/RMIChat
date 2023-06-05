package com.example.chatrmi.ui.client;

import com.example.chatrmi.connection.ConnectionRegistry;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class ChatIndicatorController {

    @FXML
    private Label name;
    @FXML
    private Label message;
    @FXML
    private Circle indicator;

    public void initialize(ConnectionRegistry registry) {
        name.setText(registry.name);
        message.setText("Connected");
        indicator.setVisible(true);
    }

    public void updateMessage(String message) {
        this.message.setText(message);
    }
    public void updateName(String name) {
        this.name.setText(name);
    }
    public void updateIndicator(boolean connected) {
        indicator.setVisible(connected);
    }
}
