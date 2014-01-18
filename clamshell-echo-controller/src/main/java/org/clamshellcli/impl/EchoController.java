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
import org.clamshellcli.core.AnInputController;

/**
 * A simple implementation of a InputController that echos back input send to the 
 * command-line.  This is meant to be demo toy controller and should be removed
 * from a production deployment.
 * 
 * @author vladimir.vivien
 */
public class EchoController extends AnInputController{
    @Override
    public boolean handle(Context ctx) {
        boolean handled = false;
        IOConsole console = ctx.getIoConsole();
        String inputLine = (String) ctx.getValue(Context.KEY_COMMAND_LINE_INPUT);
        if(inputLine != null && !inputLine.isEmpty()){
            console.print(inputLine + Configurator.VALUE_LINE_SEP);
            handled = true;
        }
        return handled;
    }

    @Override
    public void plug(Context plug) {
    }

    @Override
    public void unplug(Context plug) {
    }
    
}
