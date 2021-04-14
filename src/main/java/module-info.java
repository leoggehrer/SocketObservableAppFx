module org.example {
    requires javafx.controls;
    requires javafx.fxml;

    opens org.htlleo to javafx.fxml;
    exports org.htlleo;
}