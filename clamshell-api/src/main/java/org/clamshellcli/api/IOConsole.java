/*
 * #%L
 * clamshell-api
 * %%
 * Copyright (C) 2011 ClamShell-Cli
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.clamshellcli.api;

import java.io.File;
import java.io.PrintWriter;

/**
 * This component represents a console object used for input/output interactivity.
 * @author vladimir.vivien
 */
public interface IOConsole extends Plugin{
    
    /**
     * Getter for direct access to Console's Writer object.
     * @return OutputStream
     */
    public PrintWriter getWriter();

    /**
     * Reads an line of character from input.
     * @return 
     */
    public String readLine();
    
    /**
     * Reads line of characters after prompt.
     * @param prompt
     * @return 
     */
    public String readLine(String prompt);
    
    /**
     * Read line input with masked chars
     * @param maskChar
     * @return 
     */
    public String readLine(char maskChar);
    
    /**
     * Reads input after prompt using specified masked character.
     * @param prompt
     * @param maskChar
     * @return 
     */
    public String readLine(String prompt, char maskChar);
    
    /**
     * Shortcut to PrintWriter.print()
     * @param s 
     */
    public void print(String s);
    
    /**
     * Shortcut to PrintWriter.printf()
     * @param format
     * @param args 
     */
    public void printf(String format, Object...args);
    
   /**
    * Shortcut to PrintWriter.println()
    */
    public void println();

    /**
     * Shortcut to PrintWriter.println().
     * @param s 
     */
    public void println(String s);
    
    /**
     * Returns history flag value
     * @return 
     */
    public boolean isHistoryEnabled();   
    
    /**
     * Flushes the history content to storage.
     */
    public void saveHistory();
    
    /**
     * Adds the string to history.
     * @param s 
     */
    public void addToHistory(String s);
    
    /**
     * Gets the history file/location to use
     */
    public File getHistoryFile();
    
    /**
     * Clears the history.  
     */
    public void clearHistory();
    
    /**
     * Clears the screen content.
     */
    public void clearScreen();
    
    /**
     * Closes console upon shutdown.
     */
    public void close();
}
