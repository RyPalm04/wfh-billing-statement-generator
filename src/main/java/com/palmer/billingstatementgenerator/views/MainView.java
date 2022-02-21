package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.models.TabOneModel;
import com.palmer.billingstatementgenerator.views.tabs.TabTwo;

import javafx.scene.Parent;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainView {
	private TabPane view;
	private TabTwo tabTwo;

	public MainView() {
		createAndConfigurePane();
	}

	public Parent asParent() {
		return view;
	}

	private void createAndConfigurePane() {
		tabTwo = new TabTwo(new TabOneModel());
		view = new TabPane(tabTwo, new Tab("tab2"));
		view.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		setActions();
	}

	private void setActions() {
		tabTwo.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
	}

}
