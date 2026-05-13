package com.palmer.billingstatementgenerator.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logs workflow tab navigation events.
 * INFO level records the destination tab name; DEBUG adds the step position
 * within the total workflow to aid in reconstructing a user's path through
 * the billing statement.
 */
public class TabNavigationLogger {

    private static final Logger logger = LoggerFactory.getLogger(TabNavigationLogger.class);

    /**
     * Logs a tab navigation event.
     *
     * @param tabName
     *         the text label of the destination tab
     * @param step
     *         the one-based index of the destination tab
     * @param totalSteps
     *         the total number of tabs in the workflow
     */
    public void logNavigation(String tabName, int step, int totalSteps) {
        if (logger.isDebugEnabled()) {
            logger.info("NAV → {} (step {} of {})", tabName, step, totalSteps);
        } else if (logger.isInfoEnabled()) {
            logger.info("NAV → {}", tabName);
        }
    }
}