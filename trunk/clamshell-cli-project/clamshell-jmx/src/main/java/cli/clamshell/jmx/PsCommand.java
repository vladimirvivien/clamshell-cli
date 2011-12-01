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
package cli.clamshell.jmx;

import cli.clamshell.api.Command;
import cli.clamshell.api.Context;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author vvivien
 */
public class PsCommand implements Command{
    private static final String NAMESPACE = "jmx";
    private static final String CMD_NAME  = "ps";
    private Command.Descriptor descriptor;
    
    public Descriptor getDescriptor() {
        return (descriptor != null) ? 
            descriptor : (
                descriptor = new Command.Descriptor() {

                public String getNamespace() {
                    return NAMESPACE;
                }

                public String getName() {
                    return CMD_NAME;
                }

                public String getDescription() {
                    return "Displays the list of running JVM processes.";
                }

                public String getUsage() {
                    return "ps [options]%n";
                }

                Map<String,String> args;
                public Map<String, String> getArguments() {
                    if(args != null) return args;
                    args = new HashMap<String,String>();
                    args.put("host:<hostUrl>", "Specifies a remote host url.");
                    args.put("args:v", "Arguments for verbose output");
                    return args;
                }
            }
        );
    }

    public Object execute(Context ctx) {
        System.out.println ("Cmd Received!");
        return  null;
    }

    public void plug(Context plug) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
