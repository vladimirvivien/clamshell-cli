/*
 * Copyright 2011 ClamShell-Cli.
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
package org.clamshellcli.test;

import org.clamshellcli.api.Context;
import org.clamshellcli.api.IOConsole;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This mock implementation of the IOConsole is provided for testing.
 * @author vladimir
 */
public class MockConsole implements IOConsole{
    private InputStream in;
    private PrintStream out;
    
    public MockConsole(){
        in = System.in;
        out = System.out;
    }
    
    public InputStream getInputStream() {
        return in;
    }

    public OutputStream getOutputStream() {
        return out;
    }

    public String readInput(String prompt) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void writeOutput(String value) {
        out.print(value);
    }

    public void plug(Context plug) {
        
    }

    @Override
    public String readSecretInput(String prompt) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String readSecretInput(String prompt, char maskChar) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeOutputWithANSI(String text, Object... args) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void writeOutputWithANSI(String text) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
