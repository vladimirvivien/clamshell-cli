package cli.clamshell.api;

/**
 * The SplashScreen plugin lets you build a textual screen that is displayed
 * at the start of the shell.
 * 
 * @author Vladimir.Vivien
 */
public interface SplashScreen extends Plugin {
    /**
     * This method is called when the Shell is ready to display the SplashScreen.
     * @param ctx 
     */
    public void render(Context ctx);
}
