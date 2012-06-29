/*
 * #%L
 * clamshell-api
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
package org.clamshellcli.api;

import java.util.Map;

/**
 * The Command component is there to allow Controllers to delegate tasks.
 * Each command exposes a textual id.  This can be used to identify the action
 * (request) that will invoke the execute() method on that command.
 * @author vladimir.vivien
 */
public interface Command extends Plugin {
    /**
     * This method returns an instance of Command.Descritpor.
     * The descriptor is meta information about the command.  The descriptor
     * would also be a good place to describe and keep track of expected 
     * parameters for the command.
     * @return 
     */
    public Command.Descriptor getDescriptor();
    
    /**
     * This method will be called as the starting point to execute the logic
     * for the action mapped to this command.
     * @param ctx
     * @return 
     */
    public Object execute(Context ctx);
    
    /**
     * An interface that can be used to describe the the functionality of the 
     * command implementation.  This is a very important concept in a tex-driven
     * environment such as a command-line user interface.
     * Implementation of this class should use JCommander (http://jcommander.org)
     * to implement command-line argument handlers.
     */
    public static interface Descriptor {
        /**
         * The purpose of the namespace is to provide an identifier to group
         * commands without relying on class name or other convoluted approaches
         * to group commands.
         * @return the command's namespace
         */
        public String getNamespace();
        
        /**
         * Implementation of this method should return a simple string (with no spaces)
         * that identifies the action mapped to this command.
         * @return the name of the action mapped to this command.
         */
        public String getName();      
        
        /**
         * This method should return a descriptive text about the command 
         * it is attached to.
         * @return 
         */
        public String getDescription();
        
        
        /**
         * Implementation of this method should return helpful hint on how
         * to use the associated command and further description of options that
         * are supported by the command.
         * @return 
         */
        public String getUsage();
        
        /**
         * Use this method is to provide a map of the command arguments.
         * @return Map<String,String> key is argument, value = description of arg.
         */
        public Map<String,String> getArguments();
        
        
    }
}
