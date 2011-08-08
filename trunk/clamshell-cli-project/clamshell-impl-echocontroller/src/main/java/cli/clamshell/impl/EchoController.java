package cli.clamshell.impl;

import cli.clamshell.api.Context;
import cli.clamshell.api.Controller;
import cli.clamshell.api.IOConsole;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * A simple implementation of a Controller that echos back input send to the 
 * command-line.  This is meant to be demo toy controller and should be removed
 * from a production deployment.
 * 
 * @author vladimir.vivien
 */
public class EchoController implements Controller{

    public void handle(Context ctx) {
        IOConsole console = ctx.getIoConsole();
        String inputLine = (String) ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        console.writeOutput(inputLine + System.getProperty("line.separator"));
    }

    public void plug(Context plug) {
        // do nothing when the component is plugged in.
    }
    
}
