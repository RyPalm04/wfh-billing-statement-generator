package com.palmer.billingstatementgenerator.views.tabs;

import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

public abstract class GeneratorTabs extends Tab {
    protected Button nextButton = new Button("Next");
    protected Button prevButton = new Button("Previous");
    protected GridPane root;

    public GeneratorTabs() {
        createAndConfigurePanel();
        addForm();
    }

    protected abstract void addForm();

    protected abstract void createAndConfigurePanel();

    public Button getNextButton() {
        return nextButton;
    }

    public Button getPrevButton() {
        return prevButton;
    }
}
