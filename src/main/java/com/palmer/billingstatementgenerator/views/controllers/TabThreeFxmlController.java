package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.dao.ServicePackageDao;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.stream.Stream;

public class TabThreeFxmlController extends GridTabController<ServiceLineItem> {

    private ComboBox<ServicePackage> packagesCombo;

    @Override
    public GridPane buildView() {
        ServicePackageDao dao = new ServicePackageDao(Database.get());
        packagesCombo = new ComboBox<>();
        packagesCombo.getItems().addAll(dao.findAll());
        packagesCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ServicePackage p) {
                return p == null ? "" : String.format("%-15s %14s", p.getName(), DOLLAR_FORMATTER.format(p.getDefaultCost()));
            }
            @Override
            public ServicePackage fromString(String s) { return null; }
        });

        GridPane pane = new GridPane();
        pane.setHgap(7);
        pane.setVgap(5);
        GridPane.setConstraints(packagesCombo, 0, 0);
        GridPane.setColumnSpan(packagesCombo, 2);
        pane.getChildren().add(packagesCombo);

        GridPane itemsPane = super.buildView();
        GridPane.setConstraints(itemsPane, 0, 1);
        GridPane.setColumnSpan(itemsPane, 2);
        pane.getChildren().add(itemsPane);

        return pane;
    }

    @Override
    protected List<ServiceLineItem> getItems() {
        return StatementContext.current().getServices();
    }

    @Override
    protected CheckBox addItemRow(ServiceLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        buildPriceLabel(item.getCatalog().getDefaultCost(), row, itemsGrid);
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