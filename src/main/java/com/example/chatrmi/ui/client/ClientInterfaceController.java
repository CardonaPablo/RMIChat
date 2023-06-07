package com.example.chatrmi.ui.client;

import com.example.chatrmi.ClientNode;
import com.example.chatrmi.RemoteChat;
import com.example.chatrmi.connection.ConnectionRegistry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
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

    @FXML
    private Button newChatButton;
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

        newChatButton.setOnMouseClicked(event -> {
            try {
                showNewChatDialog();
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void createChat(ConnectionRegistry registry) throws RemoteException {
        // Create the UI first then export the chat itself to the private endpoint
        ChatController chat = createChatContainer();
        ChatIndicatorController indicator = createChatIndicator(registry, chat);
        chat.indicator = indicator;
        chat.connection = registry;
        chat.client = clientNode;
        // Export endpoint to allow to call client UI
        clientNode.getMyConnection().exportPrivateEndpoint(registry.name, chat);
        chats.add(chat);
    }

    private ChatIndicatorController createChatIndicator(ConnectionRegistry registry, ChatController chat) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChatIndicator.fxml"));
        try {
            Parent root = loader.load();
            ChatIndicatorController controller = loader.getController();
            controller.initialize(registry);
            root.setOnMouseClicked(event -> {
                controller.updateIndicator(false);
                indicatorContainer.getChildren().forEach(node -> {
                    node.setStyle("");
                });
                root.setStyle("-fx-background-color: #a8a5a5");
                chatContainer.getChildren().remove(chatContainer.getChildren().size() - 1);
                System.out.println("Changing chat to " + registry.name);
                chatContainer.getChildren().add(chat.root);
            });
            indicatorContainer.getChildren().add(0, root);
            indicatorContainer.getChildren().forEach(node -> {
                node.setStyle("");
            });
            root.setStyle("-fx-background-color: #a8a5a5");
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private ChatController createChatContainer() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Chat.fxml"));
        try {
            Parent root = loader.load();
            ChatController controller = loader.getController();
            if(chatContainer.getChildren().size() > 0) chatContainer.getChildren().remove(chatContainer.getChildren().size() - 1);
            chatContainer.getChildren().add(root);
            controller.initialize();
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void showNewChatDialog() throws NotBoundException, RemoteException {
        // Get all the connections from server
        RemoteChat server = clientNode.getServerConnection().getPublicRemoteChat();
        List<String> clients = server.getAllClients();
        // Create the UI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("NewChatSelector.fxml"));
        try {
            Parent root = loader.load();
            NewChatSelectorController controller = loader.getController();
            controller.initialize(clients, this);
            indicatorContainer.getChildren().remove(indicatorContainer.getChildren().size() - 1);
            indicatorContainer.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void startNewClientConnection(String name, String ip, int port) {
        System.out.println("Starting new client connection with data: ");
        System.out.println(name + " - " + ip + ":" + port);
        try {
            ConnectionRegistry registry = clientNode.requestStartConnection(ip, port, name);
            // Create the chat
            createChat(registry);
            // Remove the UI
            indicatorContainer.getChildren().remove(indicatorContainer.getChildren().size() - 1);
            indicatorContainer.getChildren().add(newChatButton);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        } catch (NotBoundException e) {
            throw new RuntimeException(e);
        }
    }


    // ****************************** Remotos ****************************** //
    @Override
    public boolean startRemoteConnection(String ip, int port, String name) throws RemoteException {
        ConnectionRegistry registry = clientNode.addConnection(ip, port, name);
        // Create the chat
        Platform.runLater(() -> {
            try {
                createChat(registry);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
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

    @Override
    public List<String> getAllClients() throws RemoteException {
        return null;
    }


}
