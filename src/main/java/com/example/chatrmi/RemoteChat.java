package com.example.chatrmi;

import java.util.List;

public interface RemoteChat extends java.rmi.Remote {
    /*
    * Recieves the connection data from the client.
    * After creating the connection
     */
    // De acceso público con endpoint /nombre
    public boolean startRemoteConnection(String ip, int port, String name) throws java.rmi.RemoteException;
    // Requieren endpoint custom
    public boolean endRemoteConnection(String name) throws java.rmi.RemoteException;
    public void receiveMessage(String message) throws java.rmi.RemoteException;
    // Recibe a través de connectionRegistry con server
    public void receiveBroadcastMessage(String message, String name) throws java.rmi.RemoteException;
    public List<String> getAllClients() throws java.rmi.RemoteException;

}
