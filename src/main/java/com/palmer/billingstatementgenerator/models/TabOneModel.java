package com.palmer.billingstatementgenerator.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TabOneModel {
	private final IntegerProperty controlNumber = new SimpleIntegerProperty(null, "Control Number");
	private final StringProperty servicesForName = new SimpleStringProperty(null, "Services For");
	private final StringProperty placeOfDeath = new SimpleStringProperty(null, "Place of Death");
	private final StringProperty dateOfDeath = new SimpleStringProperty(null, "Date of Death");
	private final StringProperty serviceDate = new SimpleStringProperty(null, "Service Date");

	public TabOneModel() {
	}

	public final Integer getControlNumber() {
		return this.controlNumber.get();
	}

	public final IntegerProperty controlNumberProperty() {
		return this.controlNumber;
	}

	public final void setControlNumber(final Integer controlNumber) {
		this.controlNumber.set(controlNumber);
	}

	public String getServicesForName() {
		return servicesForName.get();
	}

	public StringProperty servicesForNameProperty() {
		return this.servicesForName;
	}

	public void setServicesForName(String servicesForName) {
		this.servicesForName.set(servicesForName);
	}

	public String getPlaceOfDeath() {
		return placeOfDeath.get();
	}

	public StringProperty placeOfDeathProperty() {
		return placeOfDeath;
	}

	public void setPlaceOfDeath(String placeOfDeath) {
		this.placeOfDeath.set(placeOfDeath);
	}

	public String getDateOfDeath() {
		return dateOfDeath.get();
	}

	public StringProperty dateOfDeathProperty() {
		return dateOfDeath;
	}

	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath.set(dateOfDeath);
	}

	public String getServiceDate() {
		return serviceDate.get();
	}

	public StringProperty serviceDateProperty() {
		return serviceDate;
	}

	public void setServiceDate(String serviceDate) {
		this.serviceDate.set(serviceDate);
	}

	@Override
	public String toString() {
		return "TabOneModel{" +
				"controlNumber=" + controlNumber +
				", servicesForName=" + servicesForName +
				", placeOfDeath=" + placeOfDeath +
				", dateOfDeath=" + dateOfDeath +
				", serviceDate=" + serviceDate +
				'}';
	}
}
