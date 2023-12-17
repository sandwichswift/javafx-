module com.myapp.myapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires org.jsoup;


    opens com.myapp.myapp to javafx.fxml;
    exports com.myapp.myapp;
}