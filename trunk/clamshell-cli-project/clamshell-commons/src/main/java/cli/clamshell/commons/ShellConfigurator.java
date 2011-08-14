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
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import cli.clamshell.api.Configurator;

/**
 * This is a default implementation of the Configurator.
 * It provides configuration infomation about the shell.
 * @author vvivien
 */
public class ShellConfigurator implements Configurator{
    private static final Logger log = Logger.getLogger(ShellConfigurator.class.getName());        
    private static String CONFIG_FILE_PATH = null;

    private Properties properties;

    // setup path to properties file
    static{
        try{
            CONFIG_FILE_PATH = new File(
                    (System.getProperty(KEY_PROP_FILE) == null)
                    ? VALUE_DIR_CONF + "/" + VALUE_PROP_FILE
                    : System.getProperty(KEY_PROP_FILE)
            ).getCanonicalPath();
        }catch(IOException ex){
            CONFIG_FILE_PATH = null;
            log.log(Level.SEVERE, "IO Error while configuring properties file: ", ex.getMessage());
        }
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
            File propFile = new File(CONFIG_FILE_PATH);
            if(propFile.exists() && propFile.isFile()){
                try{
                    FileInputStream propStream = new FileInputStream(propFile);
                    properties.load(propStream);
                }catch(Exception ex){
                    log.log(Level.SEVERE, "IO Error while loading properties file: ", ex.getMessage());
                }
            }
        }else{
            log.log(Level.WARNING, "Properties file not loaded correctly.");
        }
    }
    
}
