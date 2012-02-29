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
package cli.clamshell.jmx;

import cli.clamshell.api.Context;
import cli.clamshell.api.SplashScreen;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 *
 * @author vvivien
 */ 
public class JmxSplashScreen implements SplashScreen{
    private static StringBuilder screen;
    static{
        screen = new StringBuilder();
        screen
            .append(String.format("%n%n"))
            .append("    /#####                                /######    /##   /##").append(String.format("%n"))
            .append("   |__  ##                               /##__  ##  | ##  |__/").append(String.format("%n"))
            .append("      | ##   /######/####    /##   /##  | ##  \\__/  | ##   /##").append(String.format("%n"))
            .append("      | ##  | ##_  ##_  ##  |  ## /##/  | ##        | ##  | ##").append(String.format("%n"))
            .append(" /##  | ##  | ## \\ ## \\ ##   \\  ####/   | ##        | ##  | ##").append(String.format("%n"))
            .append("| ##  | ##  | ## | ## | ##    >##  ##   | ##    ##  | ##  | ##").append(String.format("%n"))
            .append("|  ######/  | ## | ## | ##   /##/\\  ##  |  ######/  | ##  | ##").append(String.format("%n"))
            .append(" \\______/   |__/ |__/ |__/  |__/  \\__/   \\______/   |__/  |__/").append(String.format("%n%n"))
            .append("A command-line tool for JMX").append(String.format("%n"))
            .append("Powered by Clamshell-Cli framework ").append(String.format("%n"))
            .append("http://code.google.com/p/clamshell-cli/").append(String.format("%n%n"))
    
            .append("Java version: ").append(System.getProperty("java.version")).append(String.format("%n"))
            .append("OS: ").append(System.getProperty("os.name")).append(", Version: ").append(System.getProperty("os.version"))

            ;
    }
    
    public void render(Context ctx) {
        PrintStream out = new PrintStream ((OutputStream)ctx.getValue(Context.KEY_OUTPUT_STREAM));
        out.println(screen);
    }

    public void plug(Context plug) {
    }
    
}
