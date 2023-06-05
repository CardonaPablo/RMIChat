package com.example.chatrmi.ui;

import com.example.chatrmi.ui.client.ClientInterfaceController;
import com.example.chatrmi.ui.server.ServerInterfaceController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class ConnectionDialogController implements Initializable {

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
        int portClient = Integer.parseInt(clientPort.getText());
        String ipServer = serverIP.getText();
        int portServer = Integer.parseInt(serverPort.getText());
        String clientName = this.name.getText();

        Stage stage = (Stage) connectButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("client/ClientInterface.fxml"));

        try {
            Parent root = loader.load();
            ClientInterfaceController controller = loader.getController();
            controller.initialize(ipServer, portServer, clientName, portClient);

            stage.setTitle("Chat - " + clientName);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startServer() throws RemoteException, UnknownHostException {
        int port = Integer.parseInt(localPort.getText());
        // Start the UI
        Stage stage = (Stage) connectButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("server/ServerInterface.fxml"));
        try {
            Parent root = loader.load();
            ServerInterfaceController controller = loader.getController();
            controller.initialize(port);
            stage.setTitle("Server");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
