package cli.clamshell.api;

/**
 * The Command component is there to allow Controllers to delegate tasks.
 * Each command exposes a textual id.  This can be used to identify the action
 * (request) that will invoke the execute() method on that command.
 * @author vladimir.vivien
 */
public interface Command extends Plugin {
    /**
     * Implementation of this method should return a simple string that 
     * identifies the action mapped to this command.
     * @return the name of the action mapped to this command.
     */
    public String getAction();
    
    /**
     * This method will be called as the starting point to execute the logic
     * for the action mapped to this command.
     * @param ctx
     * @return 
     */
    public Object execute(Context ctx);
}
