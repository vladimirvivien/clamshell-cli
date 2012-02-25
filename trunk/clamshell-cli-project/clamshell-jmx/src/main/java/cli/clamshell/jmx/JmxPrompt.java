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
package cli.clamshell.jmx;

import cli.clamshell.api.Context;
import cli.clamshell.api.Prompt;

/**
 *
 * @author vvivien
 */
public class JmxPrompt implements Prompt{
    private static final String PROMPT = "jmx-cli > ";
    public String getValue(Context ctx) {
        return PROMPT;
    }

    public void plug(Context plug) {
        // nothing to do
    }
    
}
