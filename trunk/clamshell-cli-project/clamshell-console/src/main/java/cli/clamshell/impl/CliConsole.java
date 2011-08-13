package cli.clamshell.impl;

import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import cli.clamshell.api.Controller;
import cli.clamshell.api.Prompt;
import cli.clamshell.api.Shell;
import cli.clamshell.commons.ShellContext;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collections;
import java.util.List;
import jline.ConsoleReader;

/**
 * Default implementation of the IOConsole component.
 * It is respnosible for providing input/output interactivity.
 * @author vladimir.vivien
 */
public class CliConsole implements IOConsole{
    private Context context;
    private Shell shell;
    private Prompt prompt;
    private ConsoleReader console;
    private List<Controller> controllers;
    private boolean interpretersAreValid;
    private InputStream input;
    private OutputStream output;
    private Thread consoleThread;

    public InputStream getInputStream() {
        return input;
    }

    public OutputStream getOutputStream() {
        return output;
    }

    public void plug(Context plug) {
        context = plug;
        shell = plug.getShell();
        prompt = plug.getPrompt();
        controllers = plug.getPluginsByType(Controller.class);
        interpretersAreValid = hasInterpreters(controllers);
        input = (input = (InputStream)context.getValue(Context.KEY_INPUT_STREAM)) != null ? input : System.in;
        output = (output = (OutputStream)context.getValue(Context.KEY_OUTPUT_STREAM)) != null ? output : System.out;
        
        try {
            console = new ConsoleReader(input, new OutputStreamWriter(output));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize the console. "
                    + " Program will stop now.");
        }
        
        consoleThread = createConsoleThread();
        consoleThread.start();
    }
    
    
    @Override
    public void writeOutput(String val) {
        try {
            console.printString(val);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to invoke print on console: " ,ex);
        }
    }
    
    @Override
    public String readInput(String prompt) {
        String result = null;
        try {
            result = console.readLine(prompt);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read input: ", ex);
        }
        return result;
    }

    private Thread createConsoleThread() {
        Thread t = new Thread( new Runnable() {
            public void run() {
                while (!Thread.interrupted()){
                    //Context threadContext = cloneContext(context);
                    String line = readInput(prompt.getValue(context));
                    context.putValue(Context.KEY_COMMAND_LINE_INPUT, line);
                    if(interpretersAreValid){
                        for(Controller controller : controllers){
                            controller.handle(context);
                        }
                    }else{
                        writeOutput(String.format("Warning: no command controllers(s) found.%n"));
                    }
                }
            }
        });

        return t;
    }
    
    /**
     * Are there any controllers installed?
     * @param controllers 
     */
    private boolean hasInterpreters(List<Controller> interpreters){
        return (interpreters != null && interpreters.size() > 0) ? true : false;
    }
    
    private Context cloneContext (Context ctx){
        Context newContext = ShellContext.createInstance();
        newContext.putValues(ctx.getValues());
        return newContext;
    }
}
    