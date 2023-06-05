package com.example.chatrmi;

import com.example.chatrmi.connection.ChatNode;

import java.rmi.RemoteException;

public class ClientNode extends ChatNode {

    public ClientNode(String ip, int port) throws RemoteException {
        super(ip, port);
    }

    public void sendBroadcastMessage(String message, String author) {

    }

    public void startServerConnection(String serverIp, int serverPort) {

    }

}
