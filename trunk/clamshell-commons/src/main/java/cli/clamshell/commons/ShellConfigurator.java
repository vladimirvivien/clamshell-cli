package cli.clamshell.commons;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import org.cli.clamshell.api.Configurator;

/**
 * This is a default implementation of the Configurator.
 * It provides configuration infomation about the shell.
 * @author vvivien
 */
public class ShellConfigurator implements Configurator{
    public static final String KEY_PROP_FILE = "cli.prop.file"; // property name
    public static final String KEY_DIR_PLUGINS = "cli.plugins.dir";
    public static final String KEY_DIR_LIB = "cli.lib.dir";
    
    public static final String VALUE_DIR_CONF = "./conf";
    public static final String VALUE_FILE_CONF = "cli.properties";
    public static final String VALUE_DIR_PLUGINS = "./plugins";
    public static final String VALUE_DIR_LIB = "./lib";
    
    private static String CONFIG_FILE_PATH = null;

    private Properties properties;

    // load properties file
    static{
        CONFIG_FILE_PATH =
                (System.getProperty(KEY_PROP_FILE) == null)
                ? VALUE_DIR_CONF + "/" + VALUE_FILE_CONF
                : System.getProperty(KEY_PROP_FILE);
    }

    private ShellConfigurator(){
        initialize();
    }
    
    public static ShellConfigurator createNewInstance(){
        return new ShellConfigurator();
    }

    public Object getProperty(String key){
        return properties.get(key);
    }

    private void initialize() {
        properties = new Properties();
        if(CONFIG_FILE_PATH != null){
            try{
                FileInputStream propFile = new FileInputStream(new File(CONFIG_FILE_PATH));
                System.out.println ("*********" + new File(CONFIG_FILE_PATH).getCanonicalPath());
                properties.load(propFile);
            }catch(Exception ex){
                // Something went wrong, load defaults at least
                properties.put(KEY_DIR_PLUGINS, VALUE_DIR_PLUGINS);
                properties.put(KEY_DIR_LIB, VALUE_DIR_LIB);
            }
        }
    }
    
}
