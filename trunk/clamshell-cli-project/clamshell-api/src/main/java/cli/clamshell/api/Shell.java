package cli.clamshell.api;

/**
 * This is the core component.  Its job is to instantiate all other components.
 * It functions as a kernel for all other components loaded in the system.
 * @author vladimir.vivien
 */
public interface Shell extends Plugin{
    /**
     * Called when shell is running is pass-through mode.
     * In pass-through, the shell will not start a console.
     * Rather will attempt to interpret to command-line using its
     * internal Interpreter object.
     * @param ctx 
     */
    public void exec(Context ctx);
}
