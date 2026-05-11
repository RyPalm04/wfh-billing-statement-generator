package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.dao.ServicePackageDao;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.Statement;
import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * FXML controller for TabThree. Populates package combo and service checkboxes.
 */
public class TabThreeFxmlController extends BaseController {

    @FXML private ComboBox<ServicePackage> packagesCombo;
    @FXML private GridPane servicesGrid;

    private Button clearButton;

    @FXML
    private void initialize() {
        // populate packages
        ServicePackageDao dao = new ServicePackageDao(Database.get());
        List<ServicePackage> packageCatalog = dao.findAll();
        packagesCombo.getItems().addAll(packageCatalog);
        packagesCombo.setConverter(new javafx.util.StringConverter<ServicePackage>() {
            @Override
            public String toString(ServicePackage p) {
                return p == null ? "" : String.format("%-15s %14s", p.getName(), GeneratorTabs.DOLLAR_FORMATTER.format(p.getDefaultCost()));
            }
            @Override
            public ServicePackage fromString(String s) { return null; }
        });

        // populate servicesGrid from the current statement (services list)
        Statement stmt = StatementContext.current();
        List<ServiceLineItem> services = stmt.getServices();

        int row = 0;
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (ServiceLineItem item : services) {
            CheckBox cb = new CheckBox(item.getCatalog().getName());
            Label price = new Label(GeneratorTabs.DOLLAR_FORMATTER.format(item.getCatalog().getDefaultCost()));
            GridPane.setConstraints(cb, 0, row);
            GridPane.setConstraints(price, 1, row);
            servicesGrid.getChildren().addAll(cb, price);
            cb.selectedProperty().bindBidirectional(item.selectedProperty());
            checkBoxes.add(cb);
            row++;
        }

        // wire clear button when available (TabThree.java will call setClearButton)
        // when clearButton is set, controller will attach behavior
        if (clearButton != null) {
            wireClear(checkBoxes, packagesCombo);
        }
    }

    public void setClearButton(Button clearButton) {
        this.clearButton = clearButton;
        // if services are already populated, wire them
        if (servicesGrid != null && packagesCombo != null) {
            // collect checkboxes
            List<CheckBox> checkBoxes = new ArrayList<>();
            for (javafx.scene.Node n : servicesGrid.getChildren()) {
                if (n instanceof CheckBox) checkBoxes.add((CheckBox) n);
            }
            wireClear(checkBoxes, packagesCombo);
        }
    }

    private void wireClear(List<CheckBox> checkBoxes, ComboBox<ServicePackage> packages) {
        Observable[] deps = Stream.concat(
                checkBoxes.stream().map(CheckBox::selectedProperty),
                Stream.of(packages.valueProperty())
        ).toArray(Observable[]::new);
        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected) && packages.getValue() == null,
                deps));

        clearButton.setOnAction(e -> {
            packages.setValue(null);
            checkBoxes.forEach(cb -> cb.setSelected(false));
        });
    }
}
