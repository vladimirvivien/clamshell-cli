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

import java.util.regex.Pattern;

/**
 * The role of the InputController component is to take a input from the command line
 * and interpret it accordingly.  A simple implementation may include all logic, 
 * however, more sophisticated implementations may delegate workload to Command 
 * objects.
 * @author vladimir.vivien
 */
public interface InputController extends Plugin{
    /**
     * This method is invoked when there is an input from the console to be interpreted.
     * The input value is passed to the controller via the context instance 
     * as Context.KEY_COMMAND_LINE_INPUT.  Implementors should return a boolean
     * indicating if the controller handled the input.
     * 
     * @param ctx instance of Context
     * @return true - if handled by the controller, false if not.
     */
    public boolean handle(Context ctx);
    
    /**
     * This method returns a pre-compiled instance of the command-line pattern
     * that the controller responds to.  This mechanism provides a command
     * filter for the controller avoiding unnecessary handle() calls.
     * @return 
     */
    public Pattern respondsTo();
    
    /**
     * A method to set command-line pattern this controller handles.
     * @param p 
     */
    public void setInputPattern(Pattern p);
        
    /**
     * A flag that indicates if the controller is enabled and should participate
     * in input control.
     * @return 
     */
    public Boolean isEnabled();
    
    /**
     * Sets enabled-flag.  When true, controller will receive handling call.
     * @param flag 
     */
    public void setEnabled(Boolean flag);
}
