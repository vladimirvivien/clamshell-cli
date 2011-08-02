package cli.clamshell.impl;

import java.util.Map;
import org.cli.clamshell.api.Context;

/**
 * Implementation of the Context used to provide shell information at runtime.
 * @author vvivien
 */
public class ShellContext implements Context{

    public Map<String, ? extends Object> getValues() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void putValues(Map<String, ? extends Object> values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void putValue(String key, Object val) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getValue(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeValue(String key) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
