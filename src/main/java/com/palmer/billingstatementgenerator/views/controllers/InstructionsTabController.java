package com.palmer.billingstatementgenerator.views.controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Objects;
import java.util.function.IntConsumer;

/**
 * Controller for the Instructions tab, displayed as the first tab in the application.
 * Presents a welcome message, the Statement Manager logo, and a step-by-step
 * overview of the billing statement workflow. Provides a Get Started button
 * that navigates the user to the Service Information tab.
 */
public class InstructionsTabController extends BaseController {

    /**
     * The ordered list of workflow steps displayed on the instructions screen.
     * Each entry is a two-element array: [step title, step description].
     */
    private static final List<String[]> STEPS = List.of(
            new String[]{"Service Information",
                    "Enter the name of the deceased, dates, and place of death. The control number is assigned automatically."},
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

    /**
     * Builds the instructions tab view and returns it wrapped in a {@link ScrollPane}.
     *
     * @param onNavigate
     *         an {@link IntConsumer} invoked with a tab index when the user clicks
     *         a step card or the Get Started button
     *
     * @return a {@link ScrollPane} containing the full instructions layout
     */
    public ScrollPane buildView(IntConsumer onNavigate) {
        VBox root = new VBox(24);
        root.setAlignment(Pos.TOP_CENTER);
        root.setPadding(new Insets(32));
        root.setMaxWidth(640);

        Image logo = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/img/app-icon.svg")));
        ImageView logoView = new ImageView(logo);
        logoView.setFitWidth(120);
        logoView.setPreserveRatio(true);

        Label welcome = new Label("Welcome to the Billing Statement Generator");
        welcome.getStyleClass().add("instructions-title");
        welcome.setWrapText(true);
        welcome.setTextAlignment(TextAlignment.CENTER);

        Label subtitle = new Label(
                "Follow the steps below to complete a billing statement. " +
                        "Use the tabs at the top to navigate between sections.");
        subtitle.getStyleClass().add("instructions-subtitle");
        subtitle.setWrapText(true);
        subtitle.setTextAlignment(TextAlignment.CENTER);
        subtitle.setMaxWidth(520);

        VBox steps = new VBox(16);
        steps.setAlignment(Pos.CENTER_LEFT);
        steps.setMaxWidth(520);

        for (int i = 0; i < STEPS.size(); i++) {
            int tabIndex = i + 1;
            steps.getChildren().add(buildStep(i + 1, STEPS.get(i)[0], STEPS.get(i)[1],
                    () -> onNavigate.accept(tabIndex)));
        }

        Button getStarted = new Button("Get Started");
        getStarted.setId("getStartedButton");
        getStarted.getStyleClass().add("button-get-started");
        getStarted.setOnAction(e -> onNavigate.accept(1));

        root.getChildren().addAll(logoView, welcome, subtitle, steps, getStarted);

        VBox centered = new VBox(root);
        centered.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(root, Priority.ALWAYS);

        ScrollPane scrollPane = new ScrollPane(centered);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("summary-scroll");

        return scrollPane;
    }

    /**
     * Builds a single step card containing a numbered circle, step title, and description.
     *
     * @param number
     *         the step number displayed in the circle
     * @param title
     *         the step title
     * @param description
     *         a brief description of what the user does in this step
     * @param onClick
     *         invoked when the user clicks the card
     *
     * @return an {@link HBox} representing the step card
     */
    private HBox buildStep(int number, String title, String description, Runnable onClick) {
        Label numberLabel = new Label(String.valueOf(number));
        numberLabel.getStyleClass().add("step-number");
        numberLabel.setMinSize(36, 36);
        numberLabel.setMaxSize(36, 36);
        numberLabel.setAlignment(Pos.CENTER);

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
        step.setCursor(Cursor.HAND);
        step.setOnMouseClicked(e -> onClick.run());

        return step;
    }
}