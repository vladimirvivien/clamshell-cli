package org.cli.clamshell.api;

/**
 * This interface encapsulates the configuration of the entire system.
 * Use this to discover how the system is configured (not to set it).
 * @author vvivien
 */
public interface Configurator {
    public Object getProperty(String key);
}
