package com.palmer.billingstatementgenerator.controllers;

import org.apache.commons.lang3.StringUtils;

import com.palmer.billingstatementgenerator.models.TabTwoModel;

public class MainController {

	private final TabTwoModel model;

	public MainController(TabTwoModel model) {
		this.model = model;
	}

	public void updateX(String x) {
	}

	public void updateY(String y) {
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
