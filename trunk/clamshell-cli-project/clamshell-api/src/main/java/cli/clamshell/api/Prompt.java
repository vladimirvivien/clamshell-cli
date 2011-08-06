package cli.clamshell.api;

/**
 * A Prompt is responsible for generating the prompt in that appears
 * in the console.  Every time prompt is displayed, the loaded prompt will be
 * displayed using the getPrompt() method.
 * @author vladimir.vivien
 */
public interface Prompt extends Plugin{
    public String getValue(Context ctx);
}
