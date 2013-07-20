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

import org.clamshellcli.api.Command;
import org.clamshellcli.api.Context;
import org.clamshellcli.core.AnInputController;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * This is a simple implementation of the InputController.
 * It delegates handling of the input to Command plugins on the classpath.
 * <p/>
 * It creates an internal map of all of the commands found on the classpath.  
 * Each Command instance is mapped to String[0] from the command line input string.
 * 
 * When the controller receives command-line value pulled with 
 * Context.KEY_INPUT_LINE value, it splits it. The first token found is used to 
 * pull the Command object mapped to the token value.
 * 
 * prompt>Token1 Toke2 Token3 Token4
 * 
 * This InputController will split the prompt input 4 tokens.  Token1 will be used
 * to map to a Command instance that will handle the execution of the code.
 * 
 * @author vladimir.vivien
 */
public class CmdController extends AnInputController{
    private Map<String,Command> commands;
    private String respondsToRegEx = "\\s*(exit|help|sysinfo|time)\\b.*";
    private Pattern respondsTo;
    
    public CmdController() {
        respondsTo = (super.respondsTo() != null) ?
                super.respondsTo() : Pattern.compile(respondsToRegEx);
    }
    
    @Override
    public Pattern respondsTo() {
        return respondsTo;
    }
    
    /**
     * Handles incoming command-line input.  CmdController first splits the
     * input and uses token[0] as the action name mapped to the Command.
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
            if(!commands.isEmpty()){
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
     * Entry point for the plugin.  This method loads the Command plugins found
     * on the classpath and maps them to their action name.
     * @param plug 
     */
    @Override
    public void plug(Context plug) {
        super.plug(plug); // read config for this controller.
        List<Command> sysCommands = plug.getCommandsByNamespace("syscmd");
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
                    + " [%s].%nn", this.getClass().getName()));
        }
    }

}
