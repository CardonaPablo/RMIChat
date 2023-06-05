package com.example.chatrmi.connection;

public class ConnectionRegistry {
    public String ip;
    public int port;
    public String endpoint; // Structure Destination-Source
    public String name;
    public ConnectionRegistry(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.endpoint = "";
        this.name = "Me";
    }

    public ConnectionRegistry(String ip, int port, String endpoint, String name) {
        this.ip = ip;
        this.port = port;
        this.endpoint = endpoint;
        this.name = name;
    }
}
