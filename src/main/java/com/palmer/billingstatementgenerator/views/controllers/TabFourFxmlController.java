package com.palmer.billingstatementgenerator.views.controllers;

import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.models.lineitems.MerchandiseLineItem;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class TabFourFxmlController extends BaseController {

    @FXML private GridPane itemsGrid;
    private Button clearButton;

    @FXML
    private void initialize() {
        List<MerchandiseLineItem> items = StatementContext.current().getMerchandise();
        int row = 0;
        List<CheckBox> checkBoxes = new ArrayList<>();
        for (MerchandiseLineItem item : items) {
            CheckBox cb = new CheckBox(item.getCatalog().getName());
            TextField desc = null;
            if (item.getCatalog().isDescriptionRequired()) {
                desc = new TextField();
                desc.setPrefColumnCount(18);
                GridPane.setConstraints(desc, 1, row);
            }
            Label price = new Label(GeneratorTabs.DOLLAR_FORMATTER.format(item.getCatalog().getDefaultCost()));
            GridPane.setConstraints(cb, 0, row);
            GridPane.setConstraints(price, 2, row);
            itemsGrid.getChildren().addAll(cb, price);
            if (desc != null) itemsGrid.getChildren().add(desc);

            cb.selectedProperty().bindBidirectional(item.selectedProperty());
            if (desc != null) desc.textProperty().bindBidirectional(item.descriptionProperty());
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

    private void wireClear(List<CheckBox> checkBoxes) {
        Observable[] deps = checkBoxes.stream().map(CheckBox::selectedProperty).toArray(Observable[]::new);
        clearButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> checkBoxes.stream().noneMatch(CheckBox::isSelected),
                deps));
        clearButton.setOnAction(e -> checkBoxes.forEach(cb -> cb.setSelected(false)));
    }
}
