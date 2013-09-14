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
import java.util.Map;
import java.util.regex.Pattern;
import org.clamshellcli.api.Configurator;
import static org.clamshellcli.api.Context.*;
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
        context = plug;
        loadComponents(plug);
        startConsoleThread();
    }
    
    /**
     * Load components.  
     * Create default where possible if none found on classpath.
     * @param plug 
     */
    private void loadComponents(Context plug) {
        context = plug;
        // Load prompt component
        List<Prompt> prompts = context.getPluginsByType(Prompt.class);
        prompt = (prompts.size() > 0) ? prompts.get(0) : new DefaultPrompt();
        prompt.plug(plug);
        context.putValue(KEY_PROMPT_COMPONENT, prompt); // save for later use.     
        
        // Load IOConsole Component
        context.putValue(Context.KEY_INPUT_STREAM, System.in);
        context.putValue(Context.KEY_OUTPUT_STREAM, System.out);
        List<IOConsole> consoles = context.getPluginsByType(IOConsole.class);
        console = (consoles.size() > 0) ? consoles.get(0) : new CliConsole();
        console.plug(plug);
        context.putValue(KEY_CONSOLE_COMPONENT, console);
        
        // activate/show splash screens
        List<SplashScreen> screens = context.getPluginsByType(SplashScreen.class);
        if(screens != null && screens.size() > 0){
            context.putValue(KEY_SPLASH_SCREENS, screens);
            for(SplashScreen sc : screens){
                sc.plug(plug);
                sc.render(plug);
            }
        }

        // activate controllers
        controllers = context.getPluginsByType(InputController.class);
        if(controllers.size() > 0){
            context.putValue(KEY_CONTROLLERS, plug);
            for (InputController ctrl : controllers){
                configureController(ctrl);
                ctrl.plug(plug);
            }
        }else{
            console.writeOutput("%nWARNING: No InputControllers found on classpath.");            
        }
    }
    
    private void startConsoleThread() {
        new Thread(new Runnable() {
            @Override
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
    
    private void configureController (InputController controller){
        String ctrlClassName = this.getClass().getName();
        Configurator config = context.getConfigurator();
        
        Map<String,Map<String,? extends Object>> ctrlsMap = 
                (Map<String,Map<String,? extends Object>>) config.getControllersMap();
        
        if(ctrlsMap != null){
            Map<String, Object> map = (Map<String, Object>) ctrlsMap.get(ctrlClassName);
            if(map != null){
                String inputPattern = (String) map.get("inputPattern");
                Pattern pattern = (inputPattern != null) ? 
                        Pattern.compile(inputPattern) : 
                        Pattern.compile(".*");
                controller.setInputPattern(pattern);
                String flag = (String)map.get("enabled");
                Boolean enabled = Boolean.valueOf((flag != null) ? flag : "true");
                controller.setEnabled(enabled);
            }
        }        
    }
}
