/*
 * #%L
 * clamshell-commands
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
import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This class implements the Help command.
 * <ul>
 * <li> Usage: help - displays description for all installed commands.
 * <li> Usage: help [command_name] displays command usage.
 * </ul>
 * @author vladimir.vivien
 */
public class HelpCmd implements Command{
    private static final String NAMESPACE = "syscmd";
    private static final String CMD_NAME = "help";
    private HelpCmdDescriptor descriptor = new HelpCmdDescriptor();
    
    private class HelpCmdDescriptor implements Command.Descriptor {
        @Override public String getNamespace() {return NAMESPACE;}
        
        @Override
        public String getName() {
            return CMD_NAME;
        }

        @Override
        public String getDescription() {
            return "Displays help information for available commands.";
        }

        @Override
        public String getUsage() {
            return "Type 'help' or 'help [command_name]' ";
        }

        @Override
        public Map<String, String> getArguments() {
            return Collections.emptyMap();
            
        }
    }
    
    @Override
    public Command.Descriptor getDescriptor() {
        return descriptor;
    }

    /**
     * Executes the Help command.
     * The help command expects format 'help [command_name]'.
     * If the optional command_name parameter is present, this class will 
     * display info about about the command.  If command_name is not present
     * the Help command displays a list of help for all installed command.
     * @param ctx
     * @return 
     */
    @Override
    public Object execute(Context ctx) {
        String[] args = (String[]) ctx.getValue(Context.KEY_COMMAND_LINE_ARGS);

        // if arg passed, display help for command matching arg.
        if(args != null && args.length > 0){
            printCommandHelp(ctx, args[0].trim());
        }else{
            printAllHelp(ctx);
        }
        return null;
    }

    @Override
    public void plug(Context plug) {
        // no plugin action needed
    }
    
    private void printCommandHelp(Context ctx, String cmdName){
        Map<String, Command> commands = (Map<String,Command>) ctx.getValue(Context.KEY_COMMAND_MAP);
        if(commands != null){
            Command cmd = commands.get(cmdName.trim());
            if(cmd != null){
                printCommandHelp(ctx, cmd);
            }else{
                ctx.getIoConsole().writeOutput(String.format("%nHelp for Command [%s] cannot not found.%n%n", cmdName));
            }
        }
    }
    
    private void printCommandHelp(Context ctx, Command cmd){
        if(cmd != null && cmd.getDescriptor() != null){
            ctx.getIoConsole().writeOutput(String.format("%nCommand: %s - %s%n", cmd.getDescriptor().getName(), cmd.getDescriptor().getDescription()));
            ctx.getIoConsole().writeOutput(String.format("Usage: %s%n%n", cmd.getDescriptor().getUsage()));            
        }else{
            ctx.getIoConsole().writeOutput(String.format("%nUnable to display help for command.%n%n"));
        }
    }
    
    private void printAllHelp(Context ctx){
        IOConsole c = ctx.getIoConsole();
        c.writeOutput(String.format("%nAvailable Commands"));
        c.writeOutput(String.format("%n------------------"));
        List<Command> commands = ctx.getCommands();
        for(Command cmd : commands){
            c.writeOutput(String.format(
                "%n%1$10s %2$5s %3$s", 
                cmd.getDescriptor().getName(), 
                " ", 
                cmd.getDescriptor().getDescription()
            ));
        }
        c.writeOutput(String.format("%n%n"));
    }
    
}
