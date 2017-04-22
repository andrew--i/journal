package com.idvp.platform.journal;

import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.util.Loader;
import com.idvp.platform.journal.configuration.JournalProviderConfigurator;
import com.idvp.platform.journal.configuration.discriminator.JournalDiscriminatorDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class JournalProvider implements LifeCycle {


    private static Logger logger = LoggerFactory.getLogger(JournalProvider.class);

    private ConcurrentHashMap<String, JournalFactory> journalFactoryMap = new ConcurrentHashMap<>();

    private String configPath;
    private volatile AtomicBoolean isStarted = new AtomicBoolean(false);
    private JournalDiscriminatorDefault journalDiscriminator;
    private String configContent;

    public JournalProvider() {
    }

    public JournalProvider(String configPath) {
        this.configPath = configPath;
    }

    public void lazyInit() {
        try {
            JournalProviderConfigurator configurator = new JournalProviderConfigurator();
            if (configPath == null) {
                this.configContent = configurator.autoConfig(Loader.getTCL());
            } else {
                this.configContent = configurator.configByPath(configPath, Loader.getTCL());
            }
            journalDiscriminator = configurator.createDiscriminator(this, this.configContent);
        } catch (JournalException e) {
            stop();
            logger.error("Could not initialize journal factory", e);
        }
    }

    private synchronized void checkStartProvider() {
        if (!isStarted()) {
            lazyInit();
            start();

        }
    }

    @Override
    public void start() {
        if (journalDiscriminator != null)
            journalDiscriminator.stop();
        journalFactoryMap.values().forEach(JournalFactory::start);
        isStarted.set(true);
    }

    @Override
    public void stop() {
        journalFactoryMap.values().forEach(JournalFactory::stop);
    }

    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    public <T> void write(T record) throws JournalException {
        checkStartProvider();
        JournalFactory journalFactory = getOrCreateJournalFactoryByRecord(record);
        journalFactory.write(record);
    }


    public <T> Collection<T> read(Class<T> tClass) {
        checkStartProvider();
        JournalFactory journalFactory = getOrCreateJournalFactoryByClass(tClass);
        return journalFactory.read(tClass);
    }

    public <T> Collection<T> read(String key) {
        checkStartProvider();
        JournalFactory journalFactory = getOrCreateJournalFactoryByKey(key);
        return journalFactory.read(key);
    }


    public <T> Journal<T> get(String journalKey) {
        checkStartProvider();
        final JournalFactory journalFactory = getOrCreateJournalFactoryByKey(journalKey);
        return journalFactory.get(journalKey);
    }

    public Journal getByRecord(String key, String discriminatorValue, Object record) {
        JournalFactory journalFactory;
        if (!journalFactoryMap.containsKey(discriminatorValue)) {
            journalFactory = createJournalFactory(key, discriminatorValue);
        } else {
            journalFactory = journalFactoryMap.get(discriminatorValue);
        }
        return journalFactory.getByRecord(record);
    }

    public Journal getByClass(String key, String discriminatorValue, Class<?> journalRecordClass) {
        JournalFactory journalFactory;
        if (!journalFactoryMap.containsKey(discriminatorValue)) {
            journalFactory = createJournalFactory(key, discriminatorValue);
        } else {
            journalFactory = journalFactoryMap.get(discriminatorValue);
        }
        return journalFactory.getByClass(journalRecordClass);
    }


    private JournalFactory getOrCreateJournalFactoryByRecord(Object record) {
        final String value = journalDiscriminator.getJournalDiscriminatingValueByRecord(record);
        if (journalFactoryMap.containsKey(value))
            return journalFactoryMap.get(value);
        return createJournalFactory(journalDiscriminator.getKey(), value);
    }

    private JournalFactory getOrCreateJournalFactoryByClass(Class<?> tClass) {
        final String value = journalDiscriminator.getJournalDiscriminatingValueByClass(tClass);
        if (journalFactoryMap.containsKey(value))
            return journalFactoryMap.get(value);
        return createJournalFactory(journalDiscriminator.getKey(), value);
    }

    private JournalFactory getOrCreateJournalFactoryByKey(String key) {
        final String journalDiscriminatorKey = journalDiscriminator.getJournalDiscriminatingValueByJournalKey(key);
        if (journalFactoryMap.containsKey(journalDiscriminatorKey))
            return journalFactoryMap.get(journalDiscriminatorKey);
        return createJournalFactory(journalDiscriminator.getKey(), journalDiscriminatorKey);
    }

    private JournalFactory createJournalFactory(String discriminatorKey, String discriminatorValue) {
        final String journalFactoryConfig = configContent.replace("${" + discriminatorKey + "}", discriminatorValue);
        final JournalFactory journalFactory = new JournalFactory(journalFactoryConfig);
        journalFactoryMap.put(discriminatorValue, journalFactory);
        return journalFactory;
    }
}
