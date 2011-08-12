package cli.clamshell.impl;

import cli.clamshell.api.Command;
import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import java.util.Collections;
import java.util.Map;

/**
 * This class implements the Command component.  It responds to the the "exit"
 * action
 * @author vvivien
 */
public class ExitCmd implements Command {
    private static final String ACTION_NAME = "exit";

    @Override
    public Object execute(Context ctx) {
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
            @Override
            public String getName() {
                return ACTION_NAME;
            }

            @Override
            public String getDescription() {
               return "Exits ClamShell.";
            }

            @Override
            public String getUsage() {
                return "Type 'exit'";
            }

            @Override
            public Map<String, String> getArgsDescription() {
                return Collections.emptyMap();
            }
        };
    }
    
}
