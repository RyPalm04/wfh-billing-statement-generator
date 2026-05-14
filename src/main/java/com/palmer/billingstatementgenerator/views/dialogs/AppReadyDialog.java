package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Modal startup dialog offering New Statement or Open Existing.
 * The user's choice is available via {@link #getChoice()} after {@link #open()} returns.
 */
public class AppReadyDialog extends AppDialog {

    /** The action the user selected when the dialog closed. */
    public enum Choice { NEW, OPEN, CANCEL }

    private final boolean savedExists;
    private Choice choice = Choice.CANCEL;

    /**
     * Creates the startup dialog.
     *
     * @param savedExists
     *         whether any saved statements exist; disables "Open Existing" if {@code false}
     */
    public AppReadyDialog(boolean savedExists) {
        this.savedExists = savedExists;
    }

    /**
     * Returns the choice the user made. Valid after {@code open()} returns.
     *
     * @return the selected {@link Choice}
     */
    public Choice getChoice() {
        return choice;
    }

    /** {@inheritDoc} */
    @Override
    protected VBox buildContent() {
        Label message = new Label("What would you like to do?");
        message.getStyleClass().add("splash-subtitle");

        Button newStatement = new Button("New Statement");
        newStatement.setId("newStatementButton");
        newStatement.setOnAction(e -> { choice = Choice.NEW; close(); });

        Button openExisting = new Button("Open Existing");
        openExisting.setId("openExistingButton");
        openExisting.setDisable(!savedExists);
        openExisting.setOnAction(e -> { choice = Choice.OPEN; close(); });

        HBox buttons = new HBox(16, newStatement, openExisting);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Wright Funeral Home", message, buttons);
    }
}