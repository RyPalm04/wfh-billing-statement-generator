package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Modal dialog offering New Statement, Open Existing, Clear Selections, or Cancel.
 * The user's choice is available via {@link #getChoice()} after {@link #open()} returns.
 */
public class ResetStatementDialog extends AppDialog {

    private Choice choice = Choice.CANCEL;

    /**
     * Returns the choice the user made. Valid after {@code open()} returns.
     *
     * @return the selected {@link Choice}
     */
    public Choice getChoice() {
        return choice;
    }

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
            choice = Choice.NEW;
            close();
        });

        Button openExisting = new Button("Open Existing");
        openExisting.setId("openExistingButton");
        openExisting.setOnAction(e -> {
            choice = Choice.OPEN;
            close();
        });

        Button clearSelections = new Button("Clear Selections");
        clearSelections.setId("clearSelectionsButton");
        clearSelections.setOnAction(e -> {
            choice = Choice.CLEAR;
            close();
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> close());

        HBox buttons = new HBox(12, newStatement, openExisting, clearSelections, cancel);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Reset Statement", message, buttons);
    }

    /**
     * The action the user selected when the dialog closed.
     */
    public enum Choice {NEW, OPEN, CLEAR, CANCEL}
}