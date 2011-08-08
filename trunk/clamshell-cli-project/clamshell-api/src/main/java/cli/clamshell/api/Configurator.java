package cli.clamshell.api;

/**
 * This interface encapsulates the configuration of the entire system.
 * Use this to discover how the system is configured (not to set it).
 * @author vvivien
 */
public interface Configurator {
    public static String VALUE_DIR_ROOT = ".";    
    public static final String KEY_PROP_FILE = "cli.prop.file"; // property name for property file.
    public static final String KEY_DIR_CONF = "key.dir.conf";
    public static final String KEY_DIR_PLUGINS = "key.dir.plugins";
    public static final String KEY_DIR_LIB = "key.dir.lib";
    
    public static final String VALUE_DIR_CONF =  (
        System.getProperty(KEY_DIR_CONF) != null ? System.getProperty(KEY_DIR_CONF) 
        : VALUE_DIR_ROOT + "/conf"
    );
    
    public static final String VALUE_DIR_PLUGINS = (
        System.getProperty(KEY_DIR_PLUGINS) != null ? System.getProperty(KEY_DIR_PLUGINS)
        : VALUE_DIR_ROOT + "/plugins"
    );
    
    public static final String VALUE_DIR_LIB = (
        System.getProperty(KEY_DIR_LIB) != null ? System.getProperty(KEY_DIR_LIB)
        : VALUE_DIR_ROOT + "/lib"
    );
    public static final String VALUE_PROP_FILE = "cli.properties";

    /**
     * Reads a property from the loaded cli.properties file.
     * @param key
     * @return 
     */
    public Object getProperty(String key);
}
