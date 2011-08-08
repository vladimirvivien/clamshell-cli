package cli.clamshell.api;

import java.util.List;
import java.util.Map;

/**
 * The global context object that serves as the operating context for all components.
 * @author vladimir.vivien
 */
public interface Context{
    /**
     * Key to retrieve command-line input value from context store.
     */
    public static final String KEY_COMMAND_LINE_INPUT = "key.commandlineInput";
    
    /**
     * Key to retrieve prompt value from the context store.
     */
    public static final String kEY_PROMPT_VALUE = "key.promptValue";
    
    /**
     * Key to retrieve instance of InputStream;
     */
    public static final String KEY_INPUT_STREAM = "key.InputStream";
    
    /**
     * Key to retrieve instance of OutputStream
     */
    public static final String KEY_OUTPUT_STREAM = "key.OutputStream";
    
    /**
     * Returns the context's store copy of it's internal map.
     * @return Map<String, ? extends Object>
     */
    public Map<String,? extends Object> getValues();
    
    /**
     * Overwrites the context's internal map store.
     * @param values Map to use
     */
    public void putValues(Map<String, ? extends Object> values);
    
    /**
     * Stores a value in the context's map store.impelements
     * @param key
     * @param val 
     */
    public void putValue(String key, Object val);
    
    /**
     * Retrieves a value from the context's map store.
     * @param key
     * @return 
     */
    public Object getValue(String key);
    
    /**
     * Removes a value from the context's map store.
     * @param key 
     */
    public void removeValue(String key);
    
    /**
     * Returns an instance of the Configurator object.
     * @return Configurator
     */
    public Configurator getConfigurator();
    
    /**
     * Returns instance of Shell
     * @return Shell
     */
    public Shell getShell();
    
    /** 
     * Returns an instance of IOConsole
     * @return IOConsole
     */
    public IOConsole getIoConsole();
    
    /**
     * Returns an instance of Prompt
     * @return Prompt
     */
    public Prompt getPrompt();
    
    /**
     * Retrieves a collection of all plugins loaded at startup.  Note the 
     * collection is returning instances and not class types.
     * Implementation should think about caching strategy for performance.
     * @return List<Plugin>
     */
    public List<Plugin> getPlugins();
    
    /**
     * Convenience method that retrieves a filtered list of instances based on
     * the typed specified.
     * @param <T> the type to use as filter.
     * @param type the actual type instance
     * @return list of objects that implements the filter type.
     */
    public <T> List<T> getPluginsByType(Class<T> type);
}
