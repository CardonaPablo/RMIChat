package com.example.chatrmi;

import com.example.chatrmi.connection.ChatNode;

import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ServerNode extends ChatNode {

    public ServerNode(int port) throws RemoteException, UnknownHostException {
        super( "server", port);
    }

    public void sendBroadcastMessage(String message, String author) {
       connections.forEach(connection -> {
           if(connection.name.equals("server")) return; // Skip server (this node)
           try {
               RemoteChat remoteInterface = getRemoteNode(connection.name);
               remoteInterface.receiveBroadcastMessage(message, author);
           } catch (RemoteException e) {
           } catch (NotBoundException e) {
           }
       });
    }

    public List<String> getConnectionsInfo() {
        List<String> connectionsInfo = new ArrayList<>();
        connections.forEach(connection -> {
            connectionsInfo.add(connection.name + "-" + connection.ip + ":" + connection.port);
        });
        return connectionsInfo;
    }

    public void registerClient(String ip, int port, String name) throws RemoteException, NotBoundException {
        addConnection(ip, port, name);
        // TODO: Export interface with endpoint name here or before calling this
    }
}
