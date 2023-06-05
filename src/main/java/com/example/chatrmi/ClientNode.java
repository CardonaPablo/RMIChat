package com.example.chatrmi;

import com.example.chatrmi.connection.ChatNode;
import com.example.chatrmi.connection.ConnectionRegistry;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class ClientNode extends ChatNode {

    public ClientNode(String name, int port) throws RemoteException, UnknownHostException {
        super(name, port);
    }

    public void sendBroadcastMessage(String message, String author) {

    }

    private ConnectionRegistry getServerConnection() {
        for(ConnectionRegistry connection : connections) {
            if(connection.name.equals("server")) {
                return connection;
            }
        }
        return null;
    }

    public void startServerConnection(String ip, int port) throws NotBoundException, RemoteException {
        requestStartConnection(ip, port, "server");
    }

}
