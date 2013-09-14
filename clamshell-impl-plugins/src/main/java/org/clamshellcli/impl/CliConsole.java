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
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jline.ConsoleReader;

/**
 * Default implementation of the IOConsole component.
 * It is respnosible for providing input/output interactivity.
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
    private Thread consoleThread;
    private Map<String, String[]> inputHints;

    public InputStream getInputStream() {
        return input;
    }

    public OutputStream getOutputStream() {
        return output;
    }

    public void plug(Context plug) {
        context = plug;
        config = plug.getConfigurator();
        shell = plug.getShell();
        prompt = plug.getPrompt();
        input = (input = (InputStream)context.getValue(Context.KEY_INPUT_STREAM)) != null ? input : System.in;
        output = (output = (OutputStream)context.getValue(Context.KEY_OUTPUT_STREAM)) != null ? output : System.out;
        inputHints = new HashMap<String, String[]>();

        try {
            console = new ConsoleReader(input, new OutputStreamWriter(output));
        } catch (IOException ex) {
            throw new RuntimeException("Unable to initialize the console. "
                    + " Program will stop now.", ex);
        }
    }
    
    
    @Override
    public void writeOutput(String val) {
        try {
            console.printString(val);
        } catch (IOException ex) {
            throw new RuntimeException("Unable to invoke print on console: " ,ex);
        }
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

}