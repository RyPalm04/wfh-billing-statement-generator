package com.palmer.billingstatementgenerator.views.tabs;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.palmer.billingstatementgenerator.views.tabs.SpecialCharges.GRAVE_SERVICE;
import static com.palmer.billingstatementgenerator.views.tabs.SpecialCharges.MILEAGE;

public class TabFive extends GeneratorTabs {

    public TabFive(String tabTitle) {
        super(tabTitle);
    }

    @Override
    protected void addForm() {
        Label totalLabel = new Label("TOTAL COST FOR SPECIAL CHARGES");
        List<CheckBox> checkBoxes = Arrays.stream(SpecialCharges.values())
                                          .map(v -> new CheckBox(v.getName()))
                                          .collect(Collectors.toList());
        List<Label> prices = Arrays.stream(SpecialCharges.values())
                                   .map(v -> {
                                       if (!Arrays.asList(GRAVE_SERVICE.getName(), MILEAGE.getName())
                                                  .contains(v.getName())) {
                                           return new Label(String.format("%14s", DOLLAR_FORMATTER.format(v.getCost())));
                                       } else {
                                           return new Label("");
                                       }
                                   })
                                   .collect(Collectors.toList());

        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            Label price = prices.get(i);
            GridPane.setConstraints(checkBox, 0, i);
            GridPane.setConstraints(price, 1, i);
            checkBox.setFont(new Font(14));
            price.setFont(new Font(14));
            addGridElements(checkBox, price);
            checkBox.setOnAction(e -> clearButton.setDisable(!checkBox.isSelected()));
        }

        clearButton.setOnAction(e -> {
            checkBoxes.forEach(checkBox -> checkBox.setSelected(false));
            clearButton.setDisable(true);
        });
    }
}

enum SpecialCharges {
    GRAVE_SERVICE("Grave Service Setup/Delivery", 0.00),
    CREMATION("Direct Cremation", 0.00),
    MILEAGE("Mileage", 0.00),
    FORWARD_REMAINS("Forward of Remains to (funeral home)", 0.00),
    RECEIVE_REMAINS("Receiving of Remains from (funeral home)", 0.00),
    VAULT("Vault Company Weekend/Holiday Charge", 0.00),
    BURIAL("Immediate Burial", 0.00),
    OTHER("Other", 0.00);

    private String name;
    private Double cost;

    SpecialCharges(String name, Double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public Double getCost() {
        return cost;
    }
}
