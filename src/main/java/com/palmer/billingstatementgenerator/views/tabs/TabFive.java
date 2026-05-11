package com.palmer.billingstatementgenerator.views.tabs;

import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.SpecialChargeLineItem;

import java.util.List;

public class TabFive extends GeneratorTabs {

    public TabFive(String tabTitle) {
        super(tabTitle);
    }

    @Override
    protected void addForm() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/palmer/billingstatementgenerator/views/tab_five.fxml"));
            javafx.scene.layout.GridPane loaded = loader.load();
            grid.getColumnConstraints().addAll(loaded.getColumnConstraints());
            grid.getChildren().addAll(loaded.getChildren());
            Object ctrl = loader.getController();
            if (ctrl instanceof com.palmer.billingstatementgenerator.views.controllers.TabFiveFxmlController) {
                ((com.palmer.billingstatementgenerator.views.controllers.TabFiveFxmlController) ctrl).setClearButton(clearButton);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
