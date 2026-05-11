package com.palmer.billingstatementgenerator.views;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class SplashView {
    private VBox container;
    private ProgressBar progressBar;
    private Label statusLabel;

    public SplashView() {
        createContent();
    }

    private void createContent() {
        container = new VBox(20);
        container.getStyleClass().add("splash-container");
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(500, 400);

        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/palmer/billingstatementgenerator/img/wfh splash logo.jpg")));
        ImageView imageView = new ImageView(logo);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        Label titleLabel = new Label("Wright Funeral Home");
        titleLabel.getStyleClass().add("splash-title");

        Label subtitleLabel = new Label("Billing Statement Generator");
        subtitleLabel.getStyleClass().add("splash-subtitle");

        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);

        statusLabel = new Label("Initializing...");
        statusLabel.setStyle("-fx-text-fill: #888888;");

        container.getChildren().addAll(imageView, titleLabel, subtitleLabel, progressBar, statusLabel);
    }

    public Parent asParent() {
        return container;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getStatusLabel() {
        return statusLabel;
    }
}
