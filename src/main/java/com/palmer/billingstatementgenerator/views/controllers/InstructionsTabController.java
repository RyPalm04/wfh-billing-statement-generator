package com.palmer.billingstatementgenerator.views.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Objects;

public class InstructionsTabController extends BaseController {

    private static final List<String[]> STEPS = List.of(
            new String[]{"Service Information",
                    "Enter the control number, name of the deceased, dates, and place of death."},
            new String[]{"Services, Facilities & Transportation",
                    "Select a service package if applicable, then choose individual services provided."},
            new String[]{"Merchandise",
                    "Select any merchandise items. Enter prices for items without a default cost."},
            new String[]{"Special Charges",
                    "Select any special charges such as mileage, cremation, or grave setup fees."},
            new String[]{"Cash Advance Items",
                    "Select any cash advance items such as flowers, death certificates, or honorariums."},
            new String[]{"Summary & Export",
                    "Review all selections and totals, then generate the completed billing statement PDF."}
    );

    public ScrollPane buildView(Runnable onGetStarted) {

        VBox root = new VBox(24);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(32));
        root.setMaxWidth(640);

        // Logo
        Image logo = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/com/palmer/billingstatementgenerator/img/wfh splash logo.jpg")));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(120);
        logoView.setPreserveRatio(true);

        // Welcome text
        Label welcome = new Label("Welcome to the Billing Statement Generator");
        welcome.getStyleClass().add("instructions-title");
        welcome.setWrapText(true);
        welcome.setTextAlignment(TextAlignment.CENTER);

        Label subtitle = new Label(
                "Follow the steps below to complete a billing statement for Wright Funeral Home. " +
                        "Use the tabs at the top to navigate between sections.");
        subtitle.getStyleClass().add("instructions-subtitle");
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setMaxWidth(520);

        // Steps
        VBox steps = new VBox(16);
        steps.setAlignment(Pos.CENTER_LEFT);
        steps.setMaxWidth(520);

        for (int i = 0; i < STEPS.size(); i++) {
            steps.getChildren().add(buildStep(i + 1, STEPS.get(i)[0], STEPS.get(i)[1]));
        }

        // Get Started button
        Button getStarted = new Button("Get Started");
        getStarted.getStyleClass().add("button-get-started");
        getStarted.setOnAction(e -> onGetStarted.run());

        root.getChildren().addAll(logoView, welcome, subtitle, steps, getStarted);

        // Wrap in a centering pane so it stays centered in wide windows
        VBox centered = new VBox(root);
        centered.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(root, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(centered);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("summary-scroll");

        return scrollPane;
    }

    private HBox buildStep(int number, String title, String description) {
        // Number circle
        Label numberLabel = new Label(String.valueOf(number));
        numberLabel.getStyleClass().add("step-number");
        numberLabel.setMinSize(36, 36);
        numberLabel.setMaxSize(36, 36);
        numberLabel.setAlignment(Pos.CENTER);

        // Text
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("step-title");

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("step-description");
        descLabel.setWrapText(true);

        VBox text = new VBox(4, titleLabel, descLabel);
        text.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(text, Priority.ALWAYS);

        HBox step = new HBox(16, numberLabel, text);
        step.setAlignment(Pos.CENTER_LEFT);
        step.setPadding(new Insets(12));
        step.getStyleClass().add("step-card");

        return step;
    }
}