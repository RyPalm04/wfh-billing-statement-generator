package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * Modal dialog for displaying a single informational or error message.
 * Presents a title, a wrapped message body, and an OK button.
 */
public class MessageDialog extends AppDialog<Void> {

    private final String title;
    private final String message;

    /**
     * Creates the message dialog.
     *
     * @param title
     *         the dialog heading text
     * @param message
     *         the body message to display
     */
    public MessageDialog(String title, String message) {
        this.title = title;
        this.message = message;
    }

    /** {@inheritDoc} */
    @Override
    protected VBox buildContent() {
        Label msg = new Label(message);
        msg.getStyleClass().add("splash-subtitle");
        msg.setWrapText(true);
        msg.setMaxWidth(360);
        msg.setTextAlignment(TextAlignment.CENTER);
        msg.setAlignment(Pos.CENTER);

        Button ok = new Button("OK");
        ok.setId("okButton");
        ok.setOnAction(e -> close());

        return contentBox(title, msg, ok);
    }
}