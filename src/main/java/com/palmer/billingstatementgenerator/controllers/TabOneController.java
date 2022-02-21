package com.palmer.billingstatementgenerator.controllers;

import java.time.LocalDate;

import com.palmer.billingstatementgenerator.models.TabOneModel;

public class TabOneController {
	private final TabOneModel model;

	public TabOneController(TabOneModel model) {
		this.model = model;
	}

	private void setDateOfDeath(LocalDate dod) {
		model.setDateOfDeath(dod.toString());
	}

	private void setServiceDate(LocalDate sDate) {
		model.setServiceDate(sDate.toString());
	}

	public void updateControlNumber(Integer cNumber) {
		model.setControlNumber(cNumber);
	}

	public void updateServiceName(String name) {
		model.setServicesForName(name);
	}

	public void updatePlaceOfDeath(String placeOfDeath) {
		model.setPlaceOfDeath(placeOfDeath);
	}
}
