/*
 * #%L
 * clamshell-commons
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
package cli.clamshell.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import cli.clamshell.api.Configurator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This is a default implementation of the Configurator.
 * It provides configuration infomation about the shell.
 * @author vvivien
 */
public class ShellConfigurator implements Configurator{
    private static final Logger log = Logger.getLogger(ShellConfigurator.class.getName());
    private static final String CONFIG_FILE_PATH = "./" + VALUE_CONFIG_FILE;
    private static File configFile;
    private Map<String, Map<String,?>> configMap;

    // setup path to properties file
    static {
        configFile = new File(
            (System.getProperty(KEY_CONFIG_FILE) != null)
             ? System.getProperty(KEY_CONFIG_FILE)
             : CONFIG_FILE_PATH);
    }

    private ShellConfigurator(){
        initialize();
    }
    
    public static ShellConfigurator createNewInstance(){
        return new ShellConfigurator();
    }

    public Map<String, Map<String,? extends Object>> getControllersMap() {
        return (Map<String, Map<String, ? extends Object>>) configMap.get(KEY_CONFIG_CTRLS);
    }
    
    public Map<String,String> getPropertiesMap(){
        return (Map<String, String>) configMap.get(KEY_CONFIG_PROPS);
    }
    
    public Map<String,Map<String, ?>> getConfigMap(){
        return configMap;
    }

    private void initialize() {
        if(configFile != null && configFile.exists() && configFile.isFile()){
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String,? extends Object>>(){}.getType();
            try {
                configMap = gson.fromJson(new FileReader(configFile), mapType);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }else{
            throw new RuntimeException(String.format(
                "Unable to find config file [%s]."
                + " Clamshell looks for config file in root directory"
                + " or via property -D%s.", configFile, KEY_CONFIG_FILE
            ));
        }
    }
    
}
