package cli.clamshell.api;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * This component represents a console object used for input/output interactivity.
 * @author vladimir.vivien
 */
public interface IOConsole extends Plugin{
    /**
     * Getter for the console's internal InputStream instance.  Most implementation
     * will return System.in.
     * @return InputStream
     */
    public InputStream getInputStream();
    
    /**
     * Getter for the console's internal OutputStream instance.  Most implementation
     * will return System.out.
     * @return OutputStream
     */
    public OutputStream getOutputStream();
    
    /**
     * Prompts user to provide an input from the console.
     * @param prompt the prompt value displayed 
     * @return  the value read from console's input
     */
    public String readInput(String prompt);
   
    /**
     * Writes a string value to the console's output stream.
     * @param value value to be written
     */
    public void writeOutput(String value);
}
