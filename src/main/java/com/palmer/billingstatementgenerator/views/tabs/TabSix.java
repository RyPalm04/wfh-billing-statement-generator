package com.palmer.billingstatementgenerator.views.tabs;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TabSix extends GeneratorTabs {

    public TabSix(String tabTitle) {
        super(tabTitle);
    }

    @Override
    protected void addForm() {
        Label totalLabel = new Label("CASH ADVANCE ITEMS");
        List<CheckBox> checkBoxes = Arrays.stream(CashAdvances.values())
                                          .map(v -> new CheckBox(v.getName()))
                                          .collect(Collectors.toList());

        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            GridPane.setConstraints(checkBox, 0, i);
            checkBox.setFont(new Font(14));
            addGridElements(checkBox);
            checkBox.setOnAction(e -> clearButton.setDisable(!checkBox.isSelected()));
        }

        clearButton.setOnAction(e -> {
            checkBoxes.forEach(checkBox -> checkBox.setSelected(false));
            clearButton.setDisable(true);
        });
    }
}

enum CashAdvances {
    GRAVE("Grave Opening"),
    WEEKEND_HOLIDAY("Weekend/Holiday Charge"),
    NEWSPAPER_A("Newspaper Notices"),
    NEWSPAPER_B("Newspaper Notices"),
    NEWSPAPER_C("Newspaper Notices"),
    NEWSPAPER_D("Newspaper Notices"),
    RADIO("Radio Notices"),
    MINISTER_A("Honorarium Minister"),
    MINISTER_B("Honorarium Minister"),
    ORGANIST("Honorarium Organist"),
    SINGER_A("Honorarium Singer"),
    SINGER_B("Honorarium Singer"),
    SINGER_C("Honorarium Singer"),
    HAIRDRESSER("Honorarium Hairdresser"),
    DEATH_CERTIFICATE("Certified Death Certificate"),
    OUT_OF_TOWN("Out of Town Mortuary Charges"),
    MARKER_DATE("Date for Cemetery Maker"),
    FLOWERS("Flowers"),
    OTHER_A("Other"),
    OTHER_B("Other");

    private final String name;

    CashAdvances(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
