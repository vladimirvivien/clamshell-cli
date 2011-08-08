package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import cli.clamshell.api.Controller;
import java.lang.String;
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
    private Map<String,Command> commands;
    
    /**
     * Handles incoming command-line input.  CmdController first splits the
     * input and uses token[0] as the action name mapped to the Command.
     * @param ctx the shell context.
     */
    public void handle(Context ctx) {
        String cmdLine = (String)ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        if(!cmdLine.isEmpty()){
            String[] tokens = cmdLine.split("\\s+");
            ctx.getIoConsole().writeOutput("Command.action = " + tokens[0]);
            System.out.println ("**** " + commands);
            Command cmd = commands.get(tokens[0]);
            if(cmd != null){
                //cmd.execute(ctx);
            }
        }
    }

    /**
     * Entry point for the plugin.
     * @param plug 
     */
    public void plug(Context plug) {
        List<Command> loadedCmds = plug.getPluginsByType(Command.class);
        commands = new HashMap<String, Command>(loadedCmds.size());
        for(Command cmd : loadedCmds){
            commands.put(cmd.getAction(), cmd);
        }
    }
    
}
