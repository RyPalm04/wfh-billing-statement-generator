package com.palmer.billingstatementgenerator.views.tabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;

public class TabThree extends Tab {

	private GridPane root;
	private Button nextButton;
	private Button prevButton;

	public TabThree() {
		setText("SERVICES, FACILITIES, AND TRANSPORTATION");
		createAndConfigurePanel();
		addForm();
	}

	private void addForm() {
		nextButton = new Button("Next");
		prevButton = new Button("Previous");
		GridPane grid = new GridPane();

		final ComboBox<String> packages = new ComboBox<>();

		packages.getItems().setAll(Arrays.stream(Packages.values()).map(Packages::getName).collect(Collectors.toList()));
		packages.setValue("Package Selection...");
		packages.setStyle("-fx-font-size:14px");

		List<CheckBox> checkBoxes = Arrays.stream(Services.values()).map(value -> new CheckBox(value.getName()))
				.collect(Collectors.toList());

		grid.setAlignment(Pos.CENTER);
		grid.setHgap(7);
		grid.setVgap(10);

		GridPane.setConstraints(packages, 0, 0);

		for (int i = 1; i < checkBoxes.size() + 1; i++) {
			GridPane.setConstraints(checkBoxes.get(i - 1), 0, i);
			checkBoxes.get(i-1).setFont(new Font(14));
		}

		grid.getChildren().add(packages);
		grid.getChildren().addAll(checkBoxes);

		GridPane.setConstraints(grid, 1, 0);
		GridPane.setConstraints(prevButton, 0, 1);
		GridPane.setConstraints(nextButton, 2, 1);

		root.getChildren().addAll(grid, prevButton, nextButton);
	}

	private void createAndConfigurePanel() {
		root = new GridPane();

		root.setAlignment(Pos.CENTER);
		root.setHgap(7);
		root.setVgap(10);

		this.setContent(root);
	}

	public Button getNextButton() {
		return nextButton;
	}

	public Button getPrevButton() {
		return prevButton;
	}
}

enum Services {
	BASIC("Basic Services", 0.00),
	EMBALMING("Embalming", 0.00),
	OTHER_PREP("Other Preparation", 0.00),
	VISITATION("Visitation", 0.00),
	FUNERAL("Funeral Service", 0.00),
	MEMORIAL("Memorial Service", 0.00),
	GRAVESIDE("Graveside Service", 0.00),
	COACH("Funeral Coach", 0.00),
	PALLBEARER_CAR("Pallbearer Car", 0.00),
	SERVICE_CAR("Service Car", 0.00),
	TRANSFER("Transfer of Remains to Funeral Home", 0.00),
	OTHER_A("Other", 0.00),
	OTHER_B("Other", 0.00);

	private final String name;
	private final Double cost;

	Services(String name, Double cost) {
		this.name = name;
		this.cost = cost;
	}

	public String getName() {
		return name;
	}

	public Double getCost() {
		return cost;
	}


}

enum Packages {
	T1("Traditional One", 0.00),
	T2("Traditional Two", 0.00),
	C1("Cremation One", 0.00),
	C2("Cremation Two", 0.00),
	C3("Cremation Three", 0.00),
	C4("Cremation Four", 0.00),
	C5("Cremation Five", 0.00);

	private final String name;
	private final Double cost;

	Packages(String name, Double cost) {
		this.name = name;
		this.cost = cost;
	}

	public String getName() {
		return name;
	}

	public Double getCost() {
		return cost;
	}
}
