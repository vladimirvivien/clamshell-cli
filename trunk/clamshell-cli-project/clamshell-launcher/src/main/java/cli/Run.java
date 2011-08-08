package cli;

import cli.clamshell.api.Configurator;
import cli.clamshell.commons.ShellContext;
import cli.clamshell.api.Context;
import cli.clamshell.api.Shell;
import java.io.File;

/**
 * This is the entry point of the entire clamshell cli container (Main).
 * This is a thin starter module that serves as a bootloader for the system.
 * <ul>
 * <li> ensure that the container's folder follows the expected layout convention</li>
 * <li>Load and prepare all plugins.</li>
 * <li>Look for a Shell component.  If none is found, abort</li>
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
        System.out.println ("Starting Clamshell-Cli.");
        
        File pluginsDir = new File(Configurator.VALUE_DIR_PLUGINS);
        if(!pluginsDir.exists()){
            System.out.printf("Pugins directory [%s] not found. Clamshell-Cli will stop.%n", pluginsDir.getCanonicalPath());
            System.exit(1);
        }
        
        // load context
        Context context = ShellContext.createInstance();
        // only continue if plugins are found
        if(context.getPlugins().size() > 0){
            System.out.printf("Found %d plugins in location [%s].%n", context.getPlugins().size(), Configurator.VALUE_DIR_PLUGINS);
            Shell shell = context.getShell();
            if(context.getShell() != null){
                shell.plug(context);
            }else{
                System.out.printf ("No Shell component found in plugins directory [%s]."
                        + " Clamshell-Cli will quit now.%n", Configurator.VALUE_DIR_PLUGINS);
                System.exit(1);
            }
        }else{
            System.out.printf ("No plugins found in [%s]. Clamshell-Cli will quit now.%n", Configurator.VALUE_DIR_PLUGINS);
            System.exit(1);
        }
    }
}
