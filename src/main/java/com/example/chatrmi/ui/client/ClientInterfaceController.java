package com.example.chatrmi.ui.client;

import com.example.chatrmi.ClientNode;
import com.example.chatrmi.RemoteChat;
import com.example.chatrmi.connection.ConnectionRegistry;
import com.example.chatrmi.ui.server.ServerInterfaceController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientInterfaceController extends UnicastRemoteObject implements RemoteChat {

    @FXML
    private VBox chatContainer;
    @FXML
    private VBox indicatorContainer;
    @FXML
    private Label nameLabel;

    ClientNode clientNode;


    public ClientInterfaceController() throws RemoteException {
    }

    public void initialize(String serverIp, int serverPort, String clientName, int clientPort) throws RemoteException, UnknownHostException, NotBoundException {
        // Create my own connection
        clientNode = new ClientNode(clientName, clientPort);
        // Export my public endpoint
        nameLabel.setText(clientName);
        clientNode.getMyConnection().exportPublicEndpoint(this);
        clientNode.startServerConnection(serverIp, serverPort);
        createChat(clientNode.getServerConnection());
    }

    private void createChat(ConnectionRegistry registry) {
        // Create the UI first then export the chat itself to the private endpoint
        // Add the UI reference
        // Create ChatIndicator
        ChatIndicatorController indicator = createChatIndicator(registry);
        // Add ChatContainer to chat list
        ChatController chat = createChatContainer(registry);
        // Export endpoint to allow server to call client UI
        // clientNode.getMyConnection().exportEndpoint("server", chat);
    }

    private ChatIndicatorController createChatIndicator(ConnectionRegistry registry) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatIndicator.fxml"));
        try {
            Parent root = loader.load();
            ChatIndicatorController controller = loader.getController();
            controller.initialize(registry);
            root.setOnMouseClicked(event -> {
                controller.updateIndicator(false);
            });
            indicatorContainer.getChildren().add(root);
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ChatController createChatContainer(ConnectionRegistry registry) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat.fxml"));
        try {
            Parent root = loader.load();
            ChatController controller = loader.getController();
            chatContainer.getChildren().add(root);
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // ****************************** Remotos ****************************** //
    @Override
    public boolean startRemoteConnection(String ip, int port, String name) throws RemoteException {
        return true;
    }

    @Override
    public boolean endRemoteConnection(String name) throws RemoteException {
        return false;
    }

    @Override
    public void receiveUnicastMessage(String message) throws RemoteException {

    }

    @Override
    public void receiveBroadcastMessage(String message, String name) throws RemoteException {

    }


}
