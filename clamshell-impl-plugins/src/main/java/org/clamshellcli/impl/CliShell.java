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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import org.clamshellcli.api.CliException;
import org.clamshellcli.api.Configurator;
import static org.clamshellcli.api.Context.*;
import org.clamshellcli.api.InputController;
import org.clamshellcli.api.Prompt;
import org.clamshellcli.api.SplashScreen;

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
    private AtomicBoolean loopRunning;
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
        loopRunning = new AtomicBoolean(true);
        startConsoleThread();
    }
    
    @Override
    public void unplug(Context plug ){
        loopRunning.set(false);
        unloadComponent(plug);
    }
    
    /**
     * Load components.  
     * Create default where possible if none found on classpath.
     * @param plug 
     */
    private void loadComponents(Context plug) {
        context = plug;
        
        // Load IOConsole Component
        context.putValue(Context.KEY_INPUT_STREAM, System.in);
        context.putValue(Context.KEY_OUTPUT_STREAM, System.out);
        context.putValue(Context.KEY_ERROR_STREAM, System.err);
        
        List<IOConsole> consoles = context.getPluginsByType(IOConsole.class);
        console = (consoles.size() > 0) ? consoles.get(0) : new CliConsole();
        try{
            console.plug(plug);
            context.putValue(KEY_CONSOLE_COMPONENT, console);
        }catch(Exception ex){
            // attempt to fail fast if console is broken.
            throw new CliException (ex);
        }
        
        // Load prompt component
        List<Prompt> prompts = context.getPluginsByType(Prompt.class);
        prompt = (prompts.size() > 0) ? prompts.get(0) : new DefaultPrompt();
        try{
            prompt.plug(plug);            
        }catch(Exception ex){
            console.printf ("WARNING: Unable to load specied prompt instance.%n"
                    + "Will use a generic instance: %s%n ", ex.getMessage());
            prompt = new Prompt () {
                @Override
                public String getValue(Context ctx) {
                    return "cli>";
                }

                @Override
                public void plug(Context plug) {
                }

                @Override
                public void unplug(Context plug) {
                }
            };
        }finally{
            context.putValue(KEY_PROMPT_COMPONENT, prompt); // save for later use.
        }
                
        // activate/show splash screens
        List<SplashScreen> screens = context.getPluginsByType(SplashScreen.class);
        if(screens != null && screens.size() > 0){
            context.putValue(KEY_SPLASH_SCREENS, screens);
            
            for(SplashScreen sc : screens){
                try{
                    sc.plug(plug);
                    sc.render(plug);
                }catch (Exception ex){
                    console.printf("WARNING: unable to load/render SplashScreen instance %s%n%s%n", 
                            sc.getClass(), ex.getMessage());
                }
            }
        }

        // activate controllers
        controllers = context.getPluginsByType(InputController.class);
        if(controllers.size() > 0){
            context.putValue(KEY_CONTROLLERS, controllers);
            for (InputController ctrl : controllers){
                try{
                    configureController(ctrl);
                    ctrl.plug(plug);
                }catch (Exception ex){
                    console.printf("WARNING: unable to load/configure controller"
                            + " %s [it will be disabled]%n%s%n", 
                            ctrl.getClass(), ex.getMessage());
                    ctrl.setEnabled(false);
                }
            }
        }else{
            console.println("WARNING: No InputControllers found on classpath.");            
        }
    }
    
    private void unloadComponent(Context ctx){
        // unplug controllers
        for (InputController ctrl : ctx.getControllers()) {
            try {
                ctrl.unplug(ctx);
            } catch (Exception ex) {
                console.printf("WARNING: unable to unplug controller"
                        + " %s:%n%s%n",
                        ctrl.getClass(), ex.getMessage());
            }
        }
        
        
        // unplug splash screens
        for (SplashScreen screen : ctx.getSplashScreens()){
            try{
                screen.unplug(ctx);
            }catch(Exception ex){
                    console.printf("WARNING: unable to unplug SplashScreen instance %s%n%s%n", 
                            screen.getClass(), ex.getMessage());                
            }
        }
        
        // unplug the prompt
        try{
            ctx.getPrompt().unplug(ctx);
        }catch(Exception ex){
            console.printf("WARNING: unable to unplug Prompt instance %s%n%s%n", 
              ctx.getPrompt().getClass(), ex.getMessage());
        }
        
        // unplug the console
        try{
            ctx.getIoConsole().unplug(ctx);
        }catch(Exception ex){
            System.out.println ("WARNING: unable to properly unplug the Console instance.");
        }
    }
    
    private void startConsoleThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (loopRunning.get()) {
                    // reset command line arguments from previous command
                    context.putValue(Context.KEY_COMMAND_LINE_ARGS, null);

                    boolean handled = false;
                    String line = console.readLine(prompt.getValue(context));

                    if (line == null || line.trim().isEmpty()) {
                        continue;
                    }

                    context.putValue(Context.KEY_COMMAND_LINE_INPUT, line);
                    if (controllersExist()) {
                        for (InputController controller : controllers) {
                            Boolean enabled = controller.isEnabled();
                            // let controller handle input line if enabled
                            if(enabled){
                                try{
                                    boolean ctrlResult = controller.handle(context);
                                    handled = handled || ctrlResult;
                                }catch(Exception ex){
                                    console.printf("Unable to complete command:%n%s%n", ex.getMessage());
                                }
                            }
                        }
                        // was command line handled.
                        if (!handled) {
                            console.printf(
                                "%nCommand unhandled." +
                                "%nNo controllers found to handle [%s].%n", line
                            );
                        }
                    } else {
                        console.printf("%nWarning: no controllers(s) found.%n");
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
