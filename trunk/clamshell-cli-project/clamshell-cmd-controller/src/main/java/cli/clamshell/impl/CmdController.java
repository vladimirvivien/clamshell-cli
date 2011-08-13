package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.api.Controller;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This implementation of the Controller component uses the Controller/Command
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
 * This Controller will split the prompt input 4 tokens.  Token1 will be used
 * to map to a Command instance that will handle the execution of the code.
 * 
 * @author vladimir.vivien
 */
public class CmdController implements Controller{
    
    /**
     * Handles incoming command-line input.  CmdController first splits the
     * input and uses token[0] as the action name mapped to the Command.
     * @param ctx the shell context.
     */
    public void handle(Context ctx) {
        String cmdLine = (String)ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        
        if(!cmdLine.trim().isEmpty()){
            String[] tokens = cmdLine.split("\\s+");
            Map<String,Command> commands = (Map<String,Command>) ctx.getValue(Context.KEY_COMMAND_MAP);
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
                            + "Type help for a list of installed commands.%n%n", tokens[0]));
                }
            }
        }
    }

    /**
     * Entry point for the plugin.  This method loads the Command plugins found
     * on the classpath and maps them to their action name.
     * @param plug 
     */
    public void plug(Context plug) {
        List<Command> loadedCmds = plug.getCommands();
        if(loadedCmds.size() > 0){
            Map<String,Command> commands = new HashMap<String, Command>(loadedCmds.size());
            for(Command cmd : loadedCmds){
                cmd.plug(plug);
                Command.Descriptor desc = cmd.getDescriptor();
                if(desc != null){
                    commands.put(desc.getName(), cmd);
                }else{
                    plug.getIoConsole().writeOutput(
                        String.format("%nCommand [%] does not have a Command.Descriptor defined."
                            + "It will not be mapped.%nn", cmd.getClass().getCanonicalName())
                    );
                }
            }
            // save command map in Context;
            plug.putValue(Context.KEY_COMMAND_MAP, commands);
        }else{
            plug.getIoConsole().writeOutput(String.format("%nNo commands were mapped because none were found.%nn"));
        }
    }
    
}
