package cli.clamshell.impl;

import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.api.SplashScreen;
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
