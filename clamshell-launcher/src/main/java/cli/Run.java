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
import org.clamshellcli.api.Context;
import org.clamshellcli.api.Shell;
import org.clamshellcli.core.Clamshell;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;
import org.clamshellcli.api.Plugin;

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
        Context context  = null;
        File libDir = Clamshell.Runtime.getLibDir();
        File pluginsDir = Clamshell.Runtime.getPluginsDir();
        
        // register shutdown hook
        
        
        // Create/get Context, if something goes wrong, exit.
        try{
            context = Clamshell.Runtime.getContext();
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
        
        // load classes from lib directory
        ClassLoader libDirCl = null;
        try{
            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            libDirCl = Clamshell.ClassManager.getClassLoaderFromFiles(
                new File[]{libDir}, 
                Configurator.JARFILE_PATTERN,
                parent
            );
            Thread.currentThread().setContextClassLoader(libDirCl);
        }catch(Exception ex){
            System.out.printf("%nUnable to load classes from lib directory %s:%n%s.", libDir, ex.getMessage());
        }finally{
            if (libDirCl == null){
                System.out.printf("%nUnable to load classes from lib directory %s:%n.", libDir);
            }
        }
        
        // load Plugins classloader
        ClassLoader pluginsCl = null;
        try{
            pluginsCl = Clamshell.ClassManager.getClassLoaderFromFiles(
                new File[]{pluginsDir}, 
                Configurator.JARFILE_PATTERN,
                Thread.currentThread().getContextClassLoader()
            );
            context.putValue(Context.KEY_CLASS_LOADER, pluginsCl);
        }catch(Exception ex){
            System.out.printf("%nUnable to load Plugin classes %s:%n%s.", pluginsDir, ex.getMessage());
        }finally{
            if(pluginsCl == null){
                System.out.printf("%nUnable to load Plugin classes in directory %s:%n.", libDir);
            }
        }
        
        // load plugins
        List<Plugin> plugins =  Clamshell.Runtime.loadServicePlugins(Plugin.class, pluginsCl);
        if(plugins.isEmpty()){
            System.out.printf ("%nNo Plugin classes found in plugins directory, exiting...%n");
            System.exit(1);
        }
        context.putValue(Context.KEY_PLUGINS, plugins);
        List<Shell> shells = context.getPluginsByType(Shell.class);
                
        // Look for default Shell to launch.
        if(shells.size() > 0){
            Shell shell = shells.get(0);
            try{
                context.putValue(Context.KEY_SHELL_COMPONENT, shell);
                shell.plug(context);
            }catch(Exception ex){
                System.out.println("Something went wrong while bootstrapping the Shell:");
                ex.printStackTrace(System.err);
                System.exit(1);
            }
            
            // before launch, register shutdown handler
            Runtime.getRuntime().addShutdownHook(new ShutdownHook(context));

            
        }else{
            System.out.printf (
                "%nNo Shell component found in plugins directory." +
                "%nA Shell is required to continue bootstrapping sequence." +
                "%nExiting now."
            );
            System.exit(1);
        }
    }
    
    //TODO careful, Cotext is not thread-safe.
    private static class ShutdownHook  extends Thread {
        private final Context context;
        public ShutdownHook(final Context ctx){
            context = ctx;
        }
        
        @Override
        public void run() {
            System.out.println ("Shutting down...");
            Shell s = context.getShell();
            if(s != null){
                s.unplug(context);
            }
        }
    }
}
