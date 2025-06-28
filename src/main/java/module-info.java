module com.mycompany.javafx_dashboard_lita {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.javafx_dashboard_lita to javafx.fxml;
    exports com.mycompany.javafx_dashboard_lita;
}
