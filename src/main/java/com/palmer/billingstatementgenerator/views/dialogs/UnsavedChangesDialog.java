package com.palmer.billingstatementgenerator.views.dialogs;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Modal dialog warning the user of unsaved changes before a destructive action.
 */
public class UnsavedChangesDialog extends AppDialog {

    private final Runnable onSaveAndContinue;
    private final Runnable onDiscard;

    /**
     * Creates the unsaved-changes dialog.
     *
     * @param onSaveAndContinue
     *         action to run when the user chooses "Save &amp; Continue"
     * @param onDiscard
     *         action to run when the user chooses "Discard"
     */
    public UnsavedChangesDialog(Runnable onSaveAndContinue, Runnable onDiscard) {
        this.onSaveAndContinue = onSaveAndContinue;
        this.onDiscard = onDiscard;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected VBox buildContent() {
        Label message = new Label("You have unsaved changes. Save before continuing?");
        message.getStyleClass().add("splash-subtitle");

        Button save = new Button("Save & Continue");
        save.setId("saveContinueButton");
        save.setOnAction(e -> {
            close();
            onSaveAndContinue.run();
        });

        Button discard = new Button("Discard");
        discard.setId("discardButton");
        discard.getStyleClass().add("button-reset");
        discard.setOnAction(e -> {
            close();
            onDiscard.run();
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> close());

        HBox buttons = new HBox(12, save, discard, cancel);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Unsaved Changes", message, buttons);
    }
}