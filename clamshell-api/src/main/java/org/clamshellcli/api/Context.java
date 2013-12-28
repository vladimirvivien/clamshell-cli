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

import java.util.List;
import java.util.Map;

/**
 * The global context object that serves as the operating context for all components.
 * @author vladimir.vivien
 */
public interface Context{
    /**
     * Key to point to shared cli classloader
     */
    public static final String KEY_CLASS_LOADER = "key.classLoader";
    
    /**
     * Key to retrieve command-line input value from context store.
     */
    public static final String KEY_COMMAND_LINE_INPUT = "key.commandlineInput";
    
    /**
     * Key for command-line command arguments
     */
    public static final String KEY_COMMAND_LINE_ARGS = "key.commandParams";
    
    /**
     * Key for saving/retrieving the command map in the context.
     */
    public static final String KEY_COMMAND_MAP = "key.commandMap";
    
    /**
     * Key to save/retrieve Plugin instances.
     */
    public static final String KEY_PLUGINS = "key.plugins";
    
    /**
     * Key to retrieve prompt value from the context store.
     */
    public static final String KEY_PROMPT_VALUE = "key.promptValue";
    
    /**
     * Key to a Shell instance
     */
    public static final String KEY_SHELL_COMPONENT = "key.shellComponent";
    
    /**
     * Key to IOConsole instance
     */
    public static final String KEY_CONSOLE_COMPONENT = "key.consoleComponent";
    
    /**
     * Key to store Prompt instance
     */
    public static final String KEY_PROMPT_COMPONENT = "key.promptComponent";
    
    /**
     * Key to store loaded controllers
     */
    public static final String KEY_CONTROLLERS = "key.controllers";
    
    /**
     * Key to store loaded splash screen
     */
    public static final String KEY_SPLASH_SCREENS = "key.splashScreens";
    
    /**
     * Key to store/load Command instances.
     */
    public static final String KEY_COMMANDS = "key.commands";
    
    /**
     * Key to retrieve instance of InputStream;
     */
    public static final String KEY_INPUT_STREAM = "key.InputStream";
    
    /**
     * Key to retrieve instance of OutputStream
     */
    public static final String KEY_OUTPUT_STREAM = "key.OutputStream";
    
    /**
     * Output error stream
     */
    public static final String KEY_ERROR_STREAM = "key.ErrorStream";
    
    /**
     * Returns the context's store copy of it's internal map.
     * @return Map<String, ? extends Object>
     */
    public Map<String,? extends Object> getValues();
    
    /**
     * Overwrites the context's internal map store.
     * @param values Map to use
     */
    public void putValues(Map<String, ? extends Object> values);
    
    /**
     * Stores a value in the context's map store.impelements
     * @param key
     * @param val 
     */
    public void putValue(String key, Object val);
    
    /**
     * Retrieves a value from the context's map store.
     * @param key
     * @return 
     */
    public Object getValue(String key);
    
    /**
     * Removes a value from the context's map store.
     * @param key 
     */
    public void removeValue(String key);
    
    /**
     * Returns the context classloader 
     * @return 
     */
    public ClassLoader getClassLoader();
    
    /**
     * Returns an instance of the Configurator object.
     * @return Configurator
     */
    public Configurator getConfigurator();
    
    /**
     * Returns instance of Shell
     * @return Shell
     */
    public Shell getShell();
    
    /** 
     * Returns an instance of IOConsole
     * @return IOConsole
     */
    public IOConsole getIoConsole();
    
    /**
     * Returns an instance of Prompt
     * @return Prompt
     */
    public Prompt getPrompt();
    
    /**
     * Retrieves a collection of all plugins loaded at startup.  Note the 
     * collection is returning instances and not class types.
     * Implementation should think about caching strategy for performance.
     * @return List<Plugin>
     */
    public List<Plugin> getPlugins();
    
    /**
     * Convenience method that retrieves a filtered list of instances based on
     * the typed specified.
     * @param <T> the type to use as filter.
     * @param type the actual type instance
     * @return list of objects that implements the filter type.
     */
    public <T> List<T> getPluginsByType(Class<T> type);
    
    
    /***
     * Returns a list of loaded controllers.
     * @return 
     */
    public List<InputController> getControllers();
    
    /**
     * Returns list of loaded splash screens;
     * @return 
     */
    public List<SplashScreen> getSplashScreens();
    
    /**
     * A convenience method that retrieves a list of Command plugins.
     * 
     * @return list of Command instances.
     */
    public List<Command> getCommands();
    
    /**
     * A convenience method to retrieve Command instances from the classpath
     * using the Command.Descriptor.getNamespace() value.
     * 
     * @param namespace namespace filter used to retrieve commands
     * @return List<Command>
     */
    public List<Command> getCommandsByNamespace(String namespace);
    
    /**
     * Maps all of the commands.  The default implementation should map
     * each command using Command.Descriptor.getName() as the key.
     * @param commands a collection of commands to map.
     * @return Map<String, Command> where Command.Descriptor.getName() is the key. 
     */
    public Map<String,Command> mapCommands(List<Command> commands);
}
