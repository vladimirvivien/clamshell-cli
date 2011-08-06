package cli;

import cli.clamshell.commons.ShellContext;
import cli.clamshell.api.Context;
import cli.clamshell.api.Shell;

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
    public static void main(String[] args){
        Context context = ShellContext.createInstance();
        Shell shell = context.getShell();
        shell.plug(context);
    }
}
