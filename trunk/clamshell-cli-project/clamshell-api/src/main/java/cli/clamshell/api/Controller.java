package cli.clamshell.api;

/**
 * The role of the Controller component is to take a input from the command line
 * and decode it accordingly.  Simple implementations may do everything, however,
 * more sophisticated implementations may delegate workload to Command objects.
 * @author vladimir.vivien
 */
public interface Controller extends Plugin{
    /**
     * This is invoked when there is an input from the console to be interpreted.
     * The input value is passed in the context as Context.KEY_COMMAND_LINE_INPUT
     * @param ctx instance of Context
     */
    public void handle(Context ctx);
}
