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
    public String getAction() {
        return ACTION_NAME;
    }

    public Object execute(Context ctx) {
        ctx.getIoConsole().writeOutput("Bye" + Configurator.VALUE_LINE_SEP);
        System.out.flush();
        System.exit(0);
        return null;
    }

    public void plug(Context plug) {
        // nothing to setup
    }
    
}
