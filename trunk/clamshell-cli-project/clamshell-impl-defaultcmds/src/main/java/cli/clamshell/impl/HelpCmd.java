package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import com.beust.jcommander.Parameter;

/**
 * This class implements the Help command.
 * <ul>
 * <li> Usage: help - displays description for all installed commands.
 * <li> Usage: help [command_name] displays command usage.
 * </ul>
 * @author vladimir.vivien
 */
public class HelpCmd implements Command{
    private static final String CMD_NAME = "help";
    @Override
    public Descriptor getDescriptor() {
        return new Descriptor (){

            public String getName() {
                return CMD_NAME;
            }

            public String getDescription() {
                return "Prints help information for the shell.";
            }

            public String getUsage() {
                return "Type 'help' for command description or 'help [command]' for specific help.";
            }
            
            @Parameter
            public String cmdName;
        };
    }

    @Override
    public Object execute(Context ctx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void plug(Context plug) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
