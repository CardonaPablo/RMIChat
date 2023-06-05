package com.example.chatrmi.ui;

import com.example.chatrmi.ClientNode;
import com.example.chatrmi.ServerNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class ConnectionDialogController implements Initializable {

    @FXML
    private Parent root;
    @FXML
    private RadioButton clientRadio;
    @FXML
    private RadioButton serverRadio;
    @FXML
    private Button connectButton;

    // Client
    @FXML
    private TextField clientPort;
    @FXML
    private TextField name;

    @FXML
    private TextField serverIP;
    @FXML
    private TextField serverPort;

    // Server
    @FXML
    private TextField localPort;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientRadio.setSelected(true);
        serverRadio.setSelected(false);
        clientPort.setDisable(false);
        serverIP.setDisable(false);
        serverPort.setDisable(false);
        localPort.setDisable(true);
        connectButton.setOnMouseClicked(event -> onButtonClicked());
    }

    public void onButtonClicked() {
        if (clientRadio.isSelected()) {
            try {
                startClient();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (serverRadio.isSelected()) {
            try {
                startServer();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }

    private void startClient() throws UnknownHostException, RemoteException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        int port = Integer.parseInt(clientPort.getText());
        ClientNode client = new ClientNode(ip, port);
        // Get the server data
        String ipServer = serverIP.getText();
        int portServer = Integer.parseInt(serverPort.getText());
        String clientName = this.name.getText();
                // Connect
        // *TODO: Test in prod: client.startServerConnection(ipServer, portServer);
        // Start the UI
        // Get the controller and invoke a method to create companion classes inside controller
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ClientInterface.fxml"));
        try {
            AnchorPane pane = loader.load();
            ClientInterfaceController controller = loader.getController();
            // TODO: Call method to create server connection inside controller
            stage.setTitle("Chat - " + clientName);
            stage.setScene(new Scene(pane));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws RemoteException, UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        int port = Integer.parseInt(localPort.getText());
        ServerNode server = new ServerNode(ip, port);
        // Start the UI
        Stage stage = (Stage) root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ServerInterface.fxml"));
        try {
            stage.setScene(new Scene(fxmlLoader.load()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}