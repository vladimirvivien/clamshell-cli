package cli.clamshell.api;

/**
 * This is the core component.  Its job is to instantiate all other components.
 * It functions as a kernel for all other components loaded in the system.
 * @author vladimir.vivien
 */
public interface Shell extends Plugin{
    /**
     * Called when shell is running is pass-through mode.
     * In pass-through, the launcher will call this method only and not start
     * interactive mode through the normal startup sequence.
     * The command-line values will be passed in the context via key
     * Context.KEY_COMMAND_LINE_INPUT
     * @param ctx 
     */
    public void exec(Context ctx);
}
