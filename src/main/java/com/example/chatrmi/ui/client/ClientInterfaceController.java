package com.example.chatrmi.ui.client;

import com.example.chatrmi.ClientNode;
import com.example.chatrmi.RemoteChat;
import com.example.chatrmi.connection.ConnectionRegistry;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ClientInterfaceController extends UnicastRemoteObject implements RemoteChat {

    @FXML
    private VBox chatContainer;
    @FXML
    private VBox indicatorContainer;
    @FXML
    private Label nameLabel;

    ClientNode clientNode;

    List<ChatController> chats = new ArrayList<>();


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

    private void createChat(ConnectionRegistry registry) throws RemoteException {
        // Create the UI first then export the chat itself to the private endpoint
        ChatIndicatorController indicator = createChatIndicator(registry);
        ChatController chat = createChatContainer(registry);
        chat.indicator = indicator;
        chat.connection = registry;
        chat.client = clientNode;
        // Export endpoint to allow to call client UI
        clientNode.getMyConnection().exportPrivateEndpoint(registry.name, chat);
        chats.add(chat);
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
            controller.initialize();
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
    public void receiveMessage(String message) throws RemoteException {}

    @Override
    public void receiveBroadcastMessage(String message, String name) throws RemoteException {

    }


}
