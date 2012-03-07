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
 * A Prompt is responsible for generating the prompt in that appears
 * in the console.  Every time prompt is displayed, the loaded prompt will be
 * displayed using the getValue() method.
 * @author vladimir.vivien
 */
public interface Prompt extends Plugin{
    /**
     * Implementation of this method should return the current prompt value.
     * Keep in mind that this is called every time the console displays a 
     * prompt.  So, you may choose to provide a cached value for performance if
     * your prompt takes a while to calculate.
     * @param ctx Instance of Context
     * @return the value for the prompt
     */
    public String getValue(Context ctx);
}
