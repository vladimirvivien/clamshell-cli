/*
 * Copyright 2011 ClamShell-Cli.
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
package cli.clamshell.jmx;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.commons.AnInputController;
import com.google.gson.Gson;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is an implementation of the InputController for the JMX CLI.
 * This implementation parses command-line input from Context(KEY_INPUT_LINE)
 * value. It parses the input using JSon notation.  The command-line input is 
 * expected to be of the form:
 * 
 * <code>cmd param0:value para1:value1 ... paramN:valueN</code>
 * 
 * The controller parses the input line and dispatches the parsed command and 
 * params to a Command instance on the classpath.
 * 
 * @author vladimir.vivien
 */
public class JmxController  extends AnInputController {
    private static final String JMX_NAMESPACE = "jmx";
    private Map<String, Command> commands;
    private Gson gson;
    
    @Override
    public boolean handle(Context ctx) {
        String cmdLine = (String)ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        boolean handled = false;
       
        if(cmdLine != null && !cmdLine.trim().isEmpty()){
            String[] tokens = cmdLine.split("\\s+");
            String cmdName = tokens[0];
            Map<String,Object> argsMap = null;
            
            // if there are arguments
            if(tokens.length > 1 ){
                String argsString = Arrays.toString(
                    Arrays.copyOfRange(tokens, 1, tokens.length)
                ).replace("[", "").replace("]", "");
                
                String argsJson = "{" + argsString + "}";
                try{
                    argsMap = gson.fromJson(argsJson, Map.class);
                }catch(Exception ex){
                    ctx.getIoConsole().writeOutput(
                        String.format("%nUnable to parse command parameters [%s]: "
                            + " %s.%n%n", argsJson, ex.getMessage()));
                }
            }
            
            // launch command
            Command cmd = null;
            if(commands != null && (cmd = commands.get(cmdName)) != null){
                ctx.putValue(Context.KEY_COMMAND_LINE_ARGS, argsMap);
                cmd.execute(ctx);
                handled = true;
            }else{
                handled = false;
            }
            
        }
        
        return handled;
    }

    @Override
    public void plug(Context plug) {
        super.plug(plug);
        gson = new Gson();
        
        List<Command> jmxCommands = plug.getCommandsByNamespace(JMX_NAMESPACE);
        if(jmxCommands.size() > 0){
            commands = plug.mapCommands(jmxCommands);
            Set<String> cmdHints = new TreeSet<String>();
            // plug each Command instance and collect input hints
            for(Command cmd : jmxCommands){
                cmd.plug(plug);
                cmdHints.addAll(collectInputHints(cmd));
            }

            // save expected command input hints
            setExpectedInputs(cmdHints.toArray(new String[0]));
            
        }else{
            plug.getIoConsole().writeOutput(
                String.format("%nNo commands were found for input controller"
                    + " [%s].%n%n", this.getClass().getName()));
        }
        
    }

}