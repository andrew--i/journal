package com.idvp.platform.journal;

public class JournalException extends Exception {
    public JournalException(String message) {
        super(message);
    }

    public JournalException(String message, Exception exception) {
        super(message, exception);
    }
}
