/*
 * Copyright 2012 ClamShell-Cli.
 *
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
 */
package org.clamshellcli.impl;

import java.io.File;
import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.IOConsole;
import org.clamshellcli.api.InputController;
import org.clamshellcli.api.Prompt;
import org.clamshellcli.api.Shell;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderWriter;

/**
 * Default implementation of the IOConsole component.
 * It is responsible for providing input/output interactivity.
 * @author vladimir.vivien
 */
public class CliConsole implements IOConsole{
    private boolean plugged;
    private Context context;
    private Configurator config;
    private Shell shell;
    private Prompt prompt;
    private ConsoleReader console;
    private List<InputController> controllers;
    private boolean controllersAreValid;
    
    private PrintWriter out;
    
    private Thread consoleThread;
    private Map<String, String[]> inputHints;
    private final char defaultMask = '*';
    
    private final static Terminal TERM;
    private final static Ansi ANSI;
    private final static File CLI_USERDIR = new File(Configurator.VALUE_USERHOME,".cli");
    private File histFile = new File (CLI_USERDIR, "history.log");
    private FileHistory history;
    
    static {
        TERM = TerminalFactory.create();
        AnsiConsole.systemInstall();
        Ansi.setEnabled(true);
        
        Ansi.setDetector(new Callable(){
            @Override
            public Boolean call() throws Exception {
                return TERM.isAnsiSupported();
            }
        });
        
        ANSI = Ansi.ansi();        
    }

    @Override
    public void plug(Context plug) {
        context = plug;
        config = plug.getConfigurator();
        shell = plug.getShell();
        prompt = plug.getPrompt();
        
        inputHints = new HashMap<String, String[]>();
        
        try {
            // setup ANSI writer
            console = new ConsoleReader(System.in, System.out);
            out = new AnsiRenderWriter(console.getOutput(), true);
            
            // ensure clamshell user dir exists
            if(!CLI_USERDIR.exists()){
                CLI_USERDIR.mkdirs();
            }
            // setup history
            history = new FileHistory(histFile);
            history.moveToEnd();
            console.setHistoryEnabled(true);
            console.setHistory(history);
            
            
            // console plugged
            plugged = true;
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize the console. "
                    + " Clamshell-Cli will stop now.", ex);
        }
    }
    
    @Override
    public void unplug(Context plug){
        try {
            plugged = false;
            history.flush();
            getWriter().flush();
            getWriter().close();
            console.flush();
            console.getInput().close();
            console.shutdown();
        } catch (IOException ex) {
            throw new RuntimeException ("Unable to unplug CliConsole.", ex);
        }
    }
    
    @Override
    public boolean isHistoryEnabled() {
        return (console != null) ? console.isHistoryEnabled() : false;
    }
    public void setHistoryEnabled(boolean hist){
        console.setHistoryEnabled(hist);
    }

    @Override
    public File getHistoryFile() {
        return histFile;
    }
    
    public void setHistoryFile(File f){
        histFile = f;
        try{
            history  = new FileHistory(histFile);
            console.setHistory(history);
        }catch(IOException ex){
            throw new RuntimeException ("Unable to set history file.", ex);
        }
    }
    
    @Override
    public void saveHistory() {
        try {
            history.flush();
        } catch (IOException ex) {
            throw new RuntimeException ("Unable to save history.", ex);
        }
    }
    
    @Override
    public void addToHistory(String s) {
        history.add(s);
    }
    
    @Override
    public void clearHistory() {
        try {
            history.purge();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to delete history file.", ex);
        }
    }
    
    @Override
    public PrintWriter getWriter() {
        return out;
    }

    public ConsoleReader getReader() {
        return console;
    }

    @Override
    public String readLine() {
        try {
            return console.readLine();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read input: ", ex);
        }
    }

    @Override
    public String readLine(String prompt) {
        try {
            return console.readLine(prompt);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read input: ", ex);
        }        
    }

    @Override
    public String readLine(char maskChar) {
        try {
            return console.readLine(maskChar);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read input: ", ex);
        }
    }

    @Override
    public String readLine(String prompt, char maskChar) {
        try {
            return console.readLine(prompt, maskChar);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read input: ", ex);
        }
    }

    @Override
    public void print(String s) {
        out.print(s);
    }

    @Override
    public void printf(String format, Object... args) {
        out.printf(format, args);
    }

    @Override
    public void println() {
        out.println();
    }

    @Override
    public void println(String s) {
        out.println(s);
    }

    @Override
    public void clearScreen() {
        try{
            if(!console.clearScreen()){
                out.println ("Clearscreen command is not supported on terminal.");
            }
        }catch(IOException ex){
            throw new RuntimeException("Unable to clear screen: ", ex);
        }
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private void assertComponentPlugged() {
        if (!plugged){
            throw new IllegalStateException("Component is unplugged.");
        }
    }
}