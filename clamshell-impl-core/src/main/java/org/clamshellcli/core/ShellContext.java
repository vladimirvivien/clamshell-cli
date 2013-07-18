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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.Plugin;
import org.clamshellcli.api.Shell;
import java.lang.String;

/**
 * Implementation of the Context used to provide shell information at runtime.
 * @author vvivien
 */
public class ShellContext implements Context{
    private static final Logger log = Logger.getLogger(ShellContext.class.getName());
    private Map<String, Object> values;
    private static ShellContext context;
    private Shell shell;
    private IOConsole console;
    private Prompt prompt;
    private List<Command> commands;

    /**
     * Creates an instance of ShellContext.
     * @return ShellContex
     */
    public static ShellContext createInstance() {
        if(context == null) {
            context = new ShellContext();
        }
        return context;
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
        return Clamshell.Runtime.getPlugins();
    }
    
    /**
     * Retrieves a list of Class instances using the provided Type.
     * @param <T> The generic type used to filter the plugins by type
     * @param type the Class to used as filter
     * @return List of components of type <T> 
     */
    @Override
    public <T> List<T> getPluginsByType(Class<T> type) {
        return Clamshell.Runtime.getPluginsByType(type);
    }
    
    /**
     * Returns an instance of Plugin of type Shell.
     * @return Shell
     */
    @Override
    public Shell getShell() {
        if(shell != null) return shell;
        List<Shell> shells = Clamshell.Runtime.getPluginsByType(Shell.class);
        shell = (shells.size() > 0) ? shells.get(0) : null;
        return shell;
    }
    

    @Override
    public IOConsole getIoConsole() {
        if(console != null) return console;
        List<IOConsole> consoles = Clamshell.Runtime.getPluginsByType(IOConsole.class);
        console = (consoles.size() > 0) ? consoles.get(0) : null;
        return console;
    }

    @Override
    public Prompt getPrompt() {
        if(prompt != null) return prompt;
        List<Prompt> prompts = Clamshell.Runtime.getPluginsByType(Prompt.class);
        prompt = (prompts.size() > 0) ? prompts.get(0) : new DefaultPrompt();
        return prompt;
    }

    @Override
    public List<Command> getCommands() {
        if(commands != null) return commands;
        commands = Clamshell.Runtime.getPluginsByType(Command.class);
        return commands;
    }
    
    @Override
    public List<Command> getCommandsByNamespace(String namespace){
        List<Command> result   = new ArrayList();
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
    
    private class DefaultPrompt implements Prompt{
        private final String value = System.getProperty("user.name") + " > ";
        public String getValue(Context ctx) {
            return value;
        }
        public void plug(Context plug) {}
    }
}
