package com.palmer.billingstatementgenerator.views.tabs;

import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.CashAdvanceLineItem;
import com.palmer.billingstatementgenerator.pdf.PdfGenerator;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import java.io.File;
import java.util.List;

public class TabSix extends GeneratorTabs {

    public TabSix(String tabTitle) {
        super(tabTitle);
    }

    @Override
    protected void addForm() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/palmer/billingstatementgenerator/views/tab_six.fxml"));
            javafx.scene.layout.GridPane loaded = loader.load();
            grid.getColumnConstraints().addAll(loaded.getColumnConstraints());
            grid.getChildren().addAll(loaded.getChildren());
            Object ctrl = loader.getController();
            if (ctrl instanceof com.palmer.billingstatementgenerator.views.controllers.TabSixFxmlController) {
                ((com.palmer.billingstatementgenerator.views.controllers.TabSixFxmlController) ctrl).setClearButton(clearButton);
                ((com.palmer.billingstatementgenerator.views.controllers.TabSixFxmlController) ctrl).setNextButton(nextButton);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
