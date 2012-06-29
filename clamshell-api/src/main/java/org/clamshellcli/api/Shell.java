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

/**
 * This is the core component.  Its job is to instantiate all other components.
 * It functions as a kernel for all other components loaded in the system.
 * @author vladimir.vivien
 */
public interface Shell extends Plugin{
    /**
     * Called when shell is running is pass-through mode.
     * In pass-through, the launcher will call this method only and not start
     * interactive mode through the normal startup sequence.
     * The command-line values will be passed in the context via key
     * Context.KEY_COMMAND_LINE_INPUT
     * @param ctx 
     */
    public void exec(Context ctx);
}
