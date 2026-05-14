package com.palmer.billingstatementgenerator.models.statement;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lightweight summary of a saved statement, used to populate the Open Existing dialog.
 * Contains only the fields needed for display — the full statement is loaded on demand.
 */
public class SavedStatementSummary {

    private final int id;
    private final int controlNumber;
    private final String servicesForName;
    private final LocalDate serviceDate;
    private final LocalDateTime savedAt;

    /**
     * @param id
     *         the database ID of the saved statement
     * @param controlNumber
     *         the statement control number
     * @param servicesForName
     *         the name of the person services are for
     * @param serviceDate
     *         the date of service, or null if not set
     * @param savedAt
     *         the timestamp when the statement was last saved
     */
    public SavedStatementSummary(int id, int controlNumber, String servicesForName,
                                 LocalDate serviceDate, LocalDateTime savedAt) {
        this.id = id;
        this.controlNumber = controlNumber;
        this.servicesForName = servicesForName;
        this.serviceDate = serviceDate;
        this.savedAt = savedAt;
    }

    /**
     * @return the database ID
     */
    public int getId() {
        return id;
    }

    /**
     * @return the control number
     */
    public int getControlNumber() {
        return controlNumber;
    }

    /**
     * @return the services-for name
     */
    public String getServicesForName() {
        return servicesForName;
    }

    /**
     * @return the service date, or null if not set
     */
    public LocalDate getServiceDate() {
        return serviceDate;
    }

    /**
     * @return the timestamp when the statement was last saved
     */
    public LocalDateTime getSavedAt() {
        return savedAt;
    }
}