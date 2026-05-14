package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.dao.ServicePackageDao;
import com.palmer.billingstatementgenerator.db.Database;
import com.palmer.billingstatementgenerator.models.catalog.ServicePackage;
import com.palmer.billingstatementgenerator.models.lineitems.ServiceLineItem;
import com.palmer.billingstatementgenerator.models.statement.StatementContext;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;

import java.util.List;
import java.util.stream.Stream;

/**
 * Controller for the Services, Facilities &amp; Transportation tab.
 * Extends {@link GridTabController} with an additional service package {@link ComboBox}
 * above the services grid. When a package is selected, service checkboxes are automatically
 * set to match the package's defined service list. Overrides clear button configuration
 * to account for the combo box selection, and overrides {@link #clearAll()} to also reset
 * the combo box.
 */
public class ServicesController extends GridTabController<ServiceLineItem> {

    /**
     * Combo box for selecting a pre-defined service package.
     */
    private ComboBox<ServicePackage> packagesCombo;

    /**
     * Builds the tab view by first populating the packages combo box,
     * then delegating to the parent to build the services grid.
     * The combo and grid are arranged vertically in a wrapper {@link GridPane}.
     *
     * @return a {@link GridPane} containing the combo box and services grid
     */
    @Override
    public GridPane buildView() {
        ServicePackageDao dao = new ServicePackageDao(Database.get());
        packagesCombo = new ComboBox<>();
        packagesCombo.setId("packagesCombo");
        packagesCombo.getItems().addAll(dao.findAll());
        packagesCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ServicePackage p) {
                return p == null ? "" : String.format("%-15s %14s", p.getName(), DOLLAR_FORMATTER.format(p.getDefaultCost()));
            }

            @Override
            public ServicePackage fromString(String s) {
                return null;
            }
        });
        packagesCombo.valueProperty().bindBidirectional(StatementContext.current().selectedPackageProperty());
        packagesCombo.valueProperty().addListener((obs, oldPkg, newPkg) -> {
            if (newPkg == null) {
                StatementContext.current().getServices().forEach(item -> item.setInPackage(false));
                refreshTotal();
                return;
            }
            List<Integer> serviceIds = dao.findServiceIdsForPackage(newPkg.getId());
            StatementContext.current().getServices().forEach(item -> {
                boolean inPkg = serviceIds.contains(item.getCatalog().getId());
                item.setInPackage(inPkg);
                item.setSelected(inPkg);
            });
            refreshTotal();
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

    /**
     * Returns the list of service line items from the current statement.
     *
     * @return the list of {@link ServiceLineItem} objects
     */
    @Override
    protected List<ServiceLineItem> getItems() {
        return StatementContext.current().getServices();
    }

    /**
     * Builds a row for the given service line item, consisting of a checkbox
     * and a read-only price label.
     *
     * @param item
     *         the service line item to render
     * @param row
     *         the grid row index
     *
     * @return the {@link CheckBox} created for this row
     */
    @Override
    protected CheckBox addItemRow(ServiceLineItem item, int row) {
        CheckBox cb = buildCheckBox(item.getCatalog().getName(), row, itemsGrid);
        buildPriceLabel(item.getCatalog().getDefaultCost(), row, itemsGrid);
        cb.selectedProperty().bindBidirectional(item.selectedProperty());
        cb.disableProperty().bind(item.inPackageProperty());
        return cb;
    }

    /**
     * Overrides clear button configuration to also observe the packages combo box value.
     * The clear button is disabled only when no checkboxes are selected AND no package
     * is selected.
     *
     * @param checkBoxes
     *         the list of service checkboxes to observe
     */
    @Override
    protected void configureClearButton(List<CheckBox> checkBoxes) {
        Observable[] clearButtonDependencies = Stream.concat(
                checkBoxes.stream().map(CheckBox::selectedProperty),
                Stream.of(packagesCombo.valueProperty())
        ).toArray(Observable[]::new);

        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected) && packagesCombo.getValue() == null,
                clearButtonDependencies));

        clearButton.setOnAction(e -> clearAll());
    }

    /**
     * Clears all service checkboxes and resets the packages combo box selection.
     */
    @Override
    protected void clearAll() {
        super.clearAll();
        packagesCombo.setValue(null);
    }
}