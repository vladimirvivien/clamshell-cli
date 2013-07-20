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
package org.clamshellcli.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;
import org.clamshellcli.api.Configurator;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Map;
import static org.clamshellcli.api.Configurator.KEY_CONFIG_FILE;

/**
 * This is a default implementation of the Configurator.
 * It provides configuration infomation about the shell.
 * @author vvivien
 */
public class ShellConfigurator implements Configurator{
    private static final Logger log = Logger.getLogger(ShellConfigurator.class.getName());
    private static final String CONFIG_FILE_PATH = "./" + VALUE_CONFIG_FILE;
    private File configFile;
    private Map<String, Map<String,?>> configMap;

    private ShellConfigurator(String configFileName){
        configFile = createConfigFile(configFileName);
        initialize();
    }
    
    public static ShellConfigurator createNewInstance(String configFileName){
        return new ShellConfigurator(configFileName);
    }
    
    public static ShellConfigurator createNewInstance(){
        return new ShellConfigurator(null);
    }
    
    public File getConfigFile() {
        return configFile; 
    }
    
    @Override
    public Map<String, Map<String,? extends Object>> getControllersMap() {
        return (configMap != null) ? 
            (Map<String, Map<String, ? extends Object>>) configMap.get(KEY_CONFIG_CTRLS) :
            null;
    }
    
    @Override
    public Map<String,String> getPropertiesMap(){
        return (configMap != null ) ? 
            (Map<String, String>) configMap.get(KEY_CONFIG_PROPS) :
            null;
    }
    
    @Override
    public Map<String,Map<String, ?>> getConfigMap(){
        return configMap;
    }
    
    private File createConfigFile(String fileName){
        return (fileName != null ) ? new File(fileName) :
            new File(
                (System.getProperty(KEY_CONFIG_FILE) != null)
                ? System.getProperty(KEY_CONFIG_FILE)
                : CONFIG_FILE_PATH
            );         
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
        }
    }
   
}
