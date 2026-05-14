package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Modal dialog offering New Statement, Open Existing, Clear Selections, or Cancel.
 * The selected {@link Choice} is returned directly by {@link #open()}.
 */
public class ResetStatementDialog extends AppDialog<ResetStatementDialog.Choice> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected VBox buildContent() {
        Label message = new Label("How would you like to proceed?");
        message.getStyleClass().add("splash-subtitle");

        Button newStatement = new Button("New Statement");
        newStatement.setId("newStatementButton");
        newStatement.setOnAction(e -> {
            result = Choice.NEW;
            close();
        });

        Button openExisting = new Button("Open Existing");
        openExisting.setId("openExistingButton");
        openExisting.setOnAction(e -> {
            result = Choice.OPEN;
            close();
        });

        Button clearSelections = new Button("Clear Selections");
        clearSelections.setId("clearSelectionsButton");
        clearSelections.setOnAction(e -> {
            result = Choice.CLEAR;
            close();
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> {
            result = Choice.CANCEL;
            close();
        });

        HBox buttons = new HBox(12, newStatement, openExisting, clearSelections, cancel);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Reset Statement", message, buttons);
    }

    /**
     * The action the user selected when the dialog closed.
     */
    public enum Choice {NEW, OPEN, CLEAR, CANCEL}
}