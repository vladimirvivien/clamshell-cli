package cli.clamshell.commons;

import java.util.HashMap;
import java.util.Map;
import org.cli.clamshell.api.Configurator;
import org.cli.clamshell.api.Context;

/**
 * Implementation of the Context used to provide shell information at runtime.
 * @author vvivien
 */
public class ShellContext implements Context{
    private Map<String, Object> values;
    private static ShellContext context;
    private Configurator config;

    public static ShellContext createInstance() {
        if(context == null) {
            context = new ShellContext();
        }
        return context;
    }
    
    private ShellContext(){
        values = new HashMap<String, Object>();
        config = ShellConfigurator.createNewInstance();
    }
    
    public Map<String, ? extends Object> getValues() {
        return values;
    }

    public void putValues(Map<String, ? extends Object> values) {
        this.values.putAll(values);
    }

    public void putValue(String key, Object val) {
        values.put(key,val);
    }

    public Object getValue(String key) {
        return values.get(key);
    }

    public void removeValue(String key) {
        values.remove(key);
    }

    public Configurator getConfigurator() {
        return config;
    }
    
    @Override
    public Context clone(){
        Context cloneCtx = ShellContext.createInstance();
        cloneCtx.putValues(values);
       return cloneCtx;
    }
}
