/*
 * clamshell-launcher
 * Copyright (C) 2011 ClamShell-Cli
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
package cli;

import org.clamshellcli.api.Configurator;
import org.clamshellcli.core.ShellContext;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.Shell;
import org.clamshellcli.core.Clamshell;
import java.io.File;
import java.util.Map;

/**
 * This is the entry point of the entire clamshell cli container (Main).
 * This is a thin starter module that serves as a bootloader for the system.
 * <ul>
 * <li>It reads the config file</li>
 * <li>Uses internal bootloader to load and prepare all plugins.</li>
 * <li>Looks for a Shell component.  If none is found, abort</li>
 * <li>Hand off the continuation of the booting process to the Shell instance</li>
 * </ul>
 * 
 * <b>Argument Layout</b><br/>
 * 
 * 
 * @author vladimir.vivien
 */
public class Run {
    public static void main(String[] args){        
        // create/confiugre the context
        Context context = null;
        Configurator config = null;
        File libDir = Clamshell.Runtime.getLibDir();
        
        try{
            context = Clamshell.Runtime.getContext();
            config = Clamshell.Runtime.getConfigurator();
        }catch(RuntimeException ex){
            System.out.printf("%nUnable to start Clamshell:%n%s.%n", ex.getMessage());
            System.exit(1);
        }
                
        // only continue if plugins are found
        if(!libDir.exists()){
            System.out.printf("%nLib directory not found. ClamShell-Cli will exit.%n", 
                libDir.getAbsolutePath());
            System.exit(1);
        }
        
        // add libDir to classpath
        try{
            // modify the the thread's class loader
            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            ClassLoader cl = Clamshell.ClassManager.createClassLoaderForPath(
                new File[]{libDir}, 
                parent
            );
            Thread.currentThread().setContextClassLoader(cl);
        }catch(Exception ex){
            System.out.printf("%nUnable to create classloader for path %s:%n%s.", libDir, ex.getMessage());
        }
        
        context.putValue(Context.KEY_INPUT_STREAM, System.in);
        context.putValue(Context.KEY_OUTPUT_STREAM, System.out);
        
        // validate plugins.  Look for default Shell.
        if(context.getPlugins().size() > 0){
            Shell shell = context.getShell();
            if(context.getShell() != null){
                try{
                    shell.plug(context);
                }catch(Exception ex){
                    ex.printStackTrace();
                    System.out.printf("%nSomething went wrong:%n%s%n", ex.getMessage());
                }
            }else{
                System.out.printf (
                    "%nNo Shell component found in plugins directory." +
                    "%nA Shell instance must be on the classpath." +
                    "%nExiting now.");
                System.exit(1);
            }
        }else{
            System.out.printf (
                "%nNo plugins found in found in the plugins directory. " +
                "%nClamShell-Cli will exit now.");
            System.exit(1);
        }
    }
}
