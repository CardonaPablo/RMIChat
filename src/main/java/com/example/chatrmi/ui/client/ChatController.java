package com.example.chatrmi.ui.client;

import com.example.chatrmi.ClientNode;
import com.example.chatrmi.RemoteChat;
import com.example.chatrmi.connection.ConnectionRegistry;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatController extends UnicastRemoteObject implements RemoteChat {

    ChatIndicatorController indicator;
    ConnectionRegistry connection;
    ClientNode client;

    @FXML
    private VBox messagesContainer;
    @FXML
    private TextField messageField;
    @FXML
    private ImageView sendButton;

    public ChatController() throws RemoteException {
    }

    public void initialize() throws RemoteException {
        sendButton.setOnMouseClicked(event -> {
            try {
                sendMessage();
            } catch (NotBoundException e) {
                throw new RuntimeException(e);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }


    private void sendMessage() throws NotBoundException, RemoteException {
        String message = messageField.getText();
        messageField.setText("");
        if(!connection.name.equals("server"))
            addMessageToChat(message, client.getMyConnection().name);
        client.sendMessage(message, connection);
    }
    private void addMessageToChat(String message, String name) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Message.fxml"));
        try {
            Parent root = loader.load();
            MessageController controller = loader.getController();
            controller.initialize(message, name, client.getMyConnection().name.equals(name));
            messagesContainer.getChildren().add(root);
            indicator.updateIndicator(true);
            indicator.updateMessage(message);
            indicator.updateName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // *** REMOTE METHODS ***
    @Override
    public boolean startRemoteConnection(String ip, int port, String name) throws RemoteException {
        return false;
    }

    @Override
    public boolean endRemoteConnection(String name) throws RemoteException {
        return false;
    }

    @Override
    public void receiveMessage(String message) throws RemoteException {

    }

    @Override
    public void receiveBroadcastMessage(String message, String name) throws RemoteException {
        System.out.println("Received broadcast message: " + message + " from " + name);
        Platform.runLater(() -> addMessageToChat(message, name));
    }
}
