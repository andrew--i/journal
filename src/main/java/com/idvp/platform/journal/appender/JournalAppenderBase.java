package com.idvp.platform.journal.appender;

import ch.qos.logback.core.spi.ContextAwareBase;
import com.idvp.platform.journal.JournalException;

public abstract class JournalAppenderBase<T> extends ContextAwareBase implements JournalAppender<T> {

    protected boolean started = false;
    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public synchronized void doAppend(String key, T record) throws JournalException {

        try {

            if (!this.started) {
                throw new JournalException("Attempted to append to non started appender [" + name + "].");
            }

            // ok, we now invoke derived class' implementation of append
            this.append(key, record);

        } catch (Exception e) {
            if (e instanceof JournalException) {
                throw (JournalException) e;
            } else {
                throw new JournalException("Failed to audit an event", e);
            }
        }
    }

    protected abstract void append(String key, T record);

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    public boolean isStarted() {
        return started;
    }
}
