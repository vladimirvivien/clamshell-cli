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
package org.clamshellcli.api;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * This interface encapsulates the configuration of Clamshell-Cli.
 * Implement the getProperty() method to expose system-wide property values.
 * @author vladimir.vivien
 */
public interface Configurator {
    public static String VALUE_DIR_ROOT = ".";    
    public static final String KEY_CONFIG_FILE  = "cli.config.file"; // property name for property file.
    public static final String KEY_CONFIG_PROPS = "properties";
    public static final String KEY_CONFIG_CTRLS = "controllers";
    
    public static final String VALUE_CONFIG_PLUGINSDIR = "plugins";
    public static final String VALUE_CONFIG_LIBDIR = "lib";
    public static final String VALUE_CONFIG_FILE = "cli.config";    
    public static final String VALUE_LINE_SEP = System.getProperty("line.separator");
    public static final String VALUE_USERHOME = System.getProperty("user.home");
    
    public static final Pattern JARFILE_PATTERN = Pattern.compile(".*\\.jar");
    
    /**
     * Returns the raw config map from cli.config. 
     * @return Map<String,Map<String,Object>> containing all parsed content
     */
    public Map<String,Map<String,? extends Object>> getConfigMap();
    
    /**
     * Convenience method to return the "properties" section of config map.
     * @return Map<String,String>
     */
    public Map<String,String> getPropertiesMap();
    
    /**
     * Convenience method to return the "controllers" section of config map.
     * @return Map<String,Map<String,? extends Object>>
     */
    public Map<String,Map<String, ? extends Object>> getControllersMap();
}
