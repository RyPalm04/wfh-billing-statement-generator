package com.palmer.billingstatementgenerator.views.tabs;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.palmer.billingstatementgenerator.views.tabs.Merchandise.*;

public class TabFour extends GeneratorTabs {

    public TabFour(String tabTitle) {
        super(tabTitle);
    }

    @Override
    protected void addForm() {
        Label totalLabel = new Label("TOTAL COST FOR SELECTED MERCHANDISE");
        List<CheckBox> checkBoxes = Arrays.stream(Merchandise.values())
                                          .map(v -> new CheckBox(v.getName()))
                                          .collect(Collectors.toList());
        List<Label> prices = Arrays.stream(Merchandise.values())
                                    .map(v -> {
                                        if (!Arrays.asList(CASKET.getName(), CREMATION.getName(), VAULT.getName(), OTHER_A.getName(), OTHER_B.getName())
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

enum Merchandise {
    CASKET("Casket or Alternative Container", 0.00),
    CREMATION("Cremation Urn", 0.00),
    VAULT("Outer Burial Container", 0.00),
    ACCESSORIES("Service Accessory Package", 0.00),
    REGISTRY("Register Book", 0.00),
    CARDS("Thank You Cards", 0.00),
    FOLDERS("Memorial Folders", 0.00),
    VIDEO("Memorial Video", 0.00),
    JEWELRY("Jewelry", 0.00),
    SUPERVISION("Burial Supervision", 0.00),
    MARKER("Temporary Grave Marker", 0.00),
    OTHER_A("Other Merchandise", 0.00),
    OTHER_B("Other Merchandise", 0.00);

    private String name;
    private Double cost;

    Merchandise(String name, Double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }
}