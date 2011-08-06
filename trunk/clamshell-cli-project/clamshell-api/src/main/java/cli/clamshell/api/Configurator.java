package cli.clamshell.api;

/**
 * This interface encapsulates the configuration of the entire system.
 * Use this to discover how the system is configured (not to set it).
 * @author vvivien
 */
public interface Configurator {
    public static String VALUE_DIR_ROOT = ".";    
    public static final String KEY_PROP_FILE = "cli.prop.file"; // property name for proper property file.
    public static final String VALUE_DIR_CONF = VALUE_DIR_ROOT + "/conf";
    public static final String VALUE_DIR_PLUGINS = VALUE_DIR_ROOT + "/plugins";
    public static final String VALUE_DIR_LIB = VALUE_DIR_ROOT + "/lib";
    public static final String VALUE_PROP_FILE = "cli.properties";

    public Object getProperty(String key);
}
