/*
 * #%L
 * clamshell-splashscreen
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
package org.clamshellcli.impl;

import org.clamshellcli.api.Configurator;
import org.clamshellcli.api.Context;
import org.clamshellcli.api.SplashScreen;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * This is a SplashScreen plugin implementation to display a default ClamShell-Cli
 * splash screen.
 * @author vladimir.vivien
 */
public class CliSplashScreen implements SplashScreen {
    private static StringBuilder screen;
    public void render(Context ctx) {
        PrintStream out = new PrintStream ((OutputStream)ctx.getValue(Context.KEY_OUTPUT_STREAM));
        out.println(screen);
    }

    public void plug(Context plug) {
        screen  = new StringBuilder();
        screen
.append(Configurator.VALUE_LINE_SEP)    
.append(Configurator.VALUE_LINE_SEP)               
.append(" .d8888b.  888                         .d8888b.  888               888 888").append(Configurator.VALUE_LINE_SEP)
.append("d88P  Y88b 888                        d88P  Y88b 888               888 888").append(Configurator.VALUE_LINE_SEP) 
.append("888    888 888                        Y88b.      888               888 888").append(Configurator.VALUE_LINE_SEP) 
.append("888        888  8888b.  88888b.d88b.   :Y888b.   88888b.   .d88b.  888 888").append(Configurator.VALUE_LINE_SEP) 
.append("888        888     :88b 888 :888 :88b     :Y88b. 888 :88b d8P  Y8b 888 888").append(Configurator.VALUE_LINE_SEP) 
.append("888    888 888 .d888888 888  888  888       :888 888  888 88888888 888 888").append(Configurator.VALUE_LINE_SEP) 
.append("Y88b  d88P 888 888  888 888  888  888 Y88b  d88P 888  888 Y8b.     888 888").append(Configurator.VALUE_LINE_SEP) 
.append(" :Y8888P:  888 :Y888888 888  888  888  :Y8888P:  888  888  :Y8888  888 888").append(Configurator.VALUE_LINE_SEP)
.append(Configurator.VALUE_LINE_SEP)
.append("                                                  Command-Line Interpreter").append(Configurator.VALUE_LINE_SEP)
.append(Configurator.VALUE_LINE_SEP)
.append("Java version: ").append(System.getProperty("java.version")).append(Configurator.VALUE_LINE_SEP)
.append("Java Home: ").append(System.getProperty("java.home")).append(Configurator.VALUE_LINE_SEP)
.append("OS: ").append(System.getProperty("os.name")).append(", Version: ").append(System.getProperty("os.version"))
.append(Configurator.VALUE_LINE_SEP)
.append(Configurator.VALUE_LINE_SEP)
 ;
    }
    
}
