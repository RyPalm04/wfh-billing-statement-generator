package com.palmer.billingstatementgenerator.views;

import com.palmer.billingstatementgenerator.models.TabTwoModel;
import com.palmer.billingstatementgenerator.views.tabs.TabThree;
import com.palmer.billingstatementgenerator.views.tabs.TabTwo;

import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;

public class MainView {
	private TabPane view;
	private TabTwo tabTwo;
	private TabThree tabThree;

	public MainView() {
		createAndConfigurePane();
	}

	public Parent asParent() {
		return view;
	}

	private void createAndConfigurePane() {
		tabTwo = new TabTwo(new TabTwoModel());
		tabThree = new TabThree();
		view = new TabPane(tabTwo, tabThree);
		view.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		view.setSide(Side.LEFT);

		setActions();
	}

	private void setActions() {
		tabTwo.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabThree.getNextButton().setOnAction(e -> view.getSelectionModel().selectNext());
		tabThree.getPrevButton().setOnAction(e -> view.getSelectionModel().selectPrevious());
	}

}
