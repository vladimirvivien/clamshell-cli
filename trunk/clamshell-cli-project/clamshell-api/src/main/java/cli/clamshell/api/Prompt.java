package cli.clamshell.api;

/**
 * A Prompt is responsible for generating the prompt in that appears
 * in the console.  Every time prompt is displayed, the loaded prompt will be
 * displayed using the getValue() method.
 * @author vladimir.vivien
 */
public interface Prompt extends Plugin{
    /**
     * Implementation of this method should return the current prompt value.
     * Keep in mind that this is called every time the console displays a 
     * prompt.  So, you may choose to provide a cached value for performance if
     * your prompt takes a while to calculate.
     * @param ctx Instance of Context
     * @return the value for the prompt
     */
    public String getValue(Context ctx);
}
