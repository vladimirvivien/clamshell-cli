/*
 * Copyright 2012 ClamShell-Cli.
 *
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
 */
package org.clamshellcli.impl;

import org.clamshellcli.api.Context;
import org.clamshellcli.api.IOConsole;
import org.clamshellcli.api.Shell;
import java.util.List;
import java.util.regex.Pattern;
import org.clamshellcli.api.InputController;
import org.clamshellcli.api.Prompt;
import org.clamshellcli.api.SplashScreen;
import org.clamshellcli.core.Clamshell;

/**
 * This implementation of the Shell component loads all other components in the system.
 * In this implementation, the Shell loads
 * <ul>
 * <li> IOConsole </li>
 * <li> Prompt </li>
 * <li> Controllers <li>
 * </lu>the IOConsole for setup.
 * This component also handles non-interactive mode where it executes command
 * passed in as arguments and exits upon completion.
 * @author vladimir.vivien
 */
public class CliShell implements Shell{
    private Context context;
    private IOConsole console;
    private Prompt prompt;
    private List<InputController> controllers;

    /** 
     * This method will be called when the shell is invoked to handle commands
     * from the OS passed in as arguments.  This is used to allow the shell to 
     * work in silence (pass-through non-interactive) mode.
     * @param ctx instance of Context
     */
    public void exec(Context ctx) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Implements initialization logic when the shell is launched.
     * It works by loading the following components from the classpath
     * in the following order:
     * <ul>
     * <li> Plugin in a SplashScreen component.
     * <li> looks for a Console plugin and plugs it in
     * <li> Plug in the first instance of Controllers
     *
     *
     * @param plug instance of Context
     */
    @Override
    public void plug(Context plug) {
        loadComponents(plug);
        startConsoleThread();
    }
    
    private void loadComponents(Context plug) {
        context = plug;
        
        prompt = plug.getPrompt();
        prompt.plug(plug);
        
        // setup Console, if none found, create default.
        console = plug.getIoConsole();
        if(console == null){            
            throw new RuntimeException(
                String.format("%nUnable to find required IOConsole component in"
                + " plugins directory [%s]."
                + "Exiting...%n", Clamshell.Runtime.getPluginsDir())
            );
        }
        console.plug(plug);
        

        // activate controllers
        controllers = plug.getPluginsByType(InputController.class); 
        if(controllers.size() > 0){
            for (InputController ctrl : controllers){
                ctrl.plug(plug);
            }
        }else{
            console.writeOutput("%nWARNING: No InputControllers found on classpath.");            
        }
        
        // activate/show splash screens
        List<SplashScreen> screens = plug.getPluginsByType(SplashScreen.class);
        if(screens != null && screens.size() > 0){
            for(SplashScreen sc : screens){
                sc.plug(plug);
                sc.render(plug);
            }
        }
    }
    
    private void startConsoleThread() {
        new Thread(new Runnable() {
            public void run() {
                while (!Thread.interrupted()) {
                    // reset command line arguments from previous command
                    context.putValue(Context.KEY_COMMAND_LINE_ARGS, null);

                    boolean handled = false;
                    String line = console.readInput(prompt.getValue(context));

                    if (line == null || line.trim().isEmpty()) {
                        continue;
                    }

                    context.putValue(Context.KEY_COMMAND_LINE_INPUT, line);
                    if (controllersExist()) {
                        for (InputController controller : controllers) {
                            Boolean enabled = controller.isEnabled();
                            // let controller handle input line if enabled
                            if(enabled){
                                boolean ctrlResult = controller.handle(context);
                                handled = handled || ctrlResult;
                            }
                        }
                        // was command line handled.
                        if (!handled) {
                            console.writeOutput(String.format(
                                "%nCommand unhandled." +
                                "%nNo controllers found to handle [%s].%n", line));
                        }
                    } else {
                        console.writeOutput(String.format("%nWarning: no controllers(s) found.%n"));
                    }
                }
            }
        }).start();
    }

    /**
     * Are there any controllers installed?
     *
     * @param controllers
     */
    private boolean controllersExist() {
        return (controllers != null && controllers.size() > 0) ? true : false;
    }

}
