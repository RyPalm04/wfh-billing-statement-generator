package com.palmer.billingstatementgenerator.views.tabs;

import com.palmer.billingstatementgenerator.views.controllers.BaseController;
import com.palmer.billingstatementgenerator.views.controllers.GridTabController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.text.NumberFormat;

public class GeneratorTabs extends Tab {
    protected static final NumberFormat DOLLAR_FORMATTER = NumberFormat.getCurrencyInstance();

    private BaseController controller;
    protected GridPane grid = new GridPane();

    private GeneratorTabs(String tabTitle) {
        super(tabTitle);
        configureGrid();
    }

    public static GeneratorTabs fromFxml(String title, String fxmlPath) {
        GeneratorTabs tab = new GeneratorTabs(title);
        tab.loadFxml(fxmlPath);
        return tab;
    }

    public static GeneratorTabs fromController(String title, GridTabController<?> controller) {
        GeneratorTabs tab = new GeneratorTabs(title);
        tab.loadController(controller);
        return tab;
    }

    public static GeneratorTabs fromController(String title, BaseController controller, javafx.scene.Node view) {
        GeneratorTabs tab = new GeneratorTabs(title);
        tab.controller = controller;
        GridPane.setConstraints(view, 0, 0);
        tab.grid.getChildren().add(view);
        tab.wireLifecycle();
        return tab;
    }

    private void loadFxml(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            GridPane loaded = loader.load();
            grid.getColumnConstraints().addAll(loaded.getColumnConstraints());
            grid.getChildren().addAll(loaded.getChildren());
            Object ctrl = loader.getController();
            if (ctrl instanceof BaseController) {
                controller = (BaseController) ctrl;
                wireLifecycle();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadController(GridTabController<?> ctrl) {
        controller = ctrl;
        GridPane built = ctrl.buildView();
        GridPane.setConstraints(built, 0, 0);
        grid.getChildren().add(built);
        wireLifecycle();
    }

    private void wireLifecycle() {
        this.selectedProperty().addListener((obs, wasSel, isSel) -> {
            if (isSel) controller.onShow();
            else controller.onHide();
        });
    }

    private void configureGrid() {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(12);
        grid.setVgap(16);
        grid.setPadding(new Insets(24));
        this.setContent(grid);
    }

    public BaseController getController() {
        return controller;
    }
}