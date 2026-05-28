package com.palmer.billingstatementgenerator.views.dialogs;

import com.palmer.billingstatementgenerator.services.PdfService;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Modal dialog offering Save to Computer or Print for the current statement.
 */
public class PdfDialog extends AppDialog<Void> {

    private final PdfService pdfService = PdfService.getInstance();

    /**
     * {@inheritDoc}
     */
    @Override
    protected VBox buildContent() {
        Label message = new Label("How would you like to output this statement?");
        message.getStyleClass().add("splash-subtitle");

        Button saveBtn = new Button("Save to Computer");
        saveBtn.setId("pdfSaveButton");
        saveBtn.setOnAction(e -> {
            close();
            pdfService.export(getOwner());
        });

        Button printButton = new Button("Print");
        printButton.setId("pdfPrintButton");
        printButton.setOnAction(e -> {
            close();
            pdfService.print(getOwner());
        });

        Button cancel = new Button("Cancel");
        cancel.setId("pdfCancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> close());

        HBox buttons = new HBox(12, saveBtn, printButton, cancel);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Generate PDF", message, buttons);
    }
}