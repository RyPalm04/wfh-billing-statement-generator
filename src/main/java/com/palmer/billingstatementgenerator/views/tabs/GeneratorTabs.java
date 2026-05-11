package com.palmer.billingstatementgenerator.views.tabs;

import com.palmer.billingstatementgenerator.views.controllers.BaseController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class GeneratorTabs extends Tab {
    protected Button nextButton = new Button("Next");
    protected Button prevButton = new Button("Previous");
    protected Button clearButton = new Button("Clear Selections");
    protected GridPane grid = new GridPane();
    protected GridPane root = new GridPane();

    private GeneratorTabs(String tabTitle, boolean showPrev, boolean showNext, boolean showClear, String fxmlPath) {
        super(tabTitle);
        createAndConfigurePanel(showPrev, showNext, showClear);
        configureGrid();
        loadFxml(fxmlPath, showNext);
    }

    public static GeneratorTabs create(String title, String fxmlPath, boolean showPrev, boolean showNext, boolean showClear) {
        return new GeneratorTabs(title, showPrev, showNext, showClear, fxmlPath);
    }

    private void loadFxml(String fxmlPath, boolean showNext) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            GridPane loaded = loader.load();
            grid.getColumnConstraints().addAll(loaded.getColumnConstraints());
            grid.getChildren().addAll(loaded.getChildren());

            Object ctrl = loader.getController();
            if (ctrl instanceof BaseController) {
                BaseController base = (BaseController) ctrl;
                base.setClearButton(clearButton);
                if (showNext) {
                    base.setNextButton(nextButton);
                }
                this.selectedProperty().addListener((obs, wasSel, isSel) -> {
                    if (isSel) base.onShow();
                    else base.onHide();
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAndConfigurePanel(boolean showPrev, boolean showNext, boolean showClear) {
        root.getStyleClass().add("form-grid");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(15));
        root.setHgap(7);
        root.setVgap(10);

        GridPane.setConstraints(grid, 1, 0);
        GridPane.setConstraints(prevButton, 0, 1);
        GridPane.setConstraints(nextButton, 2, 1);
        GridPane.setConstraints(clearButton, 1, 1);
        GridPane.setHalignment(clearButton, HPos.CENTER);

        root.getChildren().add(grid);
        if (showPrev) root.getChildren().add(prevButton);
        if (showNext) root.getChildren().add(nextButton);
        if (showClear) root.getChildren().add(clearButton);

        this.setContent(root);
    }

    private void configureGrid() {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(7);
        grid.setVgap(10);
    }

    public Button getNextButton() { return nextButton; }
    public Button getPrevButton() { return prevButton; }
}