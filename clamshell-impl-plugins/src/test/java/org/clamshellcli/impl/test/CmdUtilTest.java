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

package org.clamshellcli.impl.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.clamshellcli.api.Command;
import org.clamshellcli.impl.CmdUtil;
import org.clamshellcli.test.MockCommand;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author vladimir
 */
public class CmdUtilTest {
    
    public CmdUtilTest() {}
    
    @Test
    public void testExtractCommandHints() {
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(new MockCommand());
        Map<String, List<String>> hints = CmdUtil.extractCommandHints(cmds);
        
        Assert.assertNotNull("Expected command extraction null", hints);
        Assert.assertTrue(hints.keySet().contains("mock"));
        Assert.assertTrue(hints.get("mock").size() == 7);
    }
    
    public void testGetHintsAsCompleters() {
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(new MockCommand());
        Map<String, List<String>> hints = CmdUtil.extractCommandHints(cmds);
        List<Completer> completers = CmdUtil.getHintsAsCompleters(hints);
        
        Assert.assertNotNull(completers);
        Assert.assertTrue(completers.size() == 1);
        
        StringsCompleter strComp = (StringsCompleter) completers.get(0);
        Assert.assertTrue(strComp.getStrings().size() == 8); // cmd + args
    }
    
}
