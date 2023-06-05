package com.example.chatrmi.ui.client;

import com.example.chatrmi.ClientNode;
import com.example.chatrmi.RemoteChat;
import com.example.chatrmi.connection.ConnectionRegistry;
import javafx.fxml.FXML;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientInterfaceController extends UnicastRemoteObject implements RemoteChat {

    ClientNode clientNode;

    public ClientInterfaceController() throws RemoteException {
    }

    public void initialize(String serverIp, int serverPort, String clientName, int clientPort) throws RemoteException, UnknownHostException, NotBoundException {
        // Create my own connection
        clientNode = new ClientNode(clientName, clientPort);
        // Export my public endpoint
        clientNode.getMyConnection().exportPublicEndpoint(this);
        clientNode.startServerConnection(serverIp, serverPort);
        createChat();
    }

    private void createChat() {
        // Create the UI first then export the chat itself to the private endpoint
        // Add the UI reference
        // Export endpoint to allow server to call client UI
        // clientNode.getMyConnection().exportEndpoint("server", chat);
        // Create ChatIndicator
        // Create ChatContainer
        // Add ChatIndicator to indicators list
        // Add ChatContainer to chat list
    }

    private void createChatIndicator() {

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

/*    interface Chat {
        ConnectionRegistry connection = null;
        ChatController

    }*/
}
