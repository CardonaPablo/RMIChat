package com.example.chatrmi.connection;

import com.example.chatrmi.RemoteChat;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ConnectionRegistry {
    public String ip;
    public int port;
    public String endpoint; // Structure Destination-Source
    public String name;

    public ConnectionRegistry(String ip, int port, String endpoint, String name) {
        this.ip = ip;
        this.port = port;
        this.endpoint = endpoint;
        this.name = name;
    }

    public void exportPublicEndpoint(RemoteChat object) throws RemoteException {
        // Test the registry
        Registry registry = LocateRegistry.getRegistry(port);
        // Export the InterfaceController remotely
        try {
            registry.rebind(endpoint, object);
        } catch (RemoteException e) {
            registry = LocateRegistry.createRegistry(port);
            registry.rebind(name, object);
        }
        System.err.println("Public endpoint listening...");
    }


    public void exportEndpoint(String target, RemoteChat object) throws RemoteException {
        // Export the InterfaceController remotely
        Registry registry = LocateRegistry.getRegistry(port);
        registry.rebind(name + "-" + target, object);
        System.err.println("Server listening...");
    }

    public RemoteChat getRemoteNode() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(ip, port);
        RemoteChat remoteInterface = (RemoteChat) registry.lookup(endpoint);
        return remoteInterface;
    }
}

