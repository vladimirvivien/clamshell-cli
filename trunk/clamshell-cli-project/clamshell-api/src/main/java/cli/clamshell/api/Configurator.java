/*
 * #%L
 * clamshell-api
 * %%
 * Copyright (C) 2011 ClamShell-Cli
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package cli.clamshell.api;

/**
 * This interface encapsulates the configuration of Clamshell-Cli.
 * Implement the getProperty() method to expose system-wide property values.
 * @author vladimir.vivien
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
    
    public static final String VALUE_LINE_SEP = System.getProperty("line.separator");

    /**
     * Reads a property from the loaded cli.properties file.
     * @param key the name of the property
     * @return an object representing property value
     */
    public Object getProperty(String key);
}
