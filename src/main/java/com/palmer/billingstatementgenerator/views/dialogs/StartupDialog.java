package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Modal startup dialog offering New Statement or Open Existing.
 * The selected {@link Choice} is returned directly by {@link #open()}.
 */
public class StartupDialog extends AppDialog<StartupDialog.Choice> {

    private final boolean savedExists;

    /**
     * Creates the startup dialog.
     *
     * @param savedExists
     *         whether any saved statements exist; disables "Open Existing" if {@code false}
     */
    public StartupDialog(boolean savedExists) {
        this.savedExists = savedExists;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected VBox buildContent() {
        Label message = new Label("What would you like to do?");
        message.getStyleClass().add("splash-subtitle");

        Button newStatement = new Button("New Statement");
        newStatement.setId("newStatementButton");
        newStatement.setOnAction(e -> {
            result = Choice.NEW;
            close();
        });

        Button openExisting = new Button("Open Existing");
        openExisting.setId("openExistingButton");
        openExisting.setDisable(!savedExists);
        openExisting.setOnAction(e -> {
            result = Choice.OPEN;
            close();
        });

        HBox buttons = new HBox(16, newStatement, openExisting);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Wright Funeral Home", message, buttons);
    }

    /**
     * The action the user selected when the dialog closed.
     */
    public enum Choice {NEW, OPEN, CANCEL}
}