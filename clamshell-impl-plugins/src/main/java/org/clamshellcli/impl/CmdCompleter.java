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

import java.util.List;
import java.util.Map;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.clamshellcli.api.Command;

/**
 * A custom JLine.Completor for available commands.
 * @author vladimir.vvien
 */
public class CmdCompleter implements Completer{
    StringsCompleter cmdNamesCompleter;
    Map<String,List<String>> cmdHints;
    
    public CmdCompleter(List<Command> cmds){
        cmdHints = CmdUtil.extractCommandHints(cmds);
    }
    
    @Override
    public int complete(String input, int cursor, List<CharSequence> result) {
        String cmd = input != null ? input.substring(0, cursor) : "";
        // display all avail cmds
        if (cmd.isEmpty() && cmdHints != null) {
            for (Map.Entry<String,List<String>> e : cmdHints.entrySet()) {
                result.add(e.getKey());
            }
        }
        // look for commands that match input
        if (!cmd.isEmpty() && cmdHints != null) {            
            for (Map.Entry<String,List<String>> e : cmdHints.entrySet()){
                // exact match: display arcuments, exit.
                if (e.getKey().equals(input)){
                    result.addAll(e.getValue());
                    break;
                }
                
                // display all partial match
                if (e.getKey().startsWith(input)){
                    result.add(e.getKey());
                }
            }
        }
        
        return result.isEmpty() ? -1 : cmd.length() + 1;
    }
}
