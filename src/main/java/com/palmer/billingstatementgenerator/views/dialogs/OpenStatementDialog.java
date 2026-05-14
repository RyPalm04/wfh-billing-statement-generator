package com.palmer.billingstatementgenerator.views.dialogs;

import com.palmer.billingstatementgenerator.models.statement.SavedStatementSummary;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Modal dialog listing all saved statements and allowing the user to open one.
 * Whether a statement was opened is available via {@link #wasOpened()} after
 * {@link #open(javafx.stage.Window, com.palmer.billingstatementgenerator.logging.WorkflowEventTracker)}
 * returns.
 */
public class OpenStatementDialog extends AppDialog {

    private final List<SavedStatementSummary> summaries;
    private final Consumer<Integer> onLoad;
    private boolean opened = false;

    /**
     * Creates the open-statement dialog.
     *
     * @param summaries
     *         the list of saved statements to display
     * @param onLoad
     *         called with the selected statement ID after the dialog closes;
     *         runs on the JavaFX Application Thread via {@link Platform#runLater}
     */
    public OpenStatementDialog(List<SavedStatementSummary> summaries, Consumer<Integer> onLoad) {
        this.summaries = summaries;
        this.onLoad = onLoad;
    }

    /**
     * Returns {@code true} if the user opened a statement.
     * Valid after {@code open()} returns.
     *
     * @return whether a statement was opened
     */
    public boolean wasOpened() {
        return opened;
    }

    @Override
    protected VBox buildContent() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter tsFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

        ListView<SavedStatementSummary> list = new ListView<>(FXCollections.observableArrayList(summaries));
        list.setPrefHeight(220);
        list.setPrefWidth(520);
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(SavedStatementSummary item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String date = item.getServiceDate() != null ? dateFmt.format(item.getServiceDate()) : "—";
                    String saved = item.getSavedAt() != null ? tsFmt.format(item.getSavedAt()) : "—";
                    setText(String.format("#%d  %-30s  Service: %-12s  Saved: %s",
                            item.getControlNumber(), item.getServicesForName(), date, saved));
                }
            }
        });

        Button open = new Button("Open");
        open.setId("openButton");
        open.setDisable(true);
        list.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> open.setDisable(n == null));

        open.setOnAction(e -> {
            SavedStatementSummary selected = list.getSelectionModel().getSelectedItem();
            if (selected != null) {
                opened = true;
                int id = selected.getId();
                close();
                Platform.runLater(() -> onLoad.accept(id));
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> close());

        HBox buttons = new HBox(12, open, cancel);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Open Statement", list, buttons);
    }
}