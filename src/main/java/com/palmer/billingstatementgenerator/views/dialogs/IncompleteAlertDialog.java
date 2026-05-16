package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Modal alert shown when the user navigates away from a tab that has checked
 * items missing a required price or description.
 */
public class IncompleteAlertDialog extends MessageDialog {

    public IncompleteAlertDialog() {
        super("Incomplete Items", "One or more selected items are missing a required price or description.\n" +
                     "Please complete all selections before continuing.");
    }
}