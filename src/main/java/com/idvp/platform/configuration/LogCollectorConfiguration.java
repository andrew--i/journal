package com.idvp.platform.configuration;

import java.util.HashMap;
import java.util.Map;

public class LogCollectorConfiguration {

    public static final String TAILING_PANEL_PLAY = "gui.tailingview.play";
    public static final String TAILING_PANEL_FOLLOW = "gui.tailingview.follow";
    private Map<String, Object> config = new HashMap<>();

    public void setProperty(String name, Object value) {
        config.put(name, value);
    }

    public boolean getBoolean(String name) {
        return (boolean) config.get(name);
    }
}
