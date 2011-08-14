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
package cli.clamshell.api;

/**
 * The role of the Controller component is to take a input from the command line
 * and decode it accordingly.  Simple implementations may do everything, however,
 * more sophisticated implementations may delegate workload to Command objects.
 * @author vladimir.vivien
 */
public interface Controller extends Plugin{
    /**
     * This is invoked when there is an input from the console to be interpreted.
     * The input value is passed in the context as Context.KEY_COMMAND_LINE_INPUT
     * @param ctx instance of Context
     */
    public void handle(Context ctx);
}
