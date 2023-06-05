module com.example.chatrmi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;


    opens com.example.chatrmi to javafx.fxml;
    exports com.example.chatrmi;
    exports com.example.chatrmi.ui;
    opens com.example.chatrmi.ui to javafx.fxml;
}