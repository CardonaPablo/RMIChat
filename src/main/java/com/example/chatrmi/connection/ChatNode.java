package com.example.chatrmi.connection;

import com.example.chatrmi.RemoteChat;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class ChatNode {
    public List<ConnectionRegistry> connections = new ArrayList<>();

    protected ChatNode(String name, int port) throws RemoteException, UnknownHostException {
        // Register self
        String ip = null;
        try {
            ip = getMyIP();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        ConnectionRegistry myConnection = new ConnectionRegistry(ip, port, name, name);
        connections.add(myConnection);

    }

    String getMyIP() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            System.out.println("Interface name: " + netint.getName());
            System.out.println("IP address" + netint.getHardwareAddress());
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                System.out.printf("InetAddress: %s\n", inetAddress);
            }
        }
        return "192.168.0.22";
    }
    // *********************************** Connection ***********************************

    /*
    * Called by the UI, receives the connection with the data returned by the server parsed to a ConnectionRegistry
    * Requests a node to save the connection
    * If it's successfully saved, it's added to the list of connections
    * After being called, caller calls UI remote method locally to add the Chat UI
     */
    public ConnectionRegistry requestStartConnection(String ip, int port, String name) throws RemoteException, NotBoundException {
        ConnectionRegistry connection = new ConnectionRegistry(ip, port, name, name);
        // Call public endpoint
        RemoteChat remoteInterface = name.equals("server") ? connection.getPublicRemoteChat() : connection.getRemotePrivateChat();
        connections.add(connection);
        // Send own information
        boolean success = remoteInterface.startRemoteConnection(getMyConnection().ip, getMyConnection().port, getMyConnection().name);
        if(success)
            connection.endpoint = name + "-" + connections.get(0).name;
        return connection;
    }


    /*
     * Called by the UI, receives the connection with the data returned by the server parsed to a ConnectionRegistry
     * Requests a node to save the connection
     * If it's successfully saved, it's added to the list of connections
     * After being called, caller calls UI remote method locally to remove the Chat UI
     */
    public void requestEndConnection(ConnectionRegistry connection) throws RemoteException, NotBoundException {
        // Create remote connection
        RemoteChat remoteInterface = connection.getRemotePrivateChat();
        // Send own information
        // TODO: See if it's necessary to send the  name
        boolean success = remoteInterface.endRemoteConnection(connection.name);
        if(success)
            removeConnection(connection.name);
    }

    public ConnectionRegistry addConnection(String ip, int port, String name) {
        String endpoint = name + "-" + connections.get(0).name;
        ConnectionRegistry connection = new ConnectionRegistry(ip, port, endpoint, name);
        connections.add(connection);
        return connection;
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
            RemoteChat remoteInterface = registry.getPublicRemoteChat();
            remoteInterface.receiveBroadcastMessage(message, getMyConnection().name);
       } else {
            RemoteChat remoteInterface = registry.getRemotePrivateChat();
            remoteInterface.receiveMessage(message);
        }
    }
    public ConnectionRegistry getMyConnection() {
        return connections.get(0);
    }
}
