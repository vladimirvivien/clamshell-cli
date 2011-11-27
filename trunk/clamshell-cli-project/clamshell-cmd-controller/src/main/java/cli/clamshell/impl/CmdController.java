/*
 * #%L
 * clamshell-cmd-controller
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
package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.api.InputController;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * This implementation of the InputController component uses the InputController/Command
 * pattern.  First, when the compnent is plugged in, it creates an internal
 * map of all of the commands found on the classpath.  Each Command instance 
 * is mapped to its Action string as a key.
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
public class CmdController implements InputController{
    private static final String CMD_PATTERN = "\\s*(\\w)+\\b.*";
    private static final Pattern pattern = Pattern.compile(CMD_PATTERN);
    private String[] expectedInputs;
    private Map<String, String[]> commandHints;
    
    /**
     * Handles incoming command-line input.  CmdController first splits the
     * input and uses token[0] as the action name mapped to the Command.
     * @param ctx the shell context.
     */
    public boolean handle(Context ctx) {
        String cmdLine = (String)ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        boolean handled = false;
        // handle command line entry.  NOTE: value can be null
        if(cmdLine != null && !cmdLine.trim().isEmpty()){
            String[] tokens = cmdLine.trim().split("\\s+");
            Map<String,Command> commands = (Map<String,Command>) ctx.getValue(Context.KEY_COMMAND_MAP);
            if(!commands.isEmpty()){
                Command cmd = commands.get(tokens[0]);
                if(cmd != null){
                    if(tokens.length > 1){
                        String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);
                        ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, args);
                    }
                    cmd.execute(ctx);
                    handled = true;
                }else{
                    ctx.getIoConsole().writeOutput(String.format("%nCommand [%s] is unknown. "
                            + "Type help for a list of installed commands.%n%n", tokens[0]));
                }
            }
        }
        
        return handled;
    }

    /**
     * Entry point for the plugin.  This method loads the Command plugins found
     * on the classpath and maps them to their action name.
     * @param plug 
     */
    public void plug(Context plug) {
        List<Command> loadedCmds = plug.getCommands();
        Set<String> expectedCmds = new TreeSet<String>();
        if(loadedCmds.size() > 0){
            Map<String,Command> commands = new HashMap<String, Command>(loadedCmds.size());
            commandHints = new HashMap<String, String[]>();
            for(Command cmd : loadedCmds){
                cmd.plug(plug);
                Command.Descriptor desc = cmd.getDescriptor();
                if(desc != null){
                    String cmdName = desc.getName();
                    commands.put(cmdName, cmd);
                    expectedCmds.add(cmdName);
                    collectCommandHints(expectedCmds, cmd);
                }else{
                    plug.getIoConsole().writeOutput(
                        String.format("%nCommand [%] does not have a Command.Descriptor defined."
                            + "It will not be mapped.%nn", cmd.getClass().getCanonicalName())
                    );
                }
            }
            // save command map in Context;
            plug.putValue(Context.KEY_COMMAND_MAP, commands);
            expectedInputs = expectedCmds.toArray(new String[0]);
        }else{
            plug.getIoConsole().writeOutput(String.format("%nNo commands were mapped because none were found.%nn"));
        }
    }

    public Pattern respondsTo() {
        return pattern;
    }


    public String[] getExpectedInputs() {
        return expectedInputs;
    }

    public String[] getInputHints(String input) {
        return commandHints.get(input);
    }
        
    private void collectCommandHints(final Set<String> collection, final Command cmd){
        Map<String,String> args = (cmd.getDescriptor() != null ) ? cmd.getDescriptor().getArguments() : null;
        
        if(args != null){
            String cmdName = cmd.getDescriptor().getName();
            for(String hint : args.keySet()){
                // hints sometimes are provided as "option1, option2, etc"
                String[] hintSet = hint.split("\\s*,\\s*");
                for(String hintVal : hintSet){
                    collection.add(String.format("%s %s", cmdName, hintVal));
                }
            }
        }
    }
}
