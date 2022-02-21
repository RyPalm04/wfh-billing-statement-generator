package com.palmer.billingstatementgenerator.views.tabs;

import java.util.Arrays;

import com.palmer.billingstatementgenerator.controllers.TabTwoController;
import com.palmer.billingstatementgenerator.models.TabTwoModel;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;

public class TabTwo extends Tab {

	private TabTwoController controller;
	private final TabTwoModel model;
	private GridPane root;
	private Button nextButton;

	public TabTwo(/*TabOneController controller,*/ TabTwoModel model) {
		//		this.controller = controller;
		this.setText("SERVICE INFORMATION");
		this.model = model;

		createAndConfigurePane();
		addForm();
	}

	private void addForm() {
		nextButton = new Button("Next");

		GridPane grid = new GridPane();

		TextField cNumber = new TextField();
		TextField sFor = new TextField();
		DatePicker dDate = new DatePicker();
		TextField dPlace = new TextField();
		DatePicker sDate = new DatePicker();

		configTextFieldForInts(cNumber);

		Label cNumberLabel = new Label("Control Number");
		Label sForLabel = new Label("Services For");
		Label dDateLabel = new Label("Date of Death");
		Label dPlaceLabel = new Label("Place of Death");
		Label sDateLabel = new Label("Service Date");

		formatLabels(cNumberLabel, sForLabel, dDateLabel, dPlaceLabel, sDateLabel);

		grid.setAlignment(Pos.CENTER);
		grid.setHgap(7);
		grid.setVgap(10);

		ColumnConstraints leftCol = new ColumnConstraints();
		leftCol.setHgrow(Priority.NEVER);
		leftCol.setHalignment(HPos.RIGHT);

		ColumnConstraints rightCol = new ColumnConstraints();
		rightCol.setHgrow(Priority.SOMETIMES);

		grid.getColumnConstraints().addAll(leftCol, rightCol);

		GridPane.setConstraints(cNumberLabel, 0, 0);
		GridPane.setConstraints(cNumber, 1, 0);
		GridPane.setConstraints(sForLabel, 0, 1);
		GridPane.setConstraints(sFor, 1, 1);
		GridPane.setConstraints(dDateLabel, 0, 2);
		GridPane.setConstraints(dDate, 1, 2);
		GridPane.setConstraints(dPlaceLabel, 0, 3);
		GridPane.setConstraints(dPlace, 1, 3);
		GridPane.setConstraints(sDateLabel, 0, 4);
		GridPane.setConstraints(sDate, 1, 4);

		grid.getChildren().addAll(cNumberLabel, cNumber, sForLabel, sFor, dDateLabel, dDate, dPlaceLabel, dPlace, sDateLabel, sDate);

		GridPane.setConstraints(grid, 0, 0);
		GridPane.setConstraints(nextButton, 1, 1);

		root.getChildren().addAll(grid, nextButton);
	}

	private void formatLabels(Label... labels) {
		Arrays.stream(labels).forEach(label -> label.setFont(new Font(14)));
	}

	private void createAndConfigurePane() {
		root = new GridPane();

		root.setAlignment(Pos.CENTER);
		root.setHgap(7);
		root.setVgap(10);

		this.setContent(root);
	}

	private void configTextFieldForInts(TextField... fields) {
		Arrays.stream(fields).forEach(field -> field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
			if (c.getControlNewText().matches("-?\\d*")) {
				return c;
			}
			return null;
		})));
	}

	public Button getNextButton() {
		return nextButton;
	}
}
