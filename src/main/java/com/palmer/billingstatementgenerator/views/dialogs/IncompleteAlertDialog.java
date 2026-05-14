package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Modal alert shown when the user navigates away from a tab that has checked
 * items missing a required price or description.
 */
public class IncompleteAlertDialog extends AppDialog {

    /** {@inheritDoc} */
    @Override
    protected VBox buildContent() {
        Label message = new Label(
                "One or more selected items are missing a required price or description.\n" +
                        "Please complete all selections before continuing.");
        message.getStyleClass().add("splash-subtitle");
        message.setWrapText(true);
        message.setTextAlignment(TextAlignment.CENTER);
        message.setMaxWidth(320);

        Button ok = new Button("Got It");
        ok.setId("okButton");
        ok.setOnAction(e -> close());

        return contentBox("Incomplete Items", message, ok);
    }
}