package com.example.chatrmi.connection;

import com.example.chatrmi.RemoteChat;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class ChatNode {
    public List<ConnectionRegistry> connections = new ArrayList<>();
    protected ChatNode(String ip, int port) throws RemoteException {
        // Register self
        ConnectionRegistry myConnection = new ConnectionRegistry(ip, port);
        connections.add(myConnection);
        // TODO: Export remote interface in default endpoint /
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

    // *********************************** Connection ***********************************

    /*
    * Called by the UI, recieves the connection with the data returned by the server parsed to a ConnectionRegistry
    * Requests a node to save the connection
    * If it's successfully saved, it's added to the list of connections
    * After being called, caller calls UI remote method locally to add the Chat UI
     */
    public boolean requestStartConnection(String ip, int port, String name) throws RemoteException, NotBoundException {
        // TODO: Export interface with endpoint name here or before calling this
        ConnectionRegistry connection = new ConnectionRegistry(ip, port, name, name);
        RemoteChat remoteInterface = getRemoteNode(name);
        connections.add(connection);
        // Send own information
        boolean success = remoteInterface.startRemoteConnection(connections.get(0).ip, connections.get(0).port, connections.get(0).name);
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
        // TODO: Export remote interface with endpoint name here or after calling this
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

    void sendUnicastMessage(String message, String name) throws RemoteException, NotBoundException {
        // Create remote connection
        RemoteChat remoteInterface = getRemoteNode(name);
        // Send message
        remoteInterface.receiveUnicastMessage(message);
    }

    void sendBroadcastMessageToServer(String message) throws RemoteException, NotBoundException {
        // Create remote connection
        RemoteChat remoteInterface = getRemoteNode("Server");
        remoteInterface.receiveBroadcastMessage(message, this.connections.get(0).name);
    }
}
