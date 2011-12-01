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

import cli.clamshell.api.Command;
import cli.clamshell.api.IOConsole;
import cli.clamshell.api.Prompt;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.api.Plugin;
import cli.clamshell.api.Shell;
import java.lang.String;

/**
 * Implementation of the Context used to provide shell information at runtime.
 * @author vvivien
 */
public class ShellContext implements Context{
    private static final Logger log = Logger.getLogger(ShellContext.class.getName());
    private Map<String, Object> values;
    private static ShellContext context;
    private Configurator config;
    private List<Plugin> plugins;
    private ClassLoader classLoader;
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
        config = ShellConfigurator.createNewInstance();
    }
    
    /**
     * Gets the last instance of ClassLoader created, otherwise it creates one.
     * Internally, it setups a class loader for the path specified in property cli.dir.plugins
     * (or defaults to directory ./plugins)
     * Any jar fiels found at that location will be cloaded in that class loader.
     * @return 
     */
    private ClassLoader getClassLoader(){
        if(classLoader != null) return classLoader; // return if already loaded.
        
        String pluginsDirName = config.getPropertiesMap().get(ShellConfigurator.KEY_CONFIG_PLUGINSDIR);
        File pluginsDir = new File(pluginsDirName);
        if(pluginsDir.exists() && pluginsDir.isDirectory()){
            try {
                classLoader = Clamshell.Runtime.createClassLoaderForPath(
                    new File[]{pluginsDir},
                    Thread.currentThread().getContextClassLoader()
                );
            } catch (Exception ex) {
               throw new RuntimeException(ex);
            }
        }else{
            throw new RuntimeException (String.format("Unable to find plugins directory "
                    + "[%s]. Clamshell will stop.", pluginsDir.getAbsolutePath()));
        }
        
        return classLoader;
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
        return config;
    }
    
    /**
     * Returns a list of the loaded Plugin instances.
     * @return List <Plugin> 
     */
    @Override
    public List<Plugin> getPlugins(){
        if(plugins != null) return plugins;
        ClassLoader cl = getClassLoader();
        plugins = Clamshell.Runtime.loadPlugins(cl);
        return plugins;
    }
    
    /**
     * Retrieves a list of Class instances using the provided Type.
     * @param <T> The generic type used to filter the plugins by type
     * @param type the Class to used as filter
     * @return List of components of type <T> 
     */
    @Override
    public <T> List<T> getPluginsByType(Class<T> type) {
        List<T> result = new ArrayList<T>();
        for (Plugin p : getPlugins()) {
            if (type.isAssignableFrom(p.getClass())) {
                result.add((T) p);
            }
        }
        return result;
    }
    
    /**
     * Returns an instance of Plugin of type Shell.
     * @return Shell
     */
    @Override
    public Shell getShell() {
        if(shell != null) return shell;
        List<Shell> shells = getPluginsByType(Shell.class);
        shell = (shells.size() > 0) ? shells.get(0) : null;
        return shell;
    }
    

    @Override
    public IOConsole getIoConsole() {
        if(console != null) return console;
        List<IOConsole> consoles = getPluginsByType(IOConsole.class);
        console = (consoles.size() > 0) ? consoles.get(0) : null;
        return console;
    }

    @Override
    public Prompt getPrompt() {
        if(prompt != null) return prompt;
        List<Prompt> prompts = getPluginsByType(Prompt.class);
        prompt = (prompts.size() > 0) ? prompts.get(0) : new DefaultPrompt();
        return prompt;
    }

    @Override
    public List<Command> getCommands() {
        if(commands != null) return commands;
        commands = getPluginsByType(Command.class);
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
