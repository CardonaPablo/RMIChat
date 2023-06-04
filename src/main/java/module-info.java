module com.example.chatrmi {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.chatrmi to javafx.fxml;
    exports com.example.chatrmi;
}