package com.palmer.billingstatementgenerator.models.statement;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lightweight summary of a saved statement, used to populate the Open Existing dialog.
 * Contains only the fields needed for display — the full statement is loaded on demand.
 */
public record SavedStatementSummary(int id, int controlNumber, String servicesForName, LocalDate serviceDate,
                                    LocalDateTime savedAt) {

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
    public SavedStatementSummary {
    }

    /**
     * @return the database ID
     */
    @Override
    public int id() {
        return id;
    }

    /**
     * @return the control number
     */
    @Override
    public int controlNumber() {
        return controlNumber;
    }

    /**
     * @return the services-for name
     */
    @Override
    public String servicesForName() {
        return servicesForName;
    }

    /**
     * @return the service date, or null if not set
     */
    @Override
    public LocalDate serviceDate() {
        return serviceDate;
    }

    /**
     * @return the timestamp when the statement was last saved
     */
    @Override
    public LocalDateTime savedAt() {
        return savedAt;
    }
}