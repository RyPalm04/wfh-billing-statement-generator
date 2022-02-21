package com.palmer.billingstatementgenerator;

import com.palmer.billingstatementgenerator.views.MainView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		MainView view = new MainView();

		BorderPane root = new BorderPane(view.asParent());

		Scene scene = new Scene(root, 400, 500);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Wright Funeral Home Billing Statement");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
