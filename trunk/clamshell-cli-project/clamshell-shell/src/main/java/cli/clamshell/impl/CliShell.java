package cli.clamshell.impl;



import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.api.Controller;
import cli.clamshell.api.IOConsole;
import cli.clamshell.api.Shell;
import cli.clamshell.api.SplashScreen;
import java.util.List;

/**
 * This implementation of the Shell component is for a simple command-line system.
 * The shell loads its dependent components (clamshell plugins) and activates
 * them by calling plug().
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
            System.out.printf("%nUnable to find a Console component in plugins directory [%s]."
                    + " ClamShell-Cli requires a Console component. Exiting...%n", Configurator.VALUE_DIR_PLUGINS);
            System.exit(1);
        }
        
        // show splash on the default OutputStream
        List<SplashScreen> screens = plug.getPluginsByType(SplashScreen.class);
        if(screens != null && screens.size() > 0){
            for(SplashScreen sc : screens){
                sc.plug(plug);
                sc.render(plug);
            }
        }
        
        // plug in controllers
        List<Controller> controllers = plug.getPluginsByType(Controller.class);
        for (Controller ctrl : controllers){
            ctrl.plug(plug);
        }
        
        //launch console
        console.plug(plug);
    }
    
}
