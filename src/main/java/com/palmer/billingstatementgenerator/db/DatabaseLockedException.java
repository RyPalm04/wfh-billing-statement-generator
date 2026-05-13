package com.palmer.billingstatementgenerator.db;

/**
 * Thrown when the H2 database file is locked by another process,
 * indicating a second instance of the application is already running.
 */
public class DatabaseLockedException extends RuntimeException {
    public DatabaseLockedException() {
        super("Database is locked by another process");
    }
}