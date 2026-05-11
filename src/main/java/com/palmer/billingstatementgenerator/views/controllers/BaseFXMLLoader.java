package com.palmer.billingstatementgenerator.views.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

/**
 * Utility to load an FXML file and merge its GridPane contents into an existing GridPane.
 */
public class BaseFXMLLoader {

    public static void loadIntoGrid(GridPane target, String resourcePath) {
        try {
            FXMLLoader loader = new FXMLLoader(BaseFXMLLoader.class.getResource(resourcePath));
            GridPane loaded = loader.load();
            // merge column constraints and children into the existing target grid
            target.getColumnConstraints().addAll(loaded.getColumnConstraints());
            target.getChildren().addAll(loaded.getChildren());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
