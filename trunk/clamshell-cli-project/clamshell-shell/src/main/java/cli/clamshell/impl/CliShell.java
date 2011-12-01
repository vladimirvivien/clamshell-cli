/*
 * #%L
 * clamshell-shell
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
import cli.clamshell.api.InputController;
import cli.clamshell.api.IOConsole;
import cli.clamshell.api.Shell;
import cli.clamshell.api.SplashScreen;
import java.io.File;
import java.util.List;

/**
 * This implementation of the Shell component is for a simple command-line system.
 * In this implementation, the shell loads the IOConsole for setup.
 * It then delegates loading of additional components (InputControllers, Commands)
 * to the IOConsole instance found in the classpath.
 * @author vladimir.vivien
 */
public class CliShell implements Shell{

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
    public void plug(Context plug) {
        IOConsole console = plug.getIoConsole();
        if(console == null){
            throw new RuntimeException(
                String.format("%nUnable to find required IOConsole component in"
                + " plugins directory [%s]."
                + "Exiting...%n", 
                ((File)plug.getValue(Configurator.KEY_CONFIG_PLUGINSDIR)).getName())
            );
        }
        
                
        //launch console
        console.plug(plug);        
    }
    
}
