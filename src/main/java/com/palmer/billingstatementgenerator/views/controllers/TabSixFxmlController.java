package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.pdf.PdfGenerator;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.List;

public class TabSixFxmlController extends GridTabController<CashAdvanceLineItem> {

    @Override
    protected List<CashAdvanceLineItem> getItems() {
        return StatementContext.current().getCashAdvances();
    }

    @Override
    protected CheckBox addItemRow(CashAdvanceLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        TextField provider = buildTextField(row, itemsGrid);

        cb.selectedProperty().bindBidirectional(item.selectedProperty());
        provider.textProperty().bindBidirectional(item.providerProperty());
        wireTextFieldToCheckBox(provider, cb);

        return cb;
    }

    @Override
    protected void onNextButtonSet() {
        if (nextButton != null) {
            nextButton.setText("Generate PDF");
            nextButton.setOnAction(e -> generatePdf());
        }
    }

    private void generatePdf() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Statement as PDF");
        fc.getExtensionFilters().add(new ExtensionFilter("PDF files", "*.pdf"));
        fc.setInitialFileName("statement-" + StatementContext.current().getControlNumber() + ".pdf");
        File output = fc.showSaveDialog(nextButton.getScene().getWindow());
        if (output == null) return;
        try {
            PdfGenerator.generate(StatementContext.current(), output);
            new Alert(Alert.AlertType.INFORMATION, "PDF saved to:\n" + output.getAbsolutePath()).showAndWait();
        } catch (Throwable t) {
            new Alert(Alert.AlertType.ERROR, "Failed to generate PDF: " + t.getMessage()).showAndWait();
        }
    }
}