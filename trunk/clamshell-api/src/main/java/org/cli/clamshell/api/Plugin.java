package org.cli.clamshell.api;

/**
 * Interface for Plugin.  This is the root interface for all other runtime-loadable 
 * compoennts for clamshell.
 * @author vladimir vivien
 */
public interface Plugin {
    /**
     * This is the entry point to all plugin components when they instantiated
     * by the clam container.
     * @param plug the global context for component.
     */
    public void plug(Context plug);
}