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
    public static void main(String[] args) throws Exception{        
        // create/confiugre the context
        Context context = ShellContext.createInstance();
        Configurator config = context.getConfigurator();
        Map<String,String> propsMap = config.getPropertiesMap();
        String libDirName = propsMap.get(Configurator.KEY_CONFIG_LIBIDR);
        String pluginsDirName = propsMap.get(Configurator.KEY_CONFIG_PLUGINSDIR);
        
        // only continue if plugins are found
        File libDir = new File(libDirName);
        if(!libDir.exists()){
            System.out.printf("%nLib directory [%s] not found. ClamShell-Cli will exit.%n%n", libDir.getCanonicalPath());
            System.exit(1);
        }
        context.putValue(Configurator.KEY_CONFIG_LIBIDR, libDir);
        
        // modify the the thread's class loader
        ClassLoader parent = Thread.currentThread().getContextClassLoader();
        ClassLoader cl = Clamshell.ClassManager.createClassLoaderForPath(new File[]{libDir}, parent);
        Thread.currentThread().setContextClassLoader(cl);
        
        
        File pluginsDir = new File(pluginsDirName);
        if(!pluginsDir.exists()){
            System.out.printf("%nPugins directory [%s] not found. ClamShell-Cli will exit.%n%n", pluginsDir.getCanonicalPath());
            System.exit(1);
        }
        context.putValue(Configurator.KEY_CONFIG_PLUGINSDIR, pluginsDir);

        context.putValue(Context.KEY_INPUT_STREAM, System.in);
        context.putValue(Context.KEY_OUTPUT_STREAM, System.out);
        
        // validate plugins.  Look for default Shell.
        if(context.getPlugins().size() > 0){
            Shell shell = context.getShell();
            if(context.getShell() != null){
                shell.plug(context);
            }else{
                System.out.printf ("%nNo Shell component found in plugins directory [%s]."
                        + " ClamShell-Cli will exit now.%n", pluginsDir.getCanonicalPath());
                System.exit(1);
            }
        }else{
            System.out.printf ("%nNo plugins found in [%s]. ClamShell-Cli will exit now.%n%n", pluginsDir.getCanonicalPath());
            System.exit(1);
        }
    }
}
