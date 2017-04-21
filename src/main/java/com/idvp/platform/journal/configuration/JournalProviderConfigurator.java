package com.idvp.platform.journal.configuration;

import ch.qos.logback.core.util.Loader;
import com.idvp.platform.journal.JournalException;
import com.idvp.platform.journal.configuration.discriminator.JournalDiscriminator;
import com.idvp.platform.journal.configuration.discriminator.NoOpJournalDiscriminator;
import jdk.nashorn.api.scripting.URLReader;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

public class JournalProviderConfigurator {

    public static final String AUTOCONFIG_FILE = "idvp.platform.journal.xml";
    public static final String AUTOCONFIG_FILE_PROPERTY = "idvp.platform.journal.configByPath.file";


    public String autoConfig(ClassLoader classLoader) throws JournalException {

        String autoConfigFileByProperty = System.getProperty(AUTOCONFIG_FILE_PROPERTY);
        URL url;

        if (autoConfigFileByProperty != null) {
            url = Loader.getResource(autoConfigFileByProperty, classLoader);
        } else {
            url = Loader.getResource(AUTOCONFIG_FILE, classLoader);
        }
        if (url != null) {
            return configureByResource(url);
        } else {
            String errMsg;
            if (autoConfigFileByProperty != null) {
                errMsg = "Failed to find configuration file [" + autoConfigFileByProperty + "].";
            } else {
                errMsg = "Failed to find logback-audit configuration files  [" + AUTOCONFIG_FILE + "].";
            }
            throw new JournalException(errMsg);
        }
    }

    private String configureByResource(URL url) throws JournalException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new URLReader(url))) {
            bufferedReader.lines().forEach(l -> {
                builder.append(l).append('\n');
            });
        } catch (IOException e) {
            throw new JournalException("Configuration failure in " + url, e);
        }
        return builder.toString();
    }

    public String configByPath(String configPath, ClassLoader tcl) throws JournalException {
        URL url = tcl.getResource(configPath);
        return configureByResource(url);
    }

    public JournalDiscriminator createDiscriminator(String configContent) throws JournalException {
        if (StringUtils.isEmpty(configContent))
            return new NoOpJournalDiscriminator();
        StringBuilder discriminatorClass = new StringBuilder();
        try (StringReader stringReader = new StringReader(configContent)) {
            final Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(stringReader));
            final NodeList discriminatorNodes = document.getElementsByTagName("discriminator");

            if (discriminatorNodes.getLength() == 0 || discriminatorNodes.getLength() > 1)
                return new NoOpJournalDiscriminator();

            discriminatorClass.append(discriminatorNodes.item(0).getAttributes().getNamedItem("class").getNodeValue());

        } catch (SAXException | ParserConfigurationException | IOException e) {
            throw new JournalException("Could not create discriminator", e);
        }

        if (discriminatorClass.length() == 0)
            return new NoOpJournalDiscriminator();

        try {
            final Class<?> discriminator = Class.forName(discriminatorClass.toString());
            return (JournalDiscriminator) discriminator.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new JournalException("Could not create discriminator instance", e);
        }
    }
}
