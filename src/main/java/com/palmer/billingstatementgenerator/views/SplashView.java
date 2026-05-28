package com.palmer.billingstatementgenerator.views;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

/**
 * The splash screen view displayed while the application initializes.
 * Shows the Wright Funeral Home logo, application title, a progress bar,
 * a status message label, and a copyright notice.
 */
public class SplashView {
    private VBox container;
    private ProgressBar progressBar;
    private Label statusLabel;

    /**
     * Constructs the splash view and builds its content.
     */
    public SplashView() {
        createContent();
    }

    /**
     * Builds the splash screen layout including the logo, title, subtitle,
     * progress bar, status label, and copyright notice.
     */
    private void createContent() {
        container = new VBox(20);
        container.getStyleClass().add("splash-container");
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(500, 400);

        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/app-icon.png")));
        ImageView imageView = new ImageView(logo);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        Label titleLabel = new Label("Statement Manager");
        titleLabel.getStyleClass().add("splash-title");

        Label subtitleLabel = new Label("Billing Statement Generator");
        subtitleLabel.getStyleClass().add("splash-subtitle");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);

        statusLabel = new Label("Initializing...");
        statusLabel.setStyle("-fx-text-fill: #888888;");

        Label copyrightLabel = new Label("© " + java.time.Year.now().getValue() + " Ryan Palmer. All rights reserved.");
        copyrightLabel.getStyleClass().add("splash-copyright");

        container.getChildren().addAll(imageView, titleLabel, subtitleLabel, progressBar, statusLabel, copyrightLabel);
    }

    /**
     * Returns the root node of the splash view for embedding in a {@link javafx.scene.Scene}.
     *
     * @return the root {@link Parent} node
     */
    public Parent asParent() {
        return container;
    }

    /**
     * Returns the progress bar for binding to an initialization task's progress property.
     *
     * @return the {@link ProgressBar}
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Returns the status label for binding to an initialization task's message property.
     *
     * @return the status {@link Label}
     */
    public Label getStatusLabel() {
        return statusLabel;
    }
}