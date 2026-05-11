package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.dao.ServicePackageDao;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Stream;

public class TabThreeFxmlController extends GridTabController<ServiceLineItem> {

    @FXML private ComboBox<ServicePackage> packagesCombo;

    @FXML
    @Override
    protected void initialize() {
        ServicePackageDao dao = new ServicePackageDao(Database.get());
        packagesCombo.getItems().addAll(dao.findAll());
        packagesCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServicePackage p) {
                return p == null ? "" : String.format("%-15s %14s", p.getName(), DOLLAR_FORMATTER.format(p.getDefaultCost()));
            }
            @Override
            public ServicePackage fromString(String s) { return null; }
        });

        super.initialize();
    }

    @Override
    protected List<ServiceLineItem> getItems() {
        return StatementContext.current().getServices();
    }

    @Override
    protected CheckBox addItemRow(ServiceLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        buildPriceLabel(item.getCatalog().getDefaultCost(), 1, row, itemsGrid);
        cb.selectedProperty().bindBidirectional(item.selectedProperty());
        return cb;
    }

    @Override
    protected void wireClear(List<CheckBox> checkBoxes) {
        Observable[] deps = Stream.concat(
                checkBoxes.stream().map(CheckBox::selectedProperty),
                Stream.of(packagesCombo.valueProperty())
        ).toArray(Observable[]::new);

        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected) && packagesCombo.getValue() == null,
                deps));

        clearButton.setOnAction(e -> {
            packagesCombo.setValue(null);
            checkBoxes.forEach(cb -> cb.setSelected(false));
        });
    }
}