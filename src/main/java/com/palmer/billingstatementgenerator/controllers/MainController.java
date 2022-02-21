package com.palmer.billingstatementgenerator.controllers;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.palmer.billingstatementgenerator.models.MainWindowModel;

public class MainController {

	private final MainWindowModel model;

	public MainController(MainWindowModel model) {
		this.model = model;
	}

	public void updateX(String x) {
		model.setX(convertStringToInt(x));
	}

	public void updateY(String y) {
		model.setY(convertStringToInt(y));
	}

	private int convertStringToInt(String s) {
		if (StringUtils.isEmpty(s)) {
			return 0;
		}

		if (StringUtils.equals("-", s)) {
			return 0;
		}

		return Integer.parseInt(s);
	}
}
