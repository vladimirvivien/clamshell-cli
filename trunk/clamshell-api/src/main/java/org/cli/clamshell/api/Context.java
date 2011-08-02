package org.cli.clamshell.api;

import java.util.Map;

/**
 * The global context object that serves as the operating context for all components.
 * @author vladimir.vivien
 */
public interface Context {
    public Map<String,? extends Object> getValues();
    public void putValues(Map<String, ? extends Object> values);
    public void putValue(String key, Object val);
    public Object getValue(String key);
    public void removeValue(String key);
}
