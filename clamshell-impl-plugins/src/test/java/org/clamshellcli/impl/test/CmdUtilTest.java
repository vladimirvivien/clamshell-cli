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
    public void testExtractCommandInfo() {
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(new MockCommand());
        Map<String, String[]> info = CmdUtil.extractCommandInfo(cmds);
        
        Assert.assertNotNull("Expected command extraction null", info);
        Assert.assertTrue(info.keySet().contains("mock"));
        Assert.assertTrue(info.get("mock").length == 7);
    }
    
}
