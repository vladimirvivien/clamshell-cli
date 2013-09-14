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

import org.clamshellcli.api.Command;
import org.clamshellcli.api.IOConsole;
import org.clamshellcli.api.Prompt;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import static org.clamshellcli.api.Context.KEY_CONSOLE_COMPONENT;
import static org.clamshellcli.api.Context.KEY_SHELL_COMPONENT;
import org.clamshellcli.api.InputController;
import org.clamshellcli.api.Plugin;
import org.clamshellcli.api.Shell;
import org.clamshellcli.api.SplashScreen;

/**
 * Implementation of the Context used to provide shell information at runtime.
 * @author vvivien
 */
public class ShellContext implements Context{
    private static final Logger log = Logger.getLogger(ShellContext.class.getName());
    private Map<String, Object> values;

    /**
     * Creates an instance of ShellContext.
     * @return ShellContex
     */
    public static ShellContext createInstance() {
        return new ShellContext();
    }
    
    /**
     * Private constructor
     */
    private ShellContext(){
        values = new HashMap<String, Object>();
    }
    
    /**
     * Returns a copy of the internal Map that stores context values.
     * @return copy of values.
     */
    @Override
    public Map<String, ? extends Object> getValues() {
        return values;
    }

    /**
     * Adds a full map of values to the internal context Map.
     * @param values 
     */
    @Override
    public void putValues(Map<String, ? extends Object> values) {
        this.values.putAll(values);
    }

    /**
     * Adds one value to the internal context map.
     * @param key
     * @param val 
     */
    @Override
    public void putValue(String key, Object val) {
        values.put(key,val);
    }

    /**
     * Returns a value from the context map.
     * @param key
     * @return 
     */
    @Override
    public Object getValue(String key) {
        return values.get(key);
    }

    /**
     * Removes a single value from the context map.
     * @param key 
     */
    @Override
    public void removeValue(String key) {
        values.remove(key);
    }

    /**
     * Returns an instance of the Configurator object.
     * @return 
     */
    @Override
    public Configurator getConfigurator() {
        return Clamshell.Runtime.getConfigurator();
    }
    
    /**
     * Returns a list of the loaded Plugin instances.
     * @return List <Plugin> 
     */
    @Override
    public List<Plugin> getPlugins(){
        return (List<Plugin>) values.get(KEY_PLUGINS);
    }
    
    /**
     * Retrieves a list of Class instances using the provided Type.
     * @param <T> The generic type used to filter the plugins by type
     * @param type the Class to used as filter
     * @return List of components of type <T> 
     */
    @Override
    public <T> List<T> getPluginsByType(Class<T> type) {
        return Clamshell.Runtime.filterPluginsByType(getPlugins(),type);
    }
    
    /**
     * Returns the cli's context classloader.
     * @return 
     */
    @Override
    public ClassLoader getClassLoader() {
        return (ClassLoader) values.get(KEY_CLASS_LOADER);
    }
    
    /**
     * Returns an instance of Plugin of type Shell.
     * @return Shell
     */
    @Override
    public Shell getShell() {
        return (Shell) values.get(KEY_SHELL_COMPONENT);
    }
    

    @Override
    public IOConsole getIoConsole() {
        return (IOConsole) values.get(KEY_CONSOLE_COMPONENT);
    }

    @Override
    public Prompt getPrompt() {
        return (Prompt)values.get(KEY_PROMPT_COMPONENT);
    }
    
    @Override
    public List<InputController> getControllers() {
        return (List<InputController>) values.get(KEY_CONTROLLERS);
    }
    
    @Override
    public List<SplashScreen> getSplashScreens() {
        return (List<SplashScreen>) values.get(KEY_SPLASH_SCREENS);
    }

    @Override
    public List<Command> getCommands() {
        return (List<Command>) values.get(KEY_COMMANDS);
    }
    
    @Override
    public List<Command> getCommandsByNamespace(String namespace){
        List<Command> result = new ArrayList();
        for(Command cmd: getCommands()){
            Command.Descriptor desc = cmd.getDescriptor();
            if(desc != null && desc.getNamespace().equals(namespace)){
                result.add(cmd);
            }
        }
        return result;
    }
    
    @Override
    public Map<String,Command> mapCommands(List<Command> commands){
        Map<String,Command> cmdMap = new HashMap<String,Command>();
        for(Command cmd : commands){
            Command.Descriptor desc = cmd.getDescriptor();
            if(desc != null && desc.getName() != null){
                cmdMap.put(desc.getName(), cmd);
            }
        }
        return cmdMap;
    }    
}
