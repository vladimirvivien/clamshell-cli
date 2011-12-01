/*
 * #%L
 * clamshell-commands
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

import cli.clamshell.api.Command;
import cli.clamshell.api.Configurator;
import cli.clamshell.api.Context;
import java.util.Collections;
import java.util.Map;

/**
 * This class implements the Command component.  It responds to the the "exit"
 * action
 * @author vvivien
 */
public class ExitCmd implements Command {
    private static final String NAMESPACE = "syscmd";
    private static final String ACTION_NAME = "exit";

    @Override
    public Object execute(Context ctx) {
        System.exit(0);
        return null;
    }

    @Override
    public void plug(Context plug) {
        // nothing to setup
    }
    
    @Override
    public Command.Descriptor getDescriptor(){
        return new Command.Descriptor() {
            @Override public String getNamespace() {return NAMESPACE;}
            
            @Override
            public String getName() {
                return ACTION_NAME;
            }

            @Override
            public String getDescription() {
               return "Exits ClamShell.";
            }

            @Override
            public String getUsage() {
                return "Type 'exit'";
            }

            @Override
            public Map<String, String> getArguments() {
                return Collections.emptyMap();
            }
        };
    }
    
}
