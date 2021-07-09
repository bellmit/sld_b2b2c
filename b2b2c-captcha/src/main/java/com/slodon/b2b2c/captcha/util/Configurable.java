package com.slodon.b2b2c.captcha.util;

/**
 * This interface determines if a class can be configured by properties handled
 * by config manager.
 */
public abstract class Configurable {
    private Config config = null;

    public Config getConfig() {
        return this.config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
