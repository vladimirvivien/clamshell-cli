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

import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.IOConsole;
import org.clamshellcli.api.InputController;
import org.clamshellcli.api.Prompt;
import org.clamshellcli.api.Shell;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import jline.Terminal;
import jline.TerminalFactory;
import jline.console.ConsoleReader;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.AnsiRenderWriter;
import org.fusesource.jansi.AnsiRenderer;

/**
 * Default implementation of the IOConsole component.
 * It is responsible for providing input/output interactivity.
 * @author vladimir.vivien
 */
public class CliConsole implements IOConsole{
    private Context context;
    private Configurator config;
    private Shell shell;
    private Prompt prompt;
    private ConsoleReader console;
    private List<InputController> controllers;
    private boolean controllersAreValid;
    
    private InputStream input;
    private OutputStream output;
    private PrintWriter out;
    
    private Thread consoleThread;
    private Map<String, String[]> inputHints;
    private final char defaultMask = '*';
    
    private final static Terminal TERM;
    private final static Ansi ANSI;
    
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
    public InputStream getInputStream() {
        return input;
    }

    @Override
    public OutputStream getOutputStream() {
        return output;
    }

    @Override
    public void plug(Context plug) {
        context = plug;
        config = plug.getConfigurator();
        shell = plug.getShell();
        prompt = plug.getPrompt();
        
        input = System.in;
        output = System.out;
        
        inputHints = new HashMap<String, String[]>();
        try {
            console = new ConsoleReader(input, output);
            out = new AnsiRenderWriter(console.getOutput(), true);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize the console. "
                    + " Clamshell-Cli will stop now.", ex);
        }
    }
    
    //TODO - promote to Interface
    public void writeNewLine() {
        try {
            console.println();
        } catch (IOException ex) {
            throw new RuntimeException("Unable to invoke print on console: " ,ex);
        }        
    }
    
    //TODO - promote to Interface
    public void writeOutput(String text, Object...args){
        try {
            console.print(String.format(text, args));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to invoke print on console: " ,ex);
        }        
    }
    
    @Override
    public void writeOutput(String val) {
        try {
            console.print(val);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to invoke print on console: " ,ex);
        }
    }
    
    @Override
    public void writeOutputWithANSI(String text, Object...args){
        out.printf(text, args);
    }
    
    @Override
    public void writeOutputWithANSI(String text){
        out.print(text);
    }
    
    
    @Override
    public String readInput(String prompt) {
        String result = null;
        try {
            result = console.readLine(prompt);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read input: ", ex);
        }
        return result;
    }    

    @Override
    public String readSecretInput(String prompt) {
        return readSecretInput(prompt, defaultMask);
    }

    @Override
    public String readSecretInput(String prompt, char maskChar) {
        String result = null;
        try {
            result = console.readLine(prompt, maskChar);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to read input: ", ex);
        }
        return result;
    }
}