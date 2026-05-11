package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.pdf.PdfGenerator;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TabSixFxmlController extends BaseController {

    @FXML private GridPane itemsGrid;
    private Button clearButton;
    private Button nextButton;

    @FXML
    private void initialize() {
        List<CashAdvanceLineItem> items = StatementContext.current().getCashAdvances();
        int row = 0;
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (CashAdvanceLineItem item : items) {
            CheckBox cb = new CheckBox(item.getCatalog().getName());
            TextField provider = new TextField();
            provider.setPrefColumnCount(18);
            GridPane.setConstraints(cb, 0, row);
            GridPane.setConstraints(provider, 1, row);
            itemsGrid.getChildren().addAll(cb, provider);
            cb.selectedProperty().bindBidirectional(item.selectedProperty());
            provider.textProperty().bindBidirectional(item.providerProperty());
            checkBoxes.add(cb);
            row++;
        }

        if (clearButton != null) {
            wireClear(checkBoxes);
        }
    }

    public void setClearButton(Button clearButton) {
        this.clearButton = clearButton;
        if (itemsGrid != null) {
            List<CheckBox> checkBoxes = new ArrayList<>();
            for (javafx.scene.Node n : itemsGrid.getChildren()) {
                if (n instanceof CheckBox) checkBoxes.add((CheckBox) n);
            }
            wireClear(checkBoxes);
        }
    }

    public void setNextButton(Button nextButton) {
        this.nextButton = nextButton;
        if (nextButton != null) {
            nextButton.setText("Generate PDF");
            nextButton.setOnAction(e -> generatePdf());
        }
    }

    private void wireClear(List<CheckBox> checkBoxes) {
        Observable[] deps = checkBoxes.stream().map(CheckBox::selectedProperty).toArray(Observable[]::new);
        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected),
                deps));
        clearButton.setOnAction(e -> checkBoxes.forEach(cb -> cb.setSelected(false)));
    }

    private void generatePdf() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Statement as PDF");
        fc.getExtensionFilters().add(new ExtensionFilter("PDF files", "*.pdf"));
        int controlNumber = StatementContext.current().getControlNumber();
        fc.setInitialFileName("statement-" + controlNumber + ".pdf");
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
