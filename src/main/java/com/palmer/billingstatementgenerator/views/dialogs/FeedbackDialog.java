package com.palmer.billingstatementgenerator.views.dialogs;

import com.palmer.billingstatementgenerator.client.FeedbackClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FeedbackDialog extends AppDialog<Void> {

    private final String currentTab;

    public FeedbackDialog(String currentTab) {
        this.currentTab = currentTab;
    }

    @Override
    protected VBox buildContent() {
        ComboBox<String> typeBox = new ComboBox<>(FXCollections.observableArrayList("Bug", "Feature Request", "Other"));
        typeBox.getSelectionModel().selectFirst();
        typeBox.setMaxWidth(Double.MAX_VALUE);

        TextArea descArea = new TextArea();
        descArea.setPromptText("Describe the issue or idea...");
        descArea.setWrapText(true);
        descArea.setPrefRowCount(5);
        descArea.setPrefWidth(360);

        Label errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setOnAction(e -> close());

        Button submitBtn = new Button("Submit");
        submitBtn.getStyleClass().add("button-save");
        submitBtn.setOnAction(e -> {
            if (descArea.getText().trim().isEmpty()) {
                errorLabel.setText("Please enter a description.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }
            submitBtn.setDisable(true);
            submitBtn.setText("Submitting...");
            new Thread(() -> {
                try {
                    new FeedbackClient().submitFeedback(
                            typeBox.getValue(),
                            descArea.getText().trim(),
                            currentTab
                    );
                    Platform.runLater(() -> {
                        new MessageDialog("Thank You", "Your feedback has been submitted.").open();
                        close();
                    });
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        errorLabel.setText("Failed to submit. Please try again.");
                        errorLabel.setVisible(true);
                        errorLabel.setManaged(true);
                        submitBtn.setDisable(false);
                        submitBtn.setText("Submit");
                    });
                }
            }).start();
        });

        HBox actions = new HBox(8, cancelBtn, submitBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);

        VBox fields = new VBox(8, typeBox, descArea, errorLabel, actions);

        return contentBox("Submit Feedback", fields);
    }
}