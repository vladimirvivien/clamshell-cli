package cli.clamshell.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This component represents a console object used for input/output interactivity.
 * @author vladimir.vivien
 */
public interface IOConsole extends Plugin{
    public InputStream getInputStream();
    public OutputStream getOutputStream();
    public String readInput(String prompt);
    public void writeOutput(String value);
}
