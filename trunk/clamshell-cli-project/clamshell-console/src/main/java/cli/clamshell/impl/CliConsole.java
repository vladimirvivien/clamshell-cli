/*
 * #%L
 * clamshell-console
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

import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.api.IOConsole;
import cli.clamshell.api.InputController;
import cli.clamshell.api.Prompt;
import cli.clamshell.api.Shell;
import cli.clamshell.api.SplashScreen;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import jline.CandidateListCompletionHandler;
import jline.ConsoleReader;
import jline.SimpleCompletor;

/**
 * Default implementation of the IOConsole component.
 * It is respnosible for providing input/output interactivity.
 * @author vladimir.vivien
 */
public class CliConsole implements IOConsole{
    private Context context;
    private Configurator config;
    private Shell shell;
    private Prompt prompt;
    private ConsoleReader console;
    private List<InputController> controllers;
    private boolean controllersAreValid;
    private InputStream input;
    private OutputStream output;
    private Thread consoleThread;
    private Map<String, String[]> inputHints;

    public InputStream getInputStream() {
        return input;
    }

    public OutputStream getOutputStream() {
        return output;
    }

    public void plug(Context plug) {
        context = plug;
        config = plug.getConfigurator();
        shell = plug.getShell();
        prompt = plug.getPrompt();
        input = (input = (InputStream)context.getValue(Context.KEY_INPUT_STREAM)) != null ? input : System.in;
        output = (output = (OutputStream)context.getValue(Context.KEY_OUTPUT_STREAM)) != null ? output : System.out;
        inputHints = new HashMap<String, String[]>();
        
        try {
            console = new ConsoleReader(input, new OutputStreamWriter(output));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize the console. "
                    + " Program will stop now.", ex);
        }
        
        // plug in installed input controllers
        controllers = plug.getPluginsByType(InputController.class);
        if(controllers.size() > 0){
            for (InputController ctrl : controllers){
                ctrl.plug(plug);
            }
            aggregateExpectedInputs();        
            console.setCompletionHandler(new CandidateListCompletionHandler());
        }else{
            throw new RuntimeException("Unable to initialize Clamshell-Cli. "
                + " No InputController instances found on classpath. Exiting...");            
        }
                
        // show splash on the default OutputStream
        List<SplashScreen> screens = plug.getPluginsByType(SplashScreen.class);
        if(screens != null && screens.size() > 0){
            for(SplashScreen sc : screens){
                sc.plug(plug);
                sc.render(plug);
            }
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
                    boolean handled = false;
                    String line = readInput(prompt.getValue(context));
                    context.putValue(Context.KEY_COMMAND_LINE_INPUT, line);
                    if(controllersExist()){
                        for(InputController controller : controllers){
                            Pattern pattern = controller.respondsTo();
                            Boolean enabled = controller.isEnabled();
                            
                            // Apply controller only if provided pattern matches.
                            if(pattern != null && pattern.matcher(line).matches() && enabled){
                                boolean ctrlResult = controller.handle(context);
                                handled = handled || ctrlResult;
                            }

                        }
                        // was command line handled.
                        if(!handled){
                            writeOutput(String.format("%nCommand unhandled. "
                                + "%nNo controller found to respond to [%s].%n%n",line)); 
                        }
                    }else{
                        writeOutput(String.format("Warning: no controllers(s) found.%n"));
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
    private boolean controllersExist(){
        return (controllers != null && controllers.size() > 0) ? true : false;
    }
    
    /**
     * Collection expected input values to build suggestion lists.
     */
    private void aggregateExpectedInputs(){
        for(InputController ctrl : controllers){
            String[] expectedInputs = ctrl.getExpectedInputs();
            if(expectedInputs != null){
                console.addCompletor(new SimpleCompletor(expectedInputs));
            }
        }
    }

}
