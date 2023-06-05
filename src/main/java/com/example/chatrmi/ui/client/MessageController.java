package com.example.chatrmi.ui.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MessageController {

    @FXML
    private Label name;
    @FXML
    private Label message;

    public void initialize(String message, String name, boolean isMine) {
        this.message.setText(message);
        this.name.setText(name);
        if(isMine) {
            this.message.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            this.name.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
        }
    }
}
