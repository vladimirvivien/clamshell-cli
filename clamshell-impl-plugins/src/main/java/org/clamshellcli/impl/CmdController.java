/*
 * Copyright 2012 ClamShell-Cli.
 *
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
 */
package org.clamshellcli.impl;

import java.io.File;
import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;
import org.clamshellcli.core.AnInputController;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.IOConsole;
import org.clamshellcli.core.Clamshell;

/**
 * <p>
 * This implementation of the InputController works by delegating its input to 
 * Command instances to be handled.  The Command classes are assumed to be in
 * a directory called 'commands'.  The controller loads the classes from that
 * location and add them to its classloader.
 * <p/>
 * <p>
 * The controller then maps each Command instance to value Command.Descriptor.name.
 * When the controller receives an input line value, pulled from the context with 
 * Context.KEY_INPUT_LINE, it parses and splits it. The controller then matches
 * the first command it finds that matches input line.startsWith() value.
 * </p>
 * <p>
 * <b>ClassLoading</p>
 * This controller will load the Command instances found in the 'commands'
 * directory.  It will load any file with name ending in *.jar.  It will also
 * load any class files found in directory commands/classes.
 * </p>
 * @author vladimir.vivien
 */
public class CmdController extends AnInputController{
    private static Class COMMAND_TYPE = Command.class;
    private static String COMMANDS_DIR_NAME = "commands";
    private static String CLASSES_DIR_NAME = "classes";
    private static String DEFAULT_NAMESPACE="syscmd";
    
    private Map<String,Command> commands;
    private String respondsToRegEx = "(.*)\\b"; 
    private Pattern respondsTo = Pattern.compile(respondsToRegEx);
    
    public CmdController() {}
    
    @Override
    public Pattern respondsTo() {
        return respondsTo;
    }
    
    /**
     * Handles incoming command-line input.  CmdController first splits the
     * input into token[N] tokens.  It uses token[0] as the action name mapped 
     * to the Command.
     * @param ctx the shell context.
     */
    @Override
    public boolean handle(Context ctx) {
        String cmdLine = (String)ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        boolean handled = false;

        // handle command line entry.  NOTE: value can be null
        if(
            cmdLine != null && 
            !cmdLine.trim().isEmpty() &&
            respondsTo.matcher(cmdLine).matches()
        ){
            String[] tokens = cmdLine.trim().split("\\s+");
            if(commands != null && !commands.isEmpty()){
                Command cmd = commands.get(tokens[0]);
                if(cmd != null){
                    if(tokens.length > 1){
                        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
                        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, args);
                    }
                    cmd.execute(ctx);
                }else{
                    ctx.getIoConsole().writeOutput(String.format("%nCommand [%s] is unknown. "
                            + "Type help for a list of installed commands.", tokens[0]));
                }
                handled = true;
            }
        }
        
        return handled;
    }

    /**
     * Entry point for the plugin.  It builds class path from 'commands' directory.
     * Then loads each Command found.
     * @param plug 
     */
    @Override
    public void plug(Context plug) {
        List<Command> allCmds = loadCommands(plug);
        plug.putValue(Context.KEY_COMMANDS, allCmds);
        List<Command> sysCommands = plug.getCommandsByNamespace(DEFAULT_NAMESPACE);
        if(sysCommands.size() > 0){
            commands = plug.mapCommands(sysCommands);
            Set<String> cmdHints = new TreeSet<String>();
            // plug each Command instance and collect input hints
            for(Command cmd : sysCommands){
                cmd.plug(plug);
                cmdHints.addAll(collectInputHints(cmd));
            }
            
        }else{
            plug.getIoConsole().writeOutput(
                String.format("%nNo commands were found for input controller"
                    + " [%s].%n", this.getClass().getName()));
        }
    }
    
    private List<Command> loadCommands(Context plug) {
        File commandsDir = new File(COMMANDS_DIR_NAME);
        File classesDir  = new File(commandsDir, CLASSES_DIR_NAME);
        IOConsole console = plug.getIoConsole();
        List<Command> result = Collections.EMPTY_LIST;
        
        // load classes from jar files
        if(commandsDir.isDirectory()){
            ClassLoader jarsCl = null;
            try{
                jarsCl = Clamshell.ClassManager.getClassLoaderFromFiles(
                    new File[]{commandsDir}, 
                    Configurator.JARFILE_PATTERN,
                    plug.getClassLoader()
                );
                result = Clamshell.Runtime.loadServicePlugins(COMMAND_TYPE, jarsCl);
            }catch(Exception ex){
                // failure OK. No classes loaded
            }
        }
        
        // load raw java classes in commands/classes
        if(classesDir.isDirectory()){
            ClassLoader classesCl = null;
            try{
                classesCl = Clamshell.ClassManager.getClassLoaderFromDirs(
                    new File[]{classesDir}, 
                    Thread.currentThread().getContextClassLoader()
                );
                result.addAll(Clamshell.Runtime.loadServicePlugins(COMMAND_TYPE, classesCl));
            }catch(Exception ex){
                // failure OK. No classes loaded.
            }
        }
        return result;
    }

}
