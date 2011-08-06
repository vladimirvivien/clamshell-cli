package cli.clamshell.api;

import java.util.List;
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
    
    public Configurator getConfigurator();
    public Shell getShell();
    public IOConsole getIoConsole();
    public Prompt getPrompt();
    public List<Plugin> getPlugins();
    public <T> List<T> getPluginsByType(Class<T> type);
}
