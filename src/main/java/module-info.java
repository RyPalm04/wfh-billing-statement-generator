module com.palmer.billingstatementgenerator {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires validatorfx;

    opens com.palmer.billingstatementgenerator to javafx.fxml;
    exports com.palmer.billingstatementgenerator;
}
