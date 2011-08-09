package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;

/**
 * This class implements the Command component.  It responds to the the "exit"
 * action
 * @author vvivien
 */
public class ExitCmd implements Command {
    private static final String ACTION_NAME = "exit";

    @Override
    public Object execute(Context ctx) {
        ctx.getIoConsole().writeOutput("Bye" + Configurator.VALUE_LINE_SEP);
        System.out.flush();
        System.exit(0);
        return null;
    }

    @Override
    public void plug(Context plug) {
        // nothing to setup
    }
    
    @Override
    public Command.Descriptor getDescriptor(){
        return new Command.Descriptor() {

            public String getName() {
                return ACTION_NAME;
            }

            public String getDescription() {
               return "Exits Clamshell completely.";
            }

            public String getUsage() {
                return "Type 'exit' to quit the system.";
            }
        };
    }
    
}
