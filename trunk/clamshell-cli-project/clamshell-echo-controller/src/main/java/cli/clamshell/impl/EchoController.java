/*
 * #%L
 * clamshell-echo-controller
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
package cli.clamshell.impl;

import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import cli.clamshell.api.Controller;
import cli.clamshell.api.IOConsole;

/**
 * A simple implementation of a Controller that echos back input send to the 
 * command-line.  This is meant to be demo toy controller and should be removed
 * from a production deployment.
 * 
 * @author vladimir.vivien
 */
public class EchoController implements Controller{

    public void handle(Context ctx) {
        IOConsole console = ctx.getIoConsole();
        String inputLine = (String) ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        if(!inputLine.isEmpty())
            console.writeOutput(inputLine + Configurator.VALUE_LINE_SEP);
    }

    public void plug(Context plug) {
        // do nothing when the component is plugged in.
    }
    
}
