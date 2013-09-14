/*
 * Copyright 2013 ClamShell-Cli.
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

import org.clamshellcli.api.Context;
import org.clamshellcli.api.Prompt;

/**
 * Default Prompt implementation.
 * Will be used if none is found on classpath.
 * @author Vladimir Vivien
 */
public class DefaultPrompt implements Prompt {

    private final String PROMPT = System.getProperty("user.name") + "> ";

    @Override
    public String getValue(Context ctx) {
        return PROMPT;
    }

    public void plug(Context plug) {
    }
}
