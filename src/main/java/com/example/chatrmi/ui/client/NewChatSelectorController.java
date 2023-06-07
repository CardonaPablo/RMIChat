package com.example.chatrmi.ui.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class NewChatSelectorController {

    @FXML
    private ComboBox<String> clientSelector;
    @FXML
    private Button connectButton;
    private List<String> clients;

    ClientInterfaceController parent;

    public void initialize(List<String> clients, ClientInterfaceController parent) {
        // Get all clients from server
        this.parent = parent;
        this.clients = clients;
        clients.forEach(client -> {
            String[] parts = client.split("-");
            String name = parts[0];
            if(!name.equals(parent.clientNode.getMyConnection().name) && !name.equals("server"))
                clientSelector.getItems().add(0, name);
        });

        connectButton.setOnMouseClicked(mouseEvent -> {
            String selected = clientSelector.getValue();
            clients.forEach(client -> {
                String[] parts = client.split("-");
                String name = parts[0];
                if(name.equals(selected)) {
                    String[] ipParts = parts[1].split(":");
                    parent.startNewClientConnection(name, ipParts[0], Integer.parseInt(ipParts[1]));
                    return;
                }
            });
        });
    }

}
