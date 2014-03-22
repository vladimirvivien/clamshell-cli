/*
 * Copyright 2014 ClamShell-Cli.
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.clamshellcli.api.Command;

/**
 *
 * @author vladimir.vivien
 */
public class CmdUtil{
    /**
     * Returns a map of [cmd-name][cmd args[]]
     * @param commands
     * @return 
     */
    public static Map<String,List<String>> extractCommandHints (List<Command> commands) {
        Map<String, List<String>> result = new TreeMap<String,List<String>>();
        
        for (Command cmd : commands) {
            if (cmd.getDescriptor() != null){
                String cmdStr = cmd.getDescriptor().getName();
                result.put(cmdStr, extractArgs(cmd));
            }
        }
        return result;
    }
    
    private static List<String> extractArgs(Command cmd) {
        if (cmd.getDescriptor() == null || cmd.getDescriptor().getArguments() == null) {
            return null;
        }
        List<String> args = new ArrayList<String>(cmd.getDescriptor().getArguments().size());
        for (Map.Entry<String,String> e : cmd.getDescriptor().getArguments().entrySet()){
            args.add(e.getKey());
        }
        
        return args;
    }
    
    /**
     * Builds a list of jLine completers using the hints map from {@link extractCommandHints}
     * @param hints
     * @return 
     */
    public static List<Completer> getHintsAsCompleters(Map<String,List<String>> hints) {
        List<Completer> completors = new ArrayList<Completer>(hints.size());
        for (Map.Entry<String, List<String>> hint : hints.entrySet()){
            List<String> argList = hint.getValue();
            argList.add(0, hint.getKey());
            completors.add(new StringsCompleter(argList));
        }
        return completors;
    }
}
