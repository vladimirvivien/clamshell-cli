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
import org.clamshellcli.api.Command;

/**
 *
 * @author vladimir
 */
public class CmdUtil{
    /**
     * Returns a 2d array of [cmd-name][cmd args]
     * @param commands
     * @return 
     */
    public static Map<String,String[]> extractCommandInfo (List<Command> commands) {
        Map<String, String[]> result = new TreeMap<String,String[]>();
        
        for (Command cmd : commands) {
            if (cmd.getDescriptor() != null){
                String cmdStr = cmd.getDescriptor().getName();
                String[] args = extractArgs(cmd);
                result.put(cmdStr, args);
            }
        }
        return result;
    }
    
    private static String[] extractArgs(Command cmd) {
        if (cmd.getDescriptor() == null || cmd.getDescriptor().getArguments() == null) {
            return null;
        }
        List<String> args = new ArrayList<String>(cmd.getDescriptor().getArguments().size());
        for (Map.Entry<String,String> e : cmd.getDescriptor().getArguments().entrySet()){
            args.add(e.getKey());
        }
        
        return args.toArray(new String[args.size()]);
    }
}
