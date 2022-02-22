package com.palmer.billingstatementgenerator.views.tabs;

import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import java.text.NumberFormat;

public abstract class GeneratorTabs extends Tab {
    protected static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();
    private final boolean showPrev;
    private final boolean showNext;
    private final boolean showClear;
    protected Button nextButton = new Button("Next");
    protected Button prevButton = new Button("Previous");
    protected Button clearButton = new Button("Clear Selections");
    protected GridPane grid = new GridPane();
    protected GridPane root = new GridPane();;

    public GeneratorTabs(String tabTitle) {
        this(tabTitle, true, true, true);
    }
    public GeneratorTabs(String tabTitle, boolean showPrev, boolean showNext, boolean showClear) {
        super(tabTitle);
        this.showPrev = showPrev;
        this.showNext = showNext;
        this.showClear = showClear;
        createAndConfigurePanel();
        configureGrid();
        addForm();
        clearButton.setDisable(true);
    }

    protected void createAndConfigurePanel() {
        root.setAlignment(Pos.CENTER);
        root.setHgap(7);
        root.setVgap(10);

        GridPane.setConstraints(grid, 1, 0);
        GridPane.setConstraints(prevButton, 0, 1);
        GridPane.setConstraints(nextButton, 2, 1);
        GridPane.setConstraints(clearButton, 1, 1);
        GridPane.setHalignment(clearButton, HPos.CENTER);

        root.getChildren().add(grid);

        if (showPrev) {
            root.getChildren().add(prevButton);
        }

        if (showNext) {
            root.getChildren().add(nextButton);
        }

        if (showClear) {
            root.getChildren().add(clearButton);
        }

        this.setContent(root);
    }

    protected void addGridElements(Node... nodes) {
        grid.getChildren().addAll(nodes);
    }

    public Button getNextButton() {
        return nextButton;
    }

    public Button getPrevButton() {
        return prevButton;
    }

    private void configureGrid() {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(7);
        grid.setVgap(10);
    }

    protected abstract void addForm();
}
