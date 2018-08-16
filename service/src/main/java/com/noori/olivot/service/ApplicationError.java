package com.noori.olivot.service;

/**
 * Generic application error
 */
public final class ApplicationError {
    private final String message;

    /**
     * Initializes a new instance of {@link ApplicationError}
     *
     * @param message Message to display
     */
    public ApplicationError(String message) {
        this.message = message;
    }

    /**
     * Gets the error message
     *
     * @return The error message
     */
    public String getMessage() {
        return message;
    }
}
