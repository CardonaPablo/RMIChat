package com.example.chatrmi.connection;

import com.example.chatrmi.RemoteChat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ChatNode {
    public List<ConnectionRegistry> connections = new ArrayList<>();

    protected ChatNode(String name, int port) throws RemoteException, UnknownHostException {
        // Register self
        String ip = InetAddress.getLocalHost().getHostAddress();
        ConnectionRegistry myConnection = new ConnectionRegistry(ip, port, name, name);
        connections.add(myConnection);

    }

    protected RemoteChat getRemoteNode(String name) throws RemoteException, NotBoundException {
        // Find remote connection based on name
        // Search for connection
        int connectionIndex = -1;
        for(int i = 0; i < connections.size(); i++) {
            if(connections.get(i).name.equals(name)) {
                connectionIndex = i;
                break;
            }
        }
        if(connectionIndex == -1) {
            return null;
        }
        // Create remote connection
        ConnectionRegistry connection = connections.get(connectionIndex);
        Registry registry = LocateRegistry.getRegistry(connection.ip, connection.port);
        RemoteChat remoteInterface = (RemoteChat) registry.lookup(connection.endpoint);
        return remoteInterface;
    }

    protected RemoteChat getPublicRemoteNode(String name) throws RemoteException, NotBoundException {
        // Find remote connection based on name
        // Search for connection
        int connectionIndex = -1;
        for(int i = 0; i < connections.size(); i++) {
            if(connections.get(i).name.equals(name)) {
                connectionIndex = i;
                break;
            }
        }
        if(connectionIndex == -1) {
            return null;
        }
        // Create remote connection
        ConnectionRegistry connection = connections.get(connectionIndex);
        Registry registry = LocateRegistry.getRegistry(connection.ip, connection.port);
        RemoteChat remoteInterface = (RemoteChat) registry.lookup(connection.name);
        return remoteInterface;
    }

    // *********************************** Connection ***********************************

    /*
    * Called by the UI, recieves the connection with the data returned by the server parsed to a ConnectionRegistry
    * Requests a node to save the connection
    * If it's successfully saved, it's added to the list of connections
    * After being called, caller calls UI remote method locally to add the Chat UI
     */
    public boolean requestStartConnection(String ip, int port, String name) throws RemoteException, NotBoundException {
        ConnectionRegistry connection = new ConnectionRegistry(ip, port, name, name);
        // Call public endpoint
        RemoteChat remoteInterface = connection.getRemoteNode();
        connections.add(connection);
        // Send own information
        boolean success = remoteInterface.startRemoteConnection(getMyConnection().ip, getMyConnection().port, getMyConnection().name);
        // TODO: Check if it really edits the array element or if it's a copy
        if(success)
            connection.endpoint = name + "-" + connections.get(0).name;
        return success;
    }


    /*
     * Called by the UI, receives the connection with the data returned by the server parsed to a ConnectionRegistry
     * Requests a node to save the connection
     * If it's successfully saved, it's added to the list of connections
     * After being called, caller calls UI remote method locally to remove the Chat UI
     */
    public void requestEndConnection(String name) throws RemoteException, NotBoundException {
        // Create remote connection
        RemoteChat remoteInterface = getRemoteNode(name);
        // Send own information
        boolean success = remoteInterface.endRemoteConnection(name);
        if(success)
            removeConnection(name);
    }

    public void addConnection(String ip, int port, String name) {
        String endpoint = name + "-" + connections.get(0).name;
        ConnectionRegistry connection = new ConnectionRegistry(ip, port, endpoint, name);
        connections.add(connection);
    }

    public void removeConnection(String name) {
        // Search for connection
        for(int i = 0; i < connections.size(); i++) {
            if(connections.get(i).name.equals(name)) {
                connections.remove(i);
                break;
            }
        }
    }

    // *********************************** Messages ***********************************

    public void sendMessage(String message, ConnectionRegistry registry) throws RemoteException, NotBoundException {
        // Create remote connection
        // Send message
        if(registry.name.equals("server")) {
            RemoteChat remoteInterface = getPublicRemoteNode(registry.name);
            remoteInterface.receiveBroadcastMessage(message, getMyConnection().name);
        } else {
            RemoteChat remoteInterface = getRemoteNode(registry.name);
            remoteInterface.receiveMessage(message);
        }
    }
    public ConnectionRegistry getMyConnection() {
        return connections.get(0);
    }
}
