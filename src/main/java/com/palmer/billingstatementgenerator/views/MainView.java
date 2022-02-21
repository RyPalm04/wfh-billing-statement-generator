package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.controllers.MainController;
import com.palmer.billingstatementgenerator.models.MainWindowModel;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class MainView {
	private TabPane view;
	private GridPane secondView;
	private Button button;
	private Label label;
	private TextField xField;
	private TextField yField;
	private Label sumLabel;

	private MainController controller;
	private MainWindowModel model;

	public MainView(MainController controller, MainWindowModel model) {
		this.controller = controller;
		this.model = model;

		createAndConfigurePane();
		createAndLayoutControls();
		updateControllerFromListeners();
		observeModelAndUpdateControls();
	}

	public Parent asParent() {
		return view;
	}

	private void observeModelAndUpdateControls() {
		model.xProperty().addListener((obs, oldX, newX) -> updateIfNeeded(newX, xField));
		model.yProperty().addListener((obs, oldY, newY) -> updateIfNeeded(newY, yField));
		sumLabel.textProperty().bind(model.sumProperty().asString());
	}

	private void updateIfNeeded(Number value, TextField field) {
		String s = value.toString();

		if(!field.getText().equals(s)) {
			field.setText(s);
		}
	}

	private void updateControllerFromListeners() {
		xField.textProperty().addListener((obs, oldText, newText) -> controller.updateX(newText));
		yField.textProperty().addListener((obs, oldText, newText) -> controller.updateY(newText));
	}

	private void createAndLayoutControls() {
		xField = new TextField();
		configTextFieldForInts(xField);

		yField = new TextField();
		configTextFieldForInts(yField);

		sumLabel = new Label();

		secondView.addRow(0, new Label("X:"), xField);
		secondView.addRow(1, new Label("Y:"), yField);
		secondView.addRow(2, new Label("Sum:"), sumLabel);
	}

	private void createAndConfigurePane() {
		secondView = new GridPane();
		view = new TabPane(new Tab("Tab 1", secondView), new Tab("tab2"));
		view.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		ColumnConstraints leftCol = new ColumnConstraints();
		leftCol.setHalignment(HPos.RIGHT);
		leftCol.setHgrow(Priority.NEVER);

		ColumnConstraints rightCol = new ColumnConstraints();
		rightCol.setHgrow(Priority.SOMETIMES);

		secondView.getColumnConstraints().addAll(leftCol, rightCol);

		secondView.setAlignment(Pos.CENTER);
		secondView.setHgap(5);
		secondView.setVgap(10);
	}

	private void configTextFieldForInts(TextField field) {
		field.setTextFormatter(new TextFormatter<Integer>((Change c) -> {
			if (c.getControlNewText().matches("-?\\d*")) {
				return c;
			}
			return null;
		}));
	}
}
