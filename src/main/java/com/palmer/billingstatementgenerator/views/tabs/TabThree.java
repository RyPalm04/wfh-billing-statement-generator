package com.palmer.billingstatementgenerator.views.tabs;

import com.palmer.billingstatementgenerator.dao.ServicePackageDao;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.Statement;
import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TabThree extends GeneratorTabs {

    public static final String PACKAGE_PROMPT = "Package Selection...";

    public TabThree(String tabTitle) {
        super(tabTitle);
    }

    @Override
    protected void addForm() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/palmer/billingstatementgenerator/views/tab_three.fxml"));
            javafx.scene.layout.GridPane loaded = loader.load();
            // merge into GeneratorTabs grid
            grid.getColumnConstraints().addAll(loaded.getColumnConstraints());
            grid.getChildren().addAll(loaded.getChildren());

            // pass clear button to controller so it can wire clear behavior
            Object ctrl = loader.getController();
            if (ctrl instanceof com.palmer.billingstatementgenerator.views.controllers.TabThreeFxmlController) {
                ((com.palmer.billingstatementgenerator.views.controllers.TabThreeFxmlController) ctrl).setClearButton(clearButton);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
