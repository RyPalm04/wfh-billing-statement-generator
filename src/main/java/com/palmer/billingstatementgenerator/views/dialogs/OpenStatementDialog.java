package com.palmer.billingstatementgenerator.views.dialogs;

import com.palmer.billingstatementgenerator.models.statement.SavedStatementSummary;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modal dialog listing all saved statements and allowing the user to open one.
 * Returns the selected statement ID from {@link #open()}, or {@code null} if cancelled.
 */
public class OpenStatementDialog extends AppDialog<Integer> {

    private final List<SavedStatementSummary> summaries;

    /**
     * Creates the open-statement dialog.
     *
     * @param summaries
     *         the list of saved statements to display
     */
    public OpenStatementDialog(List<SavedStatementSummary> summaries) {
        this.summaries = summaries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected VBox buildContent() {
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter tsFmt = DateTimeFormatter.ofPattern("MM/dd/yyyy h:mm a");

        TableView<SavedStatementSummary> table = new TableView<>(FXCollections.observableArrayList(summaries));
        table.setPrefHeight(220);
        table.setPrefWidth(520);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No saved statements"));

        TableColumn<SavedStatementSummary, Integer> numCol = new TableColumn<>("#");
        numCol.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().controlNumber()).asObject());
        numCol.setPrefWidth(50);

        TableColumn<SavedStatementSummary, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().servicesForName()));

        TableColumn<SavedStatementSummary, LocalDate> dateCol = new TableColumn<>("Service Date");
        dateCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().serviceDate()));
        dateCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "—" : dateFmt.format(item));
            }
        });

        TableColumn<SavedStatementSummary, LocalDateTime> savedCol = new TableColumn<>("Saved");
        savedCol.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().savedAt()));
        savedCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "—" : tsFmt.format(item));
            }
        });

        table.getColumns().addAll(numCol, nameCol, dateCol, savedCol);

        Button open = new Button("Open");
        open.setId("openButton");
        open.setDisable(true);
        table.getSelectionModel().selectedItemProperty().addListener(
                (obs, o, n) -> open.setDisable(n == null));

        Runnable doOpen = () -> {
            SavedStatementSummary selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                result = selected.id();
                close();
            }
        };

        open.setOnAction(e -> doOpen.run());
        table.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                doOpen.run();
            }
        });

        Button cancel = new Button("Cancel");
        cancel.setId("cancelButton");
        cancel.getStyleClass().add("button-clear");
        cancel.setOnAction(e -> close());

        HBox buttons = new HBox(12, open, cancel);
        buttons.setAlignment(Pos.CENTER);

        return contentBox("Open Statement", table, buttons);
    }
}