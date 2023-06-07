package com.example.chatrmi.ui.server;

import com.example.chatrmi.RemoteChat;
import com.example.chatrmi.ServerNode;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class ServerInterfaceController extends UnicastRemoteObject implements RemoteChat {

    @FXML
    private TextField ip;
    @FXML
    private TextField port;
    @FXML
    private TextArea console;
    ServerNode serverNode;

    public ServerInterfaceController() throws RemoteException {
    }

    public void initialize(int serverPort) throws RemoteException, UnknownHostException {
        // Create own connection
        serverNode = new ServerNode(serverPort);
        // Export  the public endpoint
        serverNode.getMyConnection().exportPublicEndpoint(this);
        ip.setText(serverNode.getMyConnection().ip);
        port.setText(String.valueOf(serverNode.getMyConnection().port));
        ip.setDisable(true);
        port.setDisable(true);
        writeToConsole("Server started");
    }

    // ****************************** Remoto ****************************** //

    public void writeToConsole(String message) {
        String timestamp = String.valueOf(java.time.LocalTime.now()).split("\\.")[0];
        timestamp = "[" + timestamp + "]";
        console.appendText(timestamp + " " + message + "\n");
    }

    @Override
    public boolean startRemoteConnection(String ip, int port, String name) throws RemoteException {
        writeToConsole(name + "(" + ip + ") connected");
        // Save client in connections
        serverNode.addConnection(ip, port, name);
        // Not needed to export endpoint, use public endpoint
        return true;
    }
    @Override
    public boolean endRemoteConnection(String name) throws RemoteException {
        serverNode.removeConnection(name);
        return true;
    }
    @Override
    public void receiveMessage(String message) throws RemoteException {}
    @Override
    public void receiveBroadcastMessage(String message, String name) throws RemoteException {
        writeToConsole("Received broadcast message from " + name + ": " + message);
        serverNode.sendBroadcastMessage(message, name);
    }

    @Override
    public List<String> getAllClients() {
        return serverNode.getConnectionsInfo();
    }
}
